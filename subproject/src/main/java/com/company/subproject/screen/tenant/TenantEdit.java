package com.company.subproject.screen.tenant;

import io.jmix.ui.action.Action;
import io.jmix.ui.component.TabSheet;
import io.jmix.ui.screen.*;
import com.company.subproject.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Tenant.edit")
@UiDescriptor("tenant-edit.xml")
@EditedEntityContainer("tenantDc")
public class TenantEdit extends StandardEditor<Tenant> {
    @Autowired
    private TabSheet wizardTabSheet;

    @Subscribe("nextAction")
    public void onNextAction(Action.ActionPerformedEvent event) {
        int tabValue = (wizardTabSheet.getTabIndex()) + 1;
        wizardTabSheet.setSelectedTab((TabSheet.Tab) (wizardTabSheet.getTabs().toArray())[tabValue]);
        wizardTabSheet.setTabIndex(tabValue);
    }

    @Subscribe("prevAction")
    public void onPrevAction(Action.ActionPerformedEvent event) {
        int tabValue = (wizardTabSheet.getTabIndex()) - 1;
        wizardTabSheet.setSelectedTab((TabSheet.Tab) (wizardTabSheet.getTabs().toArray())[tabValue]);
        wizardTabSheet.setTabIndex(tabValue);

    }

    @Subscribe("wizardTabSheet")
    public void onWizardTabSheetSelectedTabChange(TabSheet.SelectedTabChangeEvent event) {
        String tabName = event.getSelectedTab().getName();
        switch (tabName) {
            case "tab_1":
                wizardTabSheet.setTabIndex(0);
                break;
            case "tab_2":
                wizardTabSheet.setTabIndex(1);
                break;
            default:
                wizardTabSheet.setTabIndex(2);
                break;

        }
    }
}