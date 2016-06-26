package com.cloud.network.dao;

import com.cloud.network.UserIpv6AddressVO;
import com.cloud.utils.db.GenericDao;

import java.util.List;

public interface UserIpv6AddressDao extends GenericDao<UserIpv6AddressVO, Long> {
    List<UserIpv6AddressVO> listByAccount(long accountId);

    List<UserIpv6AddressVO> listByVlanId(long vlanId);

    List<UserIpv6AddressVO> listByDcId(long dcId);

    List<UserIpv6AddressVO> listByNetwork(long networkId);

    public UserIpv6AddressVO findByNetworkIdAndIp(long networkId, String ipAddress);

    List<UserIpv6AddressVO> listByPhysicalNetworkId(long physicalNetworkId);

    long countExistedIpsInNetwork(long networkId);

    long countExistedIpsInVlan(long vlanId);
}
