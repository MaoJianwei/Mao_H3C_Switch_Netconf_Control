package com.maojianwei.h3c.api;

public interface MaoH3cNetconfService {


    String setPortLimitRate(int ifIndex, MaoH3cPortDirection direction, int rate, int burst);

    String setPortPriority(int ifIndex, MaoH3cTrustMode trustMode, int priority);


    // just for debug
    String pushDebugNetconf(String cmdXML);
}
