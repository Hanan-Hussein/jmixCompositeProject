package com.company.subproject.security.specific;

import io.jmix.core.accesscontext.SpecificOperationAccessContext;

public class Maker extends SpecificOperationAccessContext {

    public static final String NAME = "maker";

    public Maker() {
        super(NAME);
    }
}
