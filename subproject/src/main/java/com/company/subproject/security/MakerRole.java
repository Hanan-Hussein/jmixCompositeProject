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

@ResourceRole(name = "MakerRole", code = "maker-role")
public interface MakerRole {
    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.CREATE, EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void user();

    @MenuPolicy(menuIds = "User.browse")
    @ScreenPolicy(screenIds = {"User.browse", "User.edit"})
    void screens();

    @SpecificPolicy(resources = "maker")
    void specific();
}