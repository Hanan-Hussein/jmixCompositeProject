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
}