package com.cloud.vm.dao;

import com.cloud.utils.db.GenericDao;
import com.cloud.vm.InstanceGroupVO;

import java.util.List;

public interface InstanceGroupDao extends GenericDao<InstanceGroupVO, Long> {
    List<InstanceGroupVO> listByAccountId(long id);

    boolean isNameInUse(Long accountId, String name);

    InstanceGroupVO findByAccountAndName(Long accountId, String name);

    /**
     * Updates name for the vm
     *
     * @param id   - group id
     * @param name
     */
    void updateVmGroup(long id, String name);
}
