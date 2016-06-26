package org.apache.cloudstack.engine.datacenter.entity.api;

import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.utils.fsm.NoTransitionException;
import org.apache.cloudstack.engine.datacenter.entity.api.DataCenterResourceEntity.State.Event;
import org.apache.cloudstack.engine.datacenter.entity.api.db.EngineHostVO;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HostEntityImpl implements HostEntity {

    private final DataCenterResourceManager manager;

    private final EngineHostVO hostVO;

    public HostEntityImpl(final String uuid, final DataCenterResourceManager manager) {
        this.manager = manager;
        hostVO = manager.loadHost(uuid);
    }

    @Override
    public boolean enable() {
        try {
            manager.changeState(this, Event.EnableRequest);
        } catch (final NoTransitionException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean disable() {
        try {
            manager.changeState(this, Event.DisableRequest);
        } catch (final NoTransitionException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deactivate() {
        try {
            manager.changeState(this, Event.DeactivateRequest);
        } catch (final NoTransitionException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean reactivate() {
        try {
            manager.changeState(this, Event.ActivatedRequest);
        } catch (final NoTransitionException e) {
            return false;
        }
        return true;
    }

    @Override
    public State getState() {
        return hostVO.getOrchestrationState();
    }

    @Override
    public void persist() {
        manager.saveHost(hostVO);
    }

    @Override
    public String getName() {
        return hostVO.getName();
    }

    public void setName(final String name) {
        hostVO.setName(name);
    }

    @Override
    public String getUuid() {
        return hostVO.getUuid();
    }

    @Override
    public long getId() {
        return hostVO.getId();
    }

    @Override
    public String getCurrentState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDesiredState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getCreatedTime() {
        return hostVO.getCreated();
    }

    @Override
    public Date getLastUpdatedTime() {
        return hostVO.getLastUpdated();
    }

    @Override
    public String getOwner() {
        // TODO Auto-generated method stub
        return hostVO.getOwner();
    }

    @Override
    public Map<String, String> getDetails() {
        return hostVO.getDetails();
    }

    public void setDetails(final Map<String, String> details) {
        hostVO.setDetails(details);
    }

    @Override
    public void addDetail(final String name, final String value) {
        hostVO.setDetail(name, value);
    }

    @Override
    public void delDetail(final String name, final String value) {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateDetail(final String name, final String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Method> getApplicableActions() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setOwner(final String owner) {
        hostVO.setOwner(owner);
    }

    @Override
    public Long getTotalMemory() {
        return hostVO.getTotalMemory();
    }

    @Override
    public Integer getCpus() {
        return hostVO.getCpus();
    }

    @Override
    public Long getSpeed() {
        return hostVO.getSpeed();
    }

    @Override
    public Long getPodId() {
        return hostVO.getPodId();
    }

    @Override
    public long getDataCenterId() {
        return hostVO.getDataCenterId();
    }

    @Override
    public HypervisorType getHypervisorType() {
        return hostVO.getHypervisorType();
    }

    @Override
    public String getGuid() {
        return hostVO.getGuid();
    }

    @Override
    public Long getClusterId() {
        return hostVO.getClusterId();
    }
}
