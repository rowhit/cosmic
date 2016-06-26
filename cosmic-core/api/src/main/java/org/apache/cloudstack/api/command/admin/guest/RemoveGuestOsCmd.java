package org.apache.cloudstack.api.command.admin.guest;

import com.cloud.event.EventTypes;
import com.cloud.user.Account;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiCommandJobType;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseAsyncCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.GuestOSResponse;
import org.apache.cloudstack.api.response.SuccessResponse;
import org.apache.cloudstack.context.CallContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "removeGuestOs", description = "Removes a Guest OS from listing.", responseObject = SuccessResponse.class, since = "4.4.0",
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class RemoveGuestOsCmd extends BaseAsyncCmd {

    public static final Logger s_logger = LoggerFactory.getLogger(RemoveGuestOsCmd.class.getName());
    private static final String s_name = "removeguestosresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = GuestOSResponse.class, required = true, description = "ID of the guest OS")
    private Long id;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    @Override
    public void execute() {
        CallContext.current().setEventDetails("Guest OS Id: " + id);
        final boolean result = _mgr.removeGuestOs(this);
        if (result) {
            final SuccessResponse response = new SuccessResponse(getCommandName());
            setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to remove guest OS");
        }
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

    @Override
    public String getEventType() {
        return EventTypes.EVENT_GUEST_OS_REMOVE;
    }

    @Override
    public String getEventDescription() {
        return "Removing Guest OS: " + getId();
    }

    public Long getId() {
        return id;
    }

    @Override
    public ApiCommandJobType getInstanceType() {
        return ApiCommandJobType.GuestOs;
    }
}
