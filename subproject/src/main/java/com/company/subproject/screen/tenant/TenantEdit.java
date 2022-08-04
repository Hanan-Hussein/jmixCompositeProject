package com.company.subproject.screen.tenant;

import io.jmix.ui.screen.*;
import com.company.subproject.entity.Tenant;

@UiController("Tenant.edit")
@UiDescriptor("tenant-edit.xml")
@EditedEntityContainer("tenantDc")
public class TenantEdit extends StandardEditor<Tenant> {
}