// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.api.command.admin.network;

import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.network.Network;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ResponseObject.ResponseView;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.command.user.network.CreateNetworkCmd;
import org.apache.cloudstack.api.response.NetworkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "createNetwork", description = "Creates a network", responseObject = NetworkResponse.class, responseView = ResponseView.Full, entityType = {Network.class},
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class CreateNetworkCmdByAdmin extends CreateNetworkCmd {
    public static final Logger s_logger = LoggerFactory.getLogger(CreateNetworkCmdByAdmin.class.getName());

    @Parameter(name=ApiConstants.VLAN, type=CommandType.STRING, description="the ID or VID of the network")
    private String vlan;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getVlan() {
        return vlan;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    // an exception thrown by createNetwork() will be caught by the dispatcher.
    public void execute() throws InsufficientCapacityException, ConcurrentOperationException, ResourceAllocationException{
        Network result = _networkService.createGuestNetwork(this);
        if (result != null) {
            NetworkResponse response = _responseGenerator.createNetworkResponse(ResponseView.Full, result);
            response.setResponseName(getCommandName());
            setResponseObject(response);
        }else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to create network");
        }
    }
}
