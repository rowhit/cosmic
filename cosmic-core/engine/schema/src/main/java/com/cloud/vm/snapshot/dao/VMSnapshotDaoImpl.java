package com.cloud.vm.snapshot.dao;

import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.SearchCriteria.Op;
import com.cloud.utils.db.UpdateBuilder;
import com.cloud.vm.snapshot.VMSnapshot;
import com.cloud.vm.snapshot.VMSnapshot.Event;
import com.cloud.vm.snapshot.VMSnapshot.State;
import com.cloud.vm.snapshot.VMSnapshotVO;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VMSnapshotDaoImpl extends GenericDaoBase<VMSnapshotVO, Long> implements VMSnapshotDao {
    private static final Logger s_logger = LoggerFactory.getLogger(VMSnapshotDaoImpl.class);
    private final SearchBuilder<VMSnapshotVO> SnapshotSearch;
    private final SearchBuilder<VMSnapshotVO> ExpungingSnapshotSearch;
    private final SearchBuilder<VMSnapshotVO> SnapshotStatusSearch;
    private final SearchBuilder<VMSnapshotVO> AllFieldsSearch;

    protected VMSnapshotDaoImpl() {
        AllFieldsSearch = createSearchBuilder();
        AllFieldsSearch.and("state", AllFieldsSearch.entity().getState(), Op.EQ);
        AllFieldsSearch.and("accountId", AllFieldsSearch.entity().getAccountId(), Op.EQ);
        AllFieldsSearch.and("vm_id", AllFieldsSearch.entity().getVmId(), Op.EQ);
        AllFieldsSearch.and("deviceId", AllFieldsSearch.entity().getVmId(), Op.EQ);
        AllFieldsSearch.and("id", AllFieldsSearch.entity().getId(), Op.EQ);
        AllFieldsSearch.and("removed", AllFieldsSearch.entity().getState(), Op.EQ);
        AllFieldsSearch.and("parent", AllFieldsSearch.entity().getParent(), Op.EQ);
        AllFieldsSearch.and("current", AllFieldsSearch.entity().getCurrent(), Op.EQ);
        AllFieldsSearch.and("vm_snapshot_type", AllFieldsSearch.entity().getType(), Op.EQ);
        AllFieldsSearch.and("updatedCount", AllFieldsSearch.entity().getUpdatedCount(), Op.EQ);
        AllFieldsSearch.and("display_name", AllFieldsSearch.entity().getDisplayName(), SearchCriteria.Op.EQ);
        AllFieldsSearch.and("name", AllFieldsSearch.entity().getName(), SearchCriteria.Op.EQ);
        AllFieldsSearch.done();

        SnapshotSearch = createSearchBuilder();
        SnapshotSearch.and("vm_id", SnapshotSearch.entity().getVmId(), SearchCriteria.Op.EQ);
        SnapshotSearch.done();

        ExpungingSnapshotSearch = createSearchBuilder();
        ExpungingSnapshotSearch.and("state", ExpungingSnapshotSearch.entity().getState(), SearchCriteria.Op.EQ);
        ExpungingSnapshotSearch.and("removed", ExpungingSnapshotSearch.entity().getRemoved(), SearchCriteria.Op.NULL);
        ExpungingSnapshotSearch.done();

        SnapshotStatusSearch = createSearchBuilder();
        SnapshotStatusSearch.and("vm_id", SnapshotStatusSearch.entity().getVmId(), SearchCriteria.Op.EQ);
        SnapshotStatusSearch.and("state", SnapshotStatusSearch.entity().getState(), SearchCriteria.Op.IN);
        SnapshotStatusSearch.done();
    }

    @Override
    public List<VMSnapshotVO> findByVm(final Long vmId) {
        final SearchCriteria<VMSnapshotVO> sc = SnapshotSearch.create();
        sc.setParameters("vm_id", vmId);
        return listBy(sc, null);
    }

    @Override
    public List<VMSnapshotVO> listExpungingSnapshot() {
        final SearchCriteria<VMSnapshotVO> sc = ExpungingSnapshotSearch.create();
        sc.setParameters("state", State.Expunging);
        return listBy(sc, null);
    }

    @Override
    public List<VMSnapshotVO> listByInstanceId(final Long vmId, final State... status) {
        final SearchCriteria<VMSnapshotVO> sc = SnapshotStatusSearch.create();
        sc.setParameters("vm_id", vmId);
        sc.setParameters("state", (Object[]) status);
        return listBy(sc, null);
    }

    @Override
    public VMSnapshotVO findCurrentSnapshotByVmId(final Long vmId) {
        final SearchCriteria<VMSnapshotVO> sc = AllFieldsSearch.create();
        sc.setParameters("vm_id", vmId);
        sc.setParameters("current", 1);
        return findOneBy(sc);
    }

    @Override
    public List<VMSnapshotVO> listByParent(final Long vmSnapshotId) {
        final SearchCriteria<VMSnapshotVO> sc = AllFieldsSearch.create();
        sc.setParameters("parent", vmSnapshotId);
        sc.setParameters("state", State.Ready);
        return listBy(sc, null);
    }

    @Override
    public VMSnapshotVO findByName(final Long vmId, final String name) {
        final SearchCriteria<VMSnapshotVO> sc = AllFieldsSearch.create();
        sc.setParameters("vm_id", vmId);
        sc.setParameters("display_name", name);
        return null;
    }

    public List<VMSnapshotVO> listByAccountId(final Long accountId) {
        final SearchCriteria sc = this.AllFieldsSearch.create();
        sc.setParameters("accountId", new Object[]{accountId});
        return listBy(sc, null);
    }

    @Override
    public boolean updateState(final State currentState, final Event event, final State nextState, final VMSnapshot vo, final Object data) {

        final Long oldUpdated = vo.getUpdatedCount();
        final Date oldUpdatedTime = vo.getUpdated();

        final SearchCriteria<VMSnapshotVO> sc = AllFieldsSearch.create();
        sc.setParameters("id", vo.getId());
        sc.setParameters("state", currentState);
        sc.setParameters("updatedCount", vo.getUpdatedCount());

        vo.incrUpdatedCount();

        final UpdateBuilder builder = getUpdateBuilder(vo);
        builder.set(vo, "state", nextState);
        builder.set(vo, "updated", new Date());

        final int rows = update((VMSnapshotVO) vo, sc);
        if (rows == 0 && s_logger.isDebugEnabled()) {
            final VMSnapshotVO dbVol = findByIdIncludingRemoved(vo.getId());
            if (dbVol != null) {
                final StringBuilder str = new StringBuilder("Unable to update ").append(vo.toString());
                str.append(": DB Data={id=")
                   .append(dbVol.getId())
                   .append("; state=")
                   .append(dbVol.getState())
                   .append("; updatecount=")
                   .append(dbVol.getUpdatedCount())
                   .append(";updatedTime=")
                   .append(dbVol.getUpdated());
                str.append(": New Data={id=")
                   .append(vo.getId())
                   .append("; state=")
                   .append(nextState)
                   .append("; event=")
                   .append(event)
                   .append("; updatecount=")
                   .append(vo.getUpdatedCount())
                   .append("; updatedTime=")
                   .append(vo.getUpdated());
                str.append(": stale Data={id=")
                   .append(vo.getId())
                   .append("; state=")
                   .append(currentState)
                   .append("; event=")
                   .append(event)
                   .append("; updatecount=")
                   .append(oldUpdated)
                   .append("; updatedTime=")
                   .append(oldUpdatedTime);
            } else {
                s_logger.debug("Unable to update VM snapshot: id=" + vo.getId() + ", as there is no such snapshot exists in the database anymore");
            }
        }
        return rows > 0;
    }
}
