package com.maojianwei.h3c.api;

public enum MaoH3cPortDirection { // value from port-limit-rate
    in(0),
    out(1);

    private final int value;
    MaoH3cPortDirection(int value) {
        this.value = value;
    }
    public int value() {
        return value;
    }
}
