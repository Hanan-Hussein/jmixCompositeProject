package com.company.subproject.entity;

import io.jmix.core.metamodel.datatype.impl.EnumClass;

import javax.annotation.Nullable;


public enum ApproveStatus implements EnumClass<String> {

    APPROVED("Approved"),
    NOT_APPROVED("Un_approved"),
    DECLINED("Declined");

    private String id;

    ApproveStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ApproveStatus fromId(String id) {
        for (ApproveStatus at : ApproveStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}