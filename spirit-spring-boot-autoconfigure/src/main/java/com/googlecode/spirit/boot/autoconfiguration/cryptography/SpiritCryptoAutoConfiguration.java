package com.googlecode.spirit.boot.autoconfiguration.cryptography;

import com.googlecode.easyec.security.rsa.RSAClientService;
import com.googlecode.easyec.security.rsa.RSAServerService;
import com.googlecode.easyec.security.rsa.support.RSAServerServiceFactoryBean;
import com.googlecode.easyec.security.utils.PemUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Created by JunJie on 11/2/16.
 */
@Configuration
@ConditionalOnClass(RSAClientService.class)
@EnableConfigurationProperties(SpiritCryptoProperties.class)
public class SpiritCryptoAutoConfiguration {

    @Resource
    private SpiritCryptoProperties spiritCryptoProperties;

    @Bean
    public RSAServerService rsaServerService() {
        try {
            char[] pass = null;
            if (spiritCryptoProperties.isUsePassword()) {
                String val = System.getProperty(spiritCryptoProperties.getPasswordKey());
                if (StringUtils.isNotBlank(val)) {
                    pass = val.toCharArray();
                }
            }

            InputStream in = spiritCryptoProperties.getPemFile().getInputStream();
            Object o = PemUtils.read(in, pass, spiritCryptoProperties.getCharset());
            if (!PemUtils.isKeyPair(o)) {
                throw new IllegalArgumentException("There isn't a private key file.");
            }

            KeyPair keyPair = (KeyPair) o;
            if (!(keyPair.getPublic() instanceof RSAPublicKey)) {
                throw new IllegalArgumentException("There isn't a RSAPublicKey object.");
            }

            if (!(keyPair.getPrivate() instanceof RSAPrivateKey)) {
                throw new IllegalArgumentException("There isn't a RSAPrivateKey object.");
            }

            RSAServerServiceFactoryBean fb = new RSAServerServiceFactoryBean();
            fb.setKeyPair(keyPair);
            fb.afterPropertiesSet();

            return fb.getObject();
        } catch (Exception e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }
}
