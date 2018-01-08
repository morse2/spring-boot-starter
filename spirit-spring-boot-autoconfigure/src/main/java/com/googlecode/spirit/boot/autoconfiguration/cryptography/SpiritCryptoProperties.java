package com.googlecode.spirit.boot.autoconfiguration.cryptography;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Created by JunJie on 10/29/16.
 */
@ConfigurationProperties(prefix = "spirit.crypto")
public class SpiritCryptoProperties {

    private boolean rsaClient;
    private boolean rsaServer;
    private Resource pemFile;
    private boolean usePassword;
    private String passwordKey;
    private String charset;

    public boolean isRsaClient() {
        return rsaClient;
    }

    public void setRsaClient(boolean rsaClient) {
        this.rsaClient = rsaClient;
    }

    public boolean isRsaServer() {
        return rsaServer;
    }

    public void setRsaServer(boolean rsaServer) {
        this.rsaServer = rsaServer;
    }

    public Resource getPemFile() {
        return pemFile;
    }

    public void setPemFile(Resource pemFile) {
        this.pemFile = pemFile;
    }

    public boolean isUsePassword() {
        return usePassword;
    }

    public void setUsePassword(boolean usePassword) {
        this.usePassword = usePassword;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
