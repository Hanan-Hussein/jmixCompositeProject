package com.company.subproject.security;

import com.company.subproject.entity.User;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

@ResourceRole(name = "CheckerRole", code = "checker-role")
public interface CheckerRole {
    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.CREATE, EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void user();

    @SpecificPolicy(resources = "checker")
    void specific();

    @MenuPolicy(menuIds = {"User.browse", "entityInspector.browse", "userSessions.browse"})
    @ScreenPolicy(screenIds = {"LoginScreen", "User.browse", "User.edit", "MainScreen", "snapshotDiff", "entityInspector.browse", "entityInspector.edit", "sec_UserSubstitutionEntity.edit", "sec_UserSubstitutionsFragment", "sec_UserSubstitutionsScreen", "userSessions.browse", "selectValueDialog", "inputDialog"})
    void screens();
}