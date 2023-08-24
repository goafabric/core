package org.goafabric.core.ui.practice;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.goafabric.core.ui.MainView;
import org.goafabric.core.ui.adapter.OrganizationAdapter;
import org.goafabric.core.ui.adapter.PractitionerAdapter;
import org.goafabric.core.ui.adapter.RoleAdapter;
import org.goafabric.core.ui.adapter.UserAdapter;
import org.goafabric.core.ui.practice.tabs.OrganizationView;
import org.goafabric.core.ui.practice.tabs.PractitionerView;
import org.goafabric.core.ui.practice.tabs.RoleView;
import org.goafabric.core.ui.practice.tabs.UserView;

@Route(value = "practice", layout = MainView.class)
@PageTitle("Practice")
public class PracticeView extends VerticalLayout {

    public PracticeView(
            PractitionerAdapter practitionerAdapter, OrganizationAdapter organizationAdapter,
            UserAdapter userAdapter, RoleAdapter roleAdapter) {
        this.setSizeFull();

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        tabSheet.add("Practitioner", new PractitionerView(practitionerAdapter));
        tabSheet.add("Organization", new OrganizationView(organizationAdapter));
        tabSheet.add("User", new UserView(userAdapter));
        tabSheet.add("Role", new RoleView(roleAdapter));

        add(tabSheet);
    }


}