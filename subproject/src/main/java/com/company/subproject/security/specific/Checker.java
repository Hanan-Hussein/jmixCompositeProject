package com.company.subproject.security.specific;

import io.jmix.core.accesscontext.SpecificOperationAccessContext;

public class Checker extends SpecificOperationAccessContext {

    public static final String NAME = "checker";

    public Checker() {
        super(NAME);
    }
}
