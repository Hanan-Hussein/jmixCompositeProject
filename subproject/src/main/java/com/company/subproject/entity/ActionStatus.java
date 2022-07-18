package com.company.subproject.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum ActionStatus implements EnumClass<String> {

    CREATION("Creation"),
    MODIFICATION("Update");

    private String id;

    ActionStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ActionStatus fromId(String id) {
        for (ActionStatus at : ActionStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}