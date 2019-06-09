/*
 * Copyright 2014-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maojianwei.h3c.ctl;

import com.maojianwei.h3c.api.MaoH3cNetconfService;
import com.maojianwei.h3c.api.MaoH3cPortDirection;
import com.maojianwei.h3c.api.MaoH3cTrustMode;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.net.Device;
import org.onosproject.net.behaviour.ConfigSetter;
import org.onosproject.net.device.DeviceService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;

import static com.maojianwei.h3c.ctl.MaoH3cMessageCodec.COMMAND_SUCCESS;
import static com.maojianwei.h3c.ctl.MaoH3cMessageCodec.checkReplySuccess;
import static com.maojianwei.h3c.ctl.MaoH3cMessageCodec.getPortLimitRateMsg;
import static com.maojianwei.h3c.ctl.MaoH3cMessageCodec.getPortPriorityMsg;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Core of Mao H3C Netconf Control Center.
 */
@Component(immediate = true)
@Service(value = MaoH3cNetconfService.class)
public class MaoH3cNetconfImpl implements MaoH3cNetconfService {

    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private DeviceService deviceService;

    @Activate
    public void activate(ComponentContext context) {
        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        log.info("Stopped");
    }



    @Override
    public String pushDebugNetconf(String cmdXML) {

        Device device = deviceService.getDevices().iterator().next();

        ConfigSetter h3cNetconfDriver = device.as(ConfigSetter.class);

        try {
            return h3cNetconfDriver.setConfiguration(cmdXML);
        } catch (Exception e) {
            log.error("Mao Fail to push, {}, {}, {}\n{}", e.getClass().getCanonicalName(), e.getMessage(), e.getCause(), cmdXML);
            return "Mao pushDebugNetconf Fail with " + e.getClass().getCanonicalName();
        }
//        log.info("Mao H3C Netconf Reply:\n{}", reply);
    }

    @Override
    public String setPortLimitRate(int ifIndex, MaoH3cPortDirection direction, int rate, int burst) {

        String msg = getPortLimitRateMsg(ifIndex, direction, rate, burst);

        String reply = pushDebugNetconf(msg);
        return checkReplySuccess(reply) ? COMMAND_SUCCESS : reply;
    }

    @Override
    public String setPortPriority(int ifIndex, MaoH3cTrustMode trustMode, int priority) {

        String msg = getPortPriorityMsg(ifIndex, trustMode, priority);

        String reply = pushDebugNetconf(msg);
        return checkReplySuccess(reply) ? COMMAND_SUCCESS : reply;
    }
}
