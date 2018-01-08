package com.googlecode.spirit.boot.autoconfiguration.cryptography;

import com.googlecode.easyec.security.rsa.RSAClientService;
import com.googlecode.easyec.security.rsa.RSAServerService;
import com.googlecode.easyec.security.rsa.support.RSAClientServiceFactoryBean;
import com.googlecode.easyec.security.rsa.support.RSAServerServiceFactoryBean;
import com.googlecode.easyec.security.utils.PemUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnClass({ RSAClientService.class, RSAServerService.class })
@EnableConfigurationProperties(SpiritCryptoProperties.class)
public class SpiritCryptoAutoConfiguration {

    @Resource
    private SpiritCryptoProperties spiritCryptoProperties;

    @Bean
    @ConditionalOnMissingBean(RSAServerService.class)
    @ConditionalOnProperty(prefix = "spirit.crypto", name = "rsa-server", havingValue = "true")
    public RSAServerService rsaServerService() {
        try {
            InputStream in = spiritCryptoProperties.getPemFile().getInputStream();
            Object o = PemUtils.read(in, spiritCryptoProperties.getCharset());

            PEMKeyPair pemKeyPair;
            if (o instanceof PEMEncryptedKeyPair) {
                char[] pass = null;
                if (spiritCryptoProperties.isUsePassword()) {
                    String val = System.getProperty(spiritCryptoProperties.getPasswordKey());
                    if (StringUtils.isNotBlank(val)) {
                        pass = val.toCharArray();
                    }
                }

                if (ArrayUtils.isEmpty(pass)) {
                    throw new IllegalArgumentException("Password for private key file must be present.");
                }

                pemKeyPair = ((PEMEncryptedKeyPair) o).decryptKeyPair(
                    new JcePEMDecryptorProviderBuilder().build(pass)
                );
            } else pemKeyPair = ((PEMKeyPair) o);

            KeyPair keyPair = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
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

    @Bean
    @ConditionalOnMissingBean(RSAClientService.class)
    @ConditionalOnProperty(prefix = "spirit.crypto", name = "rsa-client", havingValue = "true")
    public RSAClientService rsaClientService() {
        try {
            InputStream in = spiritCryptoProperties.getPemFile().getInputStream();
            Object o = new JcaPEMKeyConverter().getPublicKey(
                ((SubjectPublicKeyInfo) PemUtils.read(
                    in, spiritCryptoProperties.getCharset()
                ))
            );

            if (!PemUtils.isPublicKey(o)) {
                throw new IllegalArgumentException("There isn't a public key file.");
            }

            if (!(o instanceof RSAPublicKey)) {
                throw new IllegalArgumentException("There isn't a RSAPublicKey object.");
            }

            RSAClientServiceFactoryBean fa = new RSAClientServiceFactoryBean();
            fa.setPublicKey(((RSAPublicKey) o));
            fa.afterPropertiesSet();

            return fa.getObject();
        } catch (Exception e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }
}
