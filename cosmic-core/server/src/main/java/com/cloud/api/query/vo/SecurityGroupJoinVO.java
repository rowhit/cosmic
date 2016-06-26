package com.cloud.api.query.vo;

import com.cloud.network.security.SecurityGroup;
import com.cloud.network.security.SecurityRule.SecurityRuleType;
import com.cloud.server.ResourceTag.ResourceObjectType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "security_group_view")
public class SecurityGroupJoinVO extends BaseViewVO implements ControlledViewEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "uuid")
    private String uuid;

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

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "job_uuid")
    private String jobUuid;

    @Column(name = "job_status")
    private int jobStatus;

    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "rule_uuid")
    private String ruleUuid;

    @Column(name = "rule_start_port")
    private int ruleStartPort;

    @Column(name = "rule_end_port")
    private int ruleEndPort;

    @Column(name = "rule_protocol")
    private String ruleProtocol;

    @Column(name = "rule_type")
    private String ruleType;

    @Column(name = "rule_allowed_network_id")
    private final Long ruleAllowedNetworkId = null;

    @Column(name = "rule_allowed_ip_cidr")
    private final String ruleAllowedSourceIpCidr = null;

    @Column(name = "tag_id")
    private long tagId;

    @Column(name = "tag_uuid")
    private String tagUuid;

    @Column(name = "tag_key")
    private String tagKey;

    @Column(name = "tag_value")
    private String tagValue;

    @Column(name = "tag_domain_id")
    private long tagDomainId;

    @Column(name = "tag_account_id")
    private long tagAccountId;

    @Column(name = "tag_resource_id")
    private long tagResourceId;

    @Column(name = "tag_resource_uuid")
    private String tagResourceUuid;

    @Column(name = "tag_resource_type")
    @Enumerated(value = EnumType.STRING)
    private ResourceObjectType tagResourceType;

    @Column(name = "tag_customer")
    private String tagCustomer;

    public SecurityGroupJoinVO() {
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
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

    public Long getJobId() {
        return jobId;
    }

    public String getJobUuid() {
        return jobUuid;
    }

    public int getJobStatus() {
        return jobStatus;
    }

    public String getDescription() {
        return description;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public String getRuleUuid() {
        return ruleUuid;
    }

    public int getRuleStartPort() {
        return ruleStartPort;
    }

    public int getRuleEndPort() {
        return ruleEndPort;
    }

    public String getRuleProtocol() {
        return ruleProtocol;
    }

    public SecurityRuleType getRuleType() {
        if ("ingress".equalsIgnoreCase(ruleType)) {
            return SecurityRuleType.IngressRule;
        } else {
            return SecurityRuleType.EgressRule;
        }
    }

    public Long getRuleAllowedNetworkId() {
        return ruleAllowedNetworkId;
    }

    public String getRuleAllowedSourceIpCidr() {
        return ruleAllowedSourceIpCidr;
    }

    public long getTagId() {
        return tagId;
    }

    public String getTagUuid() {
        return tagUuid;
    }

    public String getTagKey() {
        return tagKey;
    }

    public String getTagValue() {
        return tagValue;
    }

    public long getTagDomainId() {
        return tagDomainId;
    }

    public long getTagAccountId() {
        return tagAccountId;
    }

    public long getTagResourceId() {
        return tagResourceId;
    }

    public String getTagResourceUuid() {
        return tagResourceUuid;
    }

    public ResourceObjectType getTagResourceType() {
        return tagResourceType;
    }

    public String getTagCustomer() {
        return tagCustomer;
    }

    @Override
    public Class<?> getEntityType() {
        return SecurityGroup.class;
    }
}
