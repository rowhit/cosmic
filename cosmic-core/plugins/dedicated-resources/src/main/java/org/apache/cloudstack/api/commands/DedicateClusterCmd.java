package org.apache.cloudstack.api.commands;

import com.cloud.dc.DedicatedResources;
import com.cloud.event.EventTypes;
import com.cloud.user.Account;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseAsyncCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.ClusterResponse;
import org.apache.cloudstack.api.response.DedicateClusterResponse;
import org.apache.cloudstack.api.response.DomainResponse;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.dedicated.DedicatedService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "dedicateCluster", description = "Dedicate an existing cluster", responseObject = DedicateClusterResponse.class,
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class DedicateClusterCmd extends BaseAsyncCmd {
    public static final Logger s_logger = LoggerFactory.getLogger(DedicateClusterCmd.class.getName());

    private static final String s_name = "dedicateclusterresponse";
    @Inject
    DedicatedService dedicatedService;

    @Parameter(name = ApiConstants.CLUSTER_ID, type = CommandType.UUID, entityType = ClusterResponse.class, required = true, description = "the ID of the Cluster")
    private Long clusterId;

    @Parameter(name = ApiConstants.DOMAIN_ID,
            type = CommandType.UUID,
            entityType = DomainResponse.class,
            required = true,
            description = "the ID of the containing domain")
    private Long domainId;

    @Parameter(name = ApiConstants.ACCOUNT, type = CommandType.STRING, description = "the name of the account which needs dedication. Must be used with domainId.")
    private String accountName;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getEventType() {
        return EventTypes.EVENT_DEDICATE_RESOURCE;
    }

    @Override
    public String getEventDescription() {
        return "dedicating a cluster";
    }

    @Override
    public void execute() {
        final List<? extends DedicatedResources> result = dedicatedService.dedicateCluster(getClusterId(), getDomainId(), getAccountName());
        final ListResponse<DedicateClusterResponse> response = new ListResponse<>();
        final List<DedicateClusterResponse> clusterResponseList = new ArrayList<>();
        if (result != null) {
            for (final DedicatedResources resource : result) {
                final DedicateClusterResponse clusterResponse = dedicatedService.createDedicateClusterResponse(resource);
                clusterResponseList.add(clusterResponse);
            }
            response.setResponses(clusterResponseList);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to dedicate cluster");
        }
    }

    public Long getClusterId() {
        return clusterId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public String getAccountName() {
        return accountName;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////
    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }
}
