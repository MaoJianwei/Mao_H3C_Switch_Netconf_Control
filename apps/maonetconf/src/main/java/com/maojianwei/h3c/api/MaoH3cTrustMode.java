package com.maojianwei.h3c.api;

public enum MaoH3cTrustMode {

    Untrust(1),
    Dot1p(2),
    DSCP(3);

    private final int value;
    MaoH3cTrustMode(int value) {
        this.value = value;
    }
    public int value() {
        return value;
    }
}
