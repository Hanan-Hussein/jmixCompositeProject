package com.company.subproject.screen.tenant;

import io.jmix.ui.screen.*;
import com.company.subproject.entity.Tenant;

@UiController("Tenant.browse")
@UiDescriptor("tenant-browse.xml")
@LookupComponent("tenantsTable")
public class TenantBrowse extends StandardLookup<Tenant> {
}