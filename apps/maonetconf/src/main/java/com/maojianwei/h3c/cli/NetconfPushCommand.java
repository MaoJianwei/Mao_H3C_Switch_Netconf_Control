/*
 * Copyright 2017-present Open Networking Foundation
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
package com.maojianwei.h3c.cli;
import com.maojianwei.h3c.api.MaoH3cNetconfService;
import com.maojianwei.h3c.ctl.MaoH3cNetconfImpl;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.apache.karaf.shell.commands.Argument;

/**
 * Mao Jianwei.
 */
@Command(scope = "onos", name = "mao-h3c-netconf-push",
        description = "mao-h3c-netconf-push")
public class NetconfPushCommand extends AbstractShellCommand {

    @Argument(required = true)
    private String cmdXML;

    @Override
    protected void execute() {
        MaoH3cNetconfService maoH3cNetconfService = AbstractShellCommand.get(MaoH3cNetconfService.class);
        maoH3cNetconfService.pushDebugNetconf(cmdXML);
    }
}
