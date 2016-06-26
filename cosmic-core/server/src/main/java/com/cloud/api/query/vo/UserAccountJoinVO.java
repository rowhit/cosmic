package com.cloud.api.query.vo;

import com.cloud.user.UserAccount;
import com.cloud.utils.db.Encrypt;
import com.cloud.utils.db.GenericDao;
import org.apache.cloudstack.api.Identity;
import org.apache.cloudstack.api.InternalIdentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "user_view")
public class UserAccountJoinVO extends BaseViewVO implements InternalIdentity, Identity, ControlledViewEntity {

    @Column(name = "is_registered")
    boolean registered;
    @Column(name = "incorrect_login_attempts")
    int loginAttempts;
    @Column(name = "default")
    boolean isDefault;
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "username")
    private final String username = null;
    @Column(name = "password")
    private final String password = null;
    @Column(name = "firstname")
    private final String firstname = null;
    @Column(name = "lastname")
    private final String lastname = null;
    @Column(name = "email")
    private final String email = null;
    @Column(name = "state")
    private String state;
    @Column(name = "api_key")
    private final String apiKey = null;
    @Encrypt
    @Column(name = "secret_key")
    private final String secretKey = null;
    @Column(name = GenericDao.CREATED_COLUMN)
    private Date created;
    @Column(name = GenericDao.REMOVED_COLUMN)
    private Date removed;
    @Column(name = "timezone")
    private String timezone;
    @Column(name = "registration_token")
    private final String registrationToken = null;
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
    @Column(name = "job_id")
    private Long jobId;
    @Column(name = "job_uuid")
    private String jobUuid;
    @Column(name = "job_status")
    private int jobStatus;

    public UserAccountJoinVO() {
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getDomainId() {
        return domainId;
    }

    public String getDomainPath() {
        return domainPath;
    }

    public short getAccountType() {
        return accountType;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getDomainUuid() {
        return domainUuid;
    }

    public String getDomainName() {
        return domainName;
    }

    @Override
    public String getProjectUuid() {
        return null;
    }

    @Override
    public String getProjectName() {
        return null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getState() {
        return state;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Date getCreated() {
        return created;
    }

    public Date getRemoved() {
        return removed;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getRegistrationToken() {
        return registrationToken;
    }

    public boolean isRegistered() {
        return registered;
    }

    public int getLoginAttempts() {
        return loginAttempts;
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

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Class<?> getEntityType() {
        return UserAccount.class;
    }
}
