package org.goafabric.core.ui;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.context.annotation.Configuration;

@Configuration
@Theme(value = "default")
public class VaadinConfiguration implements AppShellConfigurator {
}
