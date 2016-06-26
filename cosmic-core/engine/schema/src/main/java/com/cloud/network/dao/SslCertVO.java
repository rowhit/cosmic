package com.cloud.network.dao;

import com.cloud.network.lb.SslCert;
import com.cloud.utils.db.Encrypt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "sslcerts")
public class SslCertVO implements SslCert {

    @Column(name = "domain_id")
    long domainId;
    @Column(name = "fingerprint")
    String fingerPrint;
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "uuid")
    private final String uuid;
    @Column(name = "certificate", length = 16384)
    private String certificate;
    @Column(name = "chain", length = 2097152)
    private String chain;
    @Encrypt
    @Column(name = "key", length = 16384)
    private String key;
    @Encrypt
    @Column(name = "password")
    private String password;
    @Column(name = "account_id")
    private Long accountId;

    public SslCertVO() {
        uuid = UUID.randomUUID().toString();
    }

    public SslCertVO(final String cert, final String key, final String password, final String chain, final Long accountId, final Long domainId, final String fingerPrint) {
        certificate = cert;
        this.key = key;
        this.chain = chain;
        this.password = password;
        this.accountId = accountId;
        this.domainId = domainId;
        this.fingerPrint = fingerPrint;
        uuid = UUID.randomUUID().toString();
    }

    // Getters
    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public String getCertificate() {
        return certificate;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getChain() {
        return chain;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getFingerPrint() {
        return fingerPrint;
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
        return SslCert.class;
    }
}
