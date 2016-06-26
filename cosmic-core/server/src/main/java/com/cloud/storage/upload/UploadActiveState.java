package com.cloud.storage.upload;

import com.cloud.agent.api.storage.UploadAnswer;
import com.cloud.agent.api.storage.UploadProgressCommand.RequestType;
import com.cloud.storage.VMTemplateStorageResourceAssoc.Status;

import org.apache.log4j.Level;

public abstract class UploadActiveState extends UploadState {

    public UploadActiveState(final UploadListener ul) {
        super(ul);
    }

    @Override
    public String handleAnswer(final UploadAnswer answer) {
        if (s_logger.isDebugEnabled()) {
            s_logger.debug("handleAnswer, answer status=" + answer.getUploadStatus() + ", curr state=" + getName());
        }
        switch (answer.getUploadStatus()) {
            case UPLOAD_IN_PROGRESS:
                getUploadListener().scheduleStatusCheck(RequestType.GET_STATUS);
                return Status.UPLOAD_IN_PROGRESS.toString();
            case UPLOADED:
                getUploadListener().scheduleImmediateStatusCheck(RequestType.PURGE);
                getUploadListener().cancelTimeoutTask();
                return Status.UPLOADED.toString();
            case NOT_UPLOADED:
                getUploadListener().scheduleStatusCheck(RequestType.GET_STATUS);
                return Status.NOT_UPLOADED.toString();
            case UPLOAD_ERROR:
                getUploadListener().cancelStatusTask();
                getUploadListener().cancelTimeoutTask();
                return Status.UPLOAD_ERROR.toString();
            case UNKNOWN:
                getUploadListener().cancelStatusTask();
                getUploadListener().cancelTimeoutTask();
                return Status.UPLOAD_ERROR.toString();
            default:
                return null;
        }
    }

    @Override
    public String handleAbort() {
        return Status.ABANDONED.toString();
    }

    @Override
    public String handleTimeout(final long updateMs) {
        if (s_logger.isTraceEnabled()) {
            getUploadListener().log("handleTimeout, updateMs=" + updateMs + ", curr state= " + getName(), Level.TRACE);
        }
        String newState = getName();
        if (updateMs > 5 * UploadListener.STATUS_POLL_INTERVAL) {
            newState = Status.UPLOAD_ERROR.toString();
            getUploadListener().log("timeout: transitioning to upload error state, currstate=" + getName(), Level.DEBUG);
        } else if (updateMs > 3 * UploadListener.STATUS_POLL_INTERVAL) {
            getUploadListener().cancelStatusTask();
            getUploadListener().scheduleImmediateStatusCheck(RequestType.GET_STATUS);
            getUploadListener().scheduleTimeoutTask(3 * UploadListener.STATUS_POLL_INTERVAL);
            getUploadListener().log(getName() + " first timeout: checking again ", Level.DEBUG);
        } else {
            getUploadListener().scheduleTimeoutTask(3 * UploadListener.STATUS_POLL_INTERVAL);
        }
        return newState;
    }

    @Override
    public String handleDisconnect() {

        return Status.UPLOAD_ERROR.toString();
    }

    @Override
    public void onEntry(final String prevState, final UploadEvent event, final Object evtObj) {
        super.onEntry(prevState, event, evtObj);

        if (event == UploadEvent.UPLOAD_ANSWER) {
            getUploadListener().setLastUpdated();
        }
    }

    @Override
    public void onExit() {
    }
}
