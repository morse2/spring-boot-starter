package com.googlecode.spirit.boot.autoconfiguration.cryptography;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Created by JunJie on 10/29/16.
 */
@ConfigurationProperties(prefix = "spirit.crypto")
public class SpiritCryptoProperties {

    private Resource pemFile;
    private boolean usePassword;
    private String passwordKey;
    private String charset;

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
