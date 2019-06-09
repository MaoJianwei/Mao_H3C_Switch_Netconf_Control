package com.maojianwei.h3c.ctl;

import com.maojianwei.h3c.api.MaoH3cPortDirection;
import com.maojianwei.h3c.api.MaoH3cTrustMode;

import java.util.concurrent.atomic.AtomicLong;

import static com.maojianwei.h3c.api.MaoH3cPortDirection.in;
import static com.maojianwei.h3c.api.MaoH3cPortDirection.out;
import static com.maojianwei.h3c.api.MaoH3cTrustMode.DSCP;
import static com.maojianwei.h3c.api.MaoH3cTrustMode.Dot1p;
import static com.maojianwei.h3c.api.MaoH3cTrustMode.Untrust;


/**
 * Codec for H3C NETCONF message.
 */
public final class MaoH3cMessageCodec {


    public static final String COMMAND_SUCCESS = "OK";

    private static AtomicLong msgId = new AtomicLong(1);

    public static boolean checkReplySuccess(String deviceReply) {
        return deviceReply.contains("<ok/>");
    }


    private static final String PORT_LIMIT_RATE_PATTERN =
            "<rpc message-id='[msgId]' xmlns='urn:ietf:params:xml:ns:netconf:base:1.0' xmlns:web='urn:ietf:params:xml:ns:netconf:base:1.0'>\n" +
                    "    <edit-config>\n" +
                    "        <target>\n" +
                    "            <running/>\n" +
                    "        </target>\n" +
                    "        <config>\n" +
                    "            <top xmlns='http://www.h3c.com/netconf/config:1.0' web:operation='merge'>\n" +
                    "                <LR>\n" +
                    "                    <Interfaces>\n" +
                    "                        <Interface>\n" +
                    "                            <IfIndex>[IfIndex]</IfIndex>\n" +
                    "                            <Direction>[Direction]</Direction>\n" +
                    "                            <CIR>[CIR]</CIR>\n" +
                    "                            <CBS>[CBS]</CBS>\n" +
                    "                        </Interface>\n" +
                    "                    </Interfaces>\n" +
                    "                </LR>\n" +
                    "            </top>\n" +
                    "        </config>\n" +
                    "    </edit-config>\n" +
                    "</rpc>";

    public static String getPortLimitRateMsg(int ifIndex, MaoH3cPortDirection direction, int rate, int burst) {
        return PORT_LIMIT_RATE_PATTERN
                .replace("[msgId]", String.valueOf(msgId.getAndIncrement()))
                .replace("[IfIndex]", String.valueOf(ifIndex))
                .replace("[Direction]", direction.equals(in) ? String.valueOf(in.value()) : String.valueOf(out.value()))
                .replace("[CIR]", String.valueOf(rate))
                .replace("[CBS]", String.valueOf(burst));
    }


    private static final String PORT_PRIORITY =
            "<rpc message-id='[msgId]' xmlns='urn:ietf:params:xml:ns:netconf:base:1.0' xmlns:web='urn:ietf:params:xml:ns:netconf:base:1.0'>\n" +
                    "    <edit-config>\n" +
                    "        <target>\n" +
                    "            <running/>\n" +
                    "        </target>\n" +
                    "        <config>\n" +
                    "            <top xmlns='http://www.h3c.com/netconf/config:1.0' web:operation='merge'>\n" +
                    "                <PRIMAP>\n" +
                    "                    <PortPriority>\n" +
                    "                        <Port>\n" +
                    "                            <IfIndex>[IfIndex]</IfIndex>\n" +
                    "                            <Priority>[Priority]</Priority>\n" +
                    "                        </Port>\n" +
                    "                    </PortPriority>\n" +
                    "                    <PortTrustMode>\n" +
                    "                        <Port>\n" +
                    "                            <IfIndex>[IfIndex]</IfIndex>\n" +
                    "                            <TrustMode>[TrustMode]</TrustMode>\n" +
                    "                        </Port>\n" +
                    "                    </PortTrustMode>\n" +
                    "                </PRIMAP>\n" +
                    "            </top>\n" +
                    "        </config>\n" +
                    "    </edit-config>\n" +
                    "</rpc>";

    public static String getPortPriorityMsg(int ifIndex, MaoH3cTrustMode trustMode, int priority) {
        return PORT_PRIORITY
                .replace("[msgId]", String.valueOf(msgId.getAndIncrement()))
                .replace("[IfIndex]", String.valueOf(ifIndex))
                .replace("[IfIndex]", String.valueOf(ifIndex)) // two elements of [IfIndex]
                .replace("[Priority]", String.valueOf(priority))
                .replace("[TrustMode]",
                        trustMode.equals(Untrust) ? String.valueOf(Untrust.value()) :
                        (trustMode.equals(Dot1p) ? String.valueOf(Dot1p.value()) : String.valueOf(DSCP.value()))
                );
    }
}
