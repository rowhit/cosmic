package org.apache.cloudstack.resourcedetail.dao;

import com.cloud.utils.db.GenericDao;
import org.apache.cloudstack.resourcedetail.AutoScaleVmGroupDetailVO;
import org.apache.cloudstack.resourcedetail.ResourceDetailsDao;

public interface AutoScaleVmGroupDetailsDao extends GenericDao<AutoScaleVmGroupDetailVO, Long>, ResourceDetailsDao<AutoScaleVmGroupDetailVO> {

}
