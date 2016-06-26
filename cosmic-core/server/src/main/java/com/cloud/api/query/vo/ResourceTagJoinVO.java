package com.cloud.api.query.vo;

import com.cloud.server.ResourceTag;
import com.cloud.server.ResourceTag.ResourceObjectType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "resource_tag_view")
public class ResourceTagJoinVO extends BaseViewVO implements ControlledViewEntity {

    @Column(name = "value")
    String value;
    @Column(name = "resource_id")
    long resourceId;
    @Column(name = "customer")
    String customer;
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "key")
    private String key;
    @Column(name = "resource_uuid")
    private String resourceUuid;
    @Column(name = "resource_type")
    @Enumerated(value = EnumType.STRING)
    private ResourceObjectType resourceType;
    @Column(name = "account_id")
    private long accountId;

    @Column(name = "account_uuid")
    private String accountUuid;

    @Column(name = "account_name")
    private final String accountName = null;

    @Column(name = "account_type")
    private short accountType;

    @Column(name = "domain_id")
    private long domainId;

    @Column(name = "domain_uuid")
    private String domainUuid;

    @Column(name = "domain_name")
    private final String domainName = null;

    @Column(name = "domain_path")
    private final String domainPath = null;

    @Column(name = "project_id")
    private long projectId;

    @Column(name = "project_uuid")
    private String projectUuid;

    @Column(name = "project_name")
    private String projectName;

    public ResourceTagJoinVO() {
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
    public long getAccountId() {
        return accountId;
    }

    @Override
    public long getDomainId() {
        return domainId;
    }

    @Override
    public String getDomainPath() {
        return domainPath;
    }

    @Override
    public short getAccountType() {
        return accountType;
    }

    @Override
    public String getAccountUuid() {
        return accountUuid;
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    @Override
    public String getDomainUuid() {
        return domainUuid;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public String getProjectUuid() {
        return projectUuid;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public long getResourceId() {
        return resourceId;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public ResourceObjectType getResourceType() {
        return resourceType;
    }

    public String getCustomer() {
        return customer;
    }

    @Override
    public Class<?> getEntityType() {
        return ResourceTag.class;
    }
}
