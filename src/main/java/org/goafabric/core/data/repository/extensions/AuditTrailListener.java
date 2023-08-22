package org.goafabric.core.data.repository.extensions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import org.goafabric.core.extensions.HttpInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Simple Audittrail that fulfills the requirements of logging content changes + user + aot support, could be db independant
public class AuditTrailListener implements ApplicationContextAware {
    private static ApplicationContext context;

    enum DbOperation { CREATE, READ, UPDATE, DELETE }

    private record AuditTrail(
            String id,
            String orgunitId,
            String objectType,
            String objectId,
            DbOperation operation,
            String createdBy,
            Date createdAt,
            String modifiedBy,
            Date   modifiedAt,
            String oldValue,
            String newValue
    ) {}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }


    @PostPersist
    public void afterCreate(Object object)  {
        context.getBean(AuditProcessor.class).afterCreate(object);
    }

    @PostUpdate
    public void afterUpdate(Object object) {
        context.getBean(AuditProcessor.class).afterUpdate(object);
    }

    @PostRemove
    public void afterDelete(Object object) {
        context.getBean(AuditProcessor.class).afterDelete(object);
    }


    @Component
    @Profile("jpa")
    @RegisterReflectionForBinding(AuditTrail.class)
    static class AuditProcessor {
        private final DataSource dataSource;
        private final String     schemaPrefix;
        @PersistenceContext private EntityManager entityManager;
        private static final Logger log = LoggerFactory.getLogger("org.goafabric.AuditProcessor");

        private final RestTemplate auditRestTemplate;
        private final String eventDispatcherUri;
        private static final ExecutorService executor = Executors.newFixedThreadPool(3);

        public AuditProcessor(DataSource dataSource, @Value("${multi-tenancy.schema-prefix:_}") String schemaPrefix,
                              RestTemplate auditRestTemplate, @Value("${event.dispatcher.uri:}") String eventDispatcherUri) {
            this.dataSource = dataSource;
            this.schemaPrefix = schemaPrefix;
            this.auditRestTemplate = auditRestTemplate;
            this.eventDispatcherUri = eventDispatcherUri;
        }

        public void afterCreate(Object object)  {
            context.getBean(AuditProcessor.class).insertAudit(DbOperation.CREATE,  getId(object), null, object);
        }

        public void afterUpdate(Object object) {
            final String id = getId(object);
            context.getBean(AuditProcessor.class).insertAudit(DbOperation.UPDATE, id,
                    findOldObject(object.getClass(), id), object);
        }

        public void afterDelete(Object object) {
            context.getBean(AuditProcessor.class).insertAudit(DbOperation.DELETE, getId(object), object, null);
        }

        private void insertAudit(final DbOperation operation, String referenceId, final Object oldObject, final Object newObject) {
            try {
                var auditTrail = createAuditTrail(operation, referenceId, oldObject, newObject);
                log.debug("New audit:\n{}", auditTrail);
                insertAudit(auditTrail, oldObject != null ? oldObject : newObject);
                //dispatchEvent(auditTrail);
            } catch (Exception e) {
                log.error("Error during audit:\n{}", e.getMessage(), e);
            }
        }

        private AuditTrail createAuditTrail(
                DbOperation dbOperation, String referenceId, final Object oldObject, final Object newObject) throws JsonProcessingException {
            final Date date = new Date(System.currentTimeMillis());
            return new AuditTrail(
                    UUID.randomUUID().toString(),
                    TenantResolver.getOrgunitId(),
                    getTableName(newObject != null ? newObject : oldObject),
                    referenceId,
                    dbOperation,
                    (dbOperation == DbOperation.CREATE ? HttpInterceptor.getUserName() : null),
                    (dbOperation == DbOperation.CREATE ? date : null),
                    ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? HttpInterceptor.getUserName() : null),
                    ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? date : null),
                    (oldObject == null ? null : getJsonValue(oldObject)),
                    (newObject == null ? null : getJsonValue(newObject))
            );
        }

        private String getJsonValue(final Object object) throws JsonProcessingException {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }

        public void insertAudit(AuditTrail auditTrail, Object object) { //we cannot use jpa because of the dynamic table name
            new SimpleJdbcInsert(dataSource)
                    .withSchemaName(schemaPrefix + HttpInterceptor.getTenantId())
                    .withTableName("audit_trail")
                .execute(new BeanPropertySqlParameterSource(auditTrail));
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW) //new transaction helps us to retrieve the old value still inside the db
        public <T> T findOldObject(Class<T> clazz, String id) {
            return entityManager.find(clazz, id);
        }

        private static String getId(Object object) {
            return String.valueOf(context.getBean(EntityManagerFactory.class).getPersistenceUnitUtil().getIdentifier(object));
        }

        private static String getTableName(Object object) {
            return object.getClass().getSimpleName().replaceAll("Eo", "").toLowerCase();
        }

        /*
        public void dispatchEvent(AuditTrailListener.AuditTrail auditTrail) {
            if (!eventDispatcherUri.isEmpty()) {
                var changeEvent = new ChangeEvent(auditTrail.id(), HttpInterceptor.getTenantId(), auditTrail.objectId(), auditTrail.objectType(), auditTrail.operation(), "core");
                var headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                executor.submit(() -> {
                    auditRestTemplate.postForEntity(eventDispatcherUri, new HttpEntity<>(changeEvent, headers), Void.class); });
            }
        }

        @Bean
        public RestTemplate auditRestTemplate(RestTemplateBuilder builder) {
            return builder.setConnectTimeout(Duration.ofMillis(1000)).setReadTimeout(Duration.ofMillis(1000)).build();
        }

        record ChangeEvent (String id, String tenantId, String referenceId, String type, AuditTrailListener.DbOperation operation, String origin) {}

         */
    }

}



