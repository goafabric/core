import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

val group: String by project
val version: String by project
java.sourceCompatibility = JavaVersion.VERSION_21

val dockerRegistry = "goafabric"
val nativeBuilder = "paketobuildpacks/java-native-image:10.1.0"
val baseImage = "ibm-semeru-runtimes:open-21.0.4.1_7-jre-focal@sha256:8b94f8b14fd1d4660f9dc777b1ad3630f847b8e3dc371203bcb857a5e74d6c39"

plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.graalvm.buildtools.native") version "0.10.3"

	id("com.google.cloud.tools.jib") version "3.4.4"
	id("net.researchgate.release") version "3.0.2"
	id("org.sonarqube") version "5.1.0.4882"

	id("org.cyclonedx.bom") version "1.10.0"
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	constraints {
		annotationProcessor("org.mapstruct:mapstruct-processor:1.6.2")
		implementation("org.mapstruct:mapstruct:1.6.2")
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
		implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
		implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.5")
	}
}

dependencies {
	//web
	implementation("org.springframework.boot:spring-boot-starter-web")

	//monitoring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp")
	implementation("net.ttddyy.observation:datasource-micrometer-spring-boot")

	//openapi
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

	//crosscuting
	implementation("org.springframework.boot:spring-boot-starter-aop")

	//code generation
	implementation("org.mapstruct:mapstruct")
	annotationProcessor("org.mapstruct:mapstruct-processor")
	implementation("net.datafaker:datafaker:1.9.0") { exclude("org.yaml", "snakeyaml") }

	//persistence
	implementation("org.springframework.boot:spring-boot-starter-data-jpa") {exclude("org.glassfish.jaxb", "jaxb-runtime")}
	implementation("com.h2database:h2")
	implementation("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	//kafka
	implementation("org.springframework.kafka:spring-kafka")


	//elastic
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	//s3
	implementation("am.ik.s3:simple-s3-client:0.2.2") {exclude("org.springframework", "spring-web")}

	//devtools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	//test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("ca.uhn.hapi.fhir:hapi-fhir-client-okhttp:7.4.5")
	testImplementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:6.10.5")

}

tasks.withType<Test> {
	useJUnitPlatform()
	exclude("**/*NRIT*")
	finalizedBy("jacocoTestReport")
}
tasks.jacocoTestReport { reports {csv.required.set(true); xml.required.set(true) } }

jib {
	val amd64 = com.google.cloud.tools.jib.gradle.PlatformParameters(); amd64.os = "linux"; amd64.architecture = "amd64"; val arm64 = com.google.cloud.tools.jib.gradle.PlatformParameters(); arm64.os = "linux"; arm64.architecture = "arm64"
	from.image = baseImage
	to.image = "${dockerRegistry}/${project.name}:${project.version}"
	container.jvmFlags = listOf("-Xms256m", "-Xmx256m")
	from.platforms.set(listOf(amd64, arm64))
}

tasks.register("dockerImageNative") { group = "build"; dependsOn("bootBuildImage") }
tasks.named<BootBuildImage>("bootBuildImage") {
	val nativeImageName = "${dockerRegistry}/${project.name}-native" + (if (System.getProperty("os.arch").equals("aarch64")) "-arm64v8" else "") + ":${project.version}"
	builder.set("paketobuildpacks/builder-jammy-buildpackless-tiny")
	buildpacks.add(nativeBuilder)
	imageName.set(nativeImageName)
	environment.set(mapOf("BP_NATIVE_IMAGE" to "true", "BP_JVM_VERSION" to "21", "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-J-Xmx6000m -march=compatibility"))
	doLast {
		exec { commandLine("/bin/sh", "-c", "docker run --rm $nativeImageName -check-integrity") }
		exec { commandLine("/bin/sh", "-c", "docker push $nativeImageName") }
	}
}
configure<net.researchgate.release.ReleaseExtension> {
	buildTasks.set(listOf("build", "test", "jib", "dockerImageNative"))
	tagTemplate.set("v${version}".replace("-SNAPSHOT", ""))
}