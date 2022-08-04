package com.company.subproject.listener;

import com.company.subproject.entity.User;
import io.jmix.core.DataManager;

import io.jmix.core.EntitySerialization;
import io.jmix.core.Metadata;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component

public class UserEventListener {
    @Autowired
    private Metadata metadata;

    @Autowired
    private DataManager dataManager;
    @Autowired
    private EntitySerialization entitySerialization;

    @EventListener
    public void onUserSaving(EntitySavingEvent<User> event) {
        
    }
//    private ApplicationUser backoffice(User user) {
//        ApplicationUser applicationUser = metadata.create(ApplicationUser.class);
//        applicationUser.setUserName(user.getUsername());
//        applicationUser.setPreferredName(user.getFirstName());
//        applicationUser.setEmail(user.getEmail());
//        applicationUser.setActive(user.getActive());
////        final List<Role> userRoles = user.getUserRoles().stream().map(UserRole::getRole).collect(Collectors.toList());
////        applicationUser.setUserRoles(userRoles);
//        applicationUser.setFirstName(user.getFirstName());
//        applicationUser.setMiddleName(user.getLastName());
//        applicationUser.setSurname(user.getLastName());
//        return applicationUser;
//    }
//

   


}