package com.cloud.network.vpc;

import com.cloud.utils.db.GenericDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "vpc_gateways")
public class VpcGatewayVO implements VpcGateway {

    @Column(name = "ip4_address")
    String ip4Address;
    @Column(name = "gateway")
    String gateway;
    @Column(name = "netmask")
    String netmask;
    @Column(name = "vlan_tag")
    String broadcastUri;
    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    VpcGateway.Type type;
    @Column(name = "vpc_id")
    Long vpcId;
    @Column(name = "zone_id")
    long zoneId;
    @Column(name = "network_id")
    long networkId;
    @Column(name = GenericDao.CREATED_COLUMN)
    Date created;
    @Column(name = GenericDao.REMOVED_COLUMN)
    Date removed;
    @Column(name = "account_id")
    long accountId;
    @Column(name = "domain_id")
    long domainId;
    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    State state;
    @Column(name = "source_nat")
    boolean sourceNat;
    @Column(name = "network_acl_id")
    long networkACLId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "uuid")
    private final String uuid;

    protected VpcGatewayVO() {
        uuid = UUID.randomUUID().toString();
    }

    /**
     * @param ip4Address
     * @param type
     * @param vpcId
     * @param zoneId
     * @param networkId
     * @param broadcastUri TODO
     * @param gateway      TODO
     * @param netmask      TODO
     * @param accountId    TODO
     * @param domainId     TODO
     * @param account_id
     * @param sourceNat
     */
    public VpcGatewayVO(final String ip4Address, final Type type, final long vpcId, final long zoneId, final long networkId, final String broadcastUri, final String gateway,
                        final String netmask, final long accountId,
                        final long domainId, final boolean sourceNat, final long networkACLId) {
        this.ip4Address = ip4Address;
        this.type = type;
        this.vpcId = vpcId;
        this.zoneId = zoneId;
        this.networkId = networkId;
        this.broadcastUri = broadcastUri;
        this.gateway = gateway;
        this.netmask = netmask;
        uuid = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.domainId = domainId;
        state = State.Creating;
        this.sourceNat = sourceNat;
        this.networkACLId = networkACLId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getIp4Address() {
        return ip4Address;
    }

    @Override
    public VpcGateway.Type getType() {
        return type;
    }

    @Override
    public Long getVpcId() {
        return vpcId;
    }

    @Override
    public long getZoneId() {
        return zoneId;
    }

    @Override
    public long getNetworkId() {
        return networkId;
    }

    @Override
    public String getGateway() {
        return gateway;
    }

    @Override
    public String getNetmask() {
        return netmask;
    }

    @Override
    public String getBroadcastUri() {
        return broadcastUri;
    }

    @Override
    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    @Override
    public boolean getSourceNat() {
        return sourceNat;
    }

    @Override
    public long getNetworkACLId() {
        return networkACLId;
    }

    public void setNetworkACLId(final long networkACLId) {
        this.networkACLId = networkACLId;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder("VpcGateway[");
        buf.append(id).append("|").append(ip4Address.toString()).append("|").append(vpcId).append("]");
        return buf.toString();
    }

    @Override
    public long getAccountId() {
        return accountId;
    }

    @Override
    public long getDomainId() {
        return domainId;
    }

    @Override
    public Class<?> getEntityType() {
        return VpcGateway.class;
    }
}
