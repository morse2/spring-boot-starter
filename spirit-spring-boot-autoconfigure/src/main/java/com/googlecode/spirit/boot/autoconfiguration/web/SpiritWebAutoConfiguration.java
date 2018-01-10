package com.googlecode.spirit.boot.autoconfiguration.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.easyec.spirit.web.httpcomponent.HttpRequest;
import com.googlecode.easyec.spirit.web.httpcomponent.impl.AutowireCapableHttpRequestFactoryBean;
import com.googlecode.easyec.spirit.web.httpcomponent.impl.HttpRequestFactoryBean;
import com.googlecode.easyec.spirit.web.webservice.factory.StreamObjectFactory;
import com.googlecode.easyec.spirit.web.webservice.factory.impl.DefaultJackson2ObjectFactory;
import com.googlecode.easyec.spirit.web.webservice.factory.impl.Jaxb2MarshallerObjectFactory;
import com.googlecode.easyec.spirit.web.webservice.factory.impl.StringObjectFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Configuration
@EnableConfigurationProperties(SpiritWebProperties.class)
public class SpiritWebAutoConfiguration {

    @Resource
    private SpiritWebProperties spiritWebProperties;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(HttpClientConnectionManager.class)
    @ConfigurationProperties(prefix = "spirit.web")
    public HttpClientConnectionManager httpClientConnectionManager() {
        return new PoolingHttpClientConnectionManager();
    }

    @Bean
    @ConditionalOnMissingBean(HttpClientBuilder.class)
    public HttpClientBuilder httpClientBuilder(HttpClientConnectionManager httpClientConnectionManager) {
        return HttpClients.custom().setConnectionManager(httpClientConnectionManager);
    }

    @Scope("prototype")
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(HttpClient.class)
    public CloseableHttpClient httpClient(HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder.build();
    }

    @Bean
    @ConditionalOnMissingBean(HttpRequest.class)
    @ConditionalOnProperty(prefix = "spirit.web", name = "use-http-request", havingValue = "true")
    public HttpRequest httpRequest(HttpClient httpClient) {
        try {
            HttpRequestFactoryBean defaultHttpRequestFactory = new HttpRequestFactoryBean();
            defaultHttpRequestFactory.setHttpClient(httpClient);
            defaultHttpRequestFactory.afterPropertiesSet();
            defaultHttpRequestFactory.getObject();

            AutowireCapableHttpRequestFactoryBean httpRequestFactoryBean = new AutowireCapableHttpRequestFactoryBean();
            httpRequestFactoryBean.setHttpRequest(defaultHttpRequestFactory.getObject());
            httpRequestFactoryBean.afterPropertiesSet();
            return httpRequestFactoryBean.getObject();
        } catch (Exception e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }

    // ----- stream object factory here
    @Bean
    @ConditionalOnMissingBean(StringObjectFactory.class)
    @ConditionalOnProperty(prefix = "spirit.web", name = "use-string-stream", havingValue = "true")
    public StreamObjectFactory stringObjectFactory() {
        return new StringObjectFactory();
    }

    @Bean
    @ConditionalOnMissingBean(DefaultJackson2ObjectFactory.class)
    @ConditionalOnProperty(prefix = "spirit.web", name = "use-json-stream", havingValue = "true")
    public StreamObjectFactory jsonObjectFactory(ObjectMapper objectMapper) {
        return new DefaultJackson2ObjectFactory(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(Jaxb2MarshallerObjectFactory.class)
    @ConditionalOnProperty(prefix = "spirit.web", name = "use-jaxb-stream", havingValue = "true")
    public StreamObjectFactory jaxbObjectFactory(Jaxb2Marshaller jaxb2Marshaller) {
        return new Jaxb2MarshallerObjectFactory(jaxb2Marshaller);
    }

    @Bean
    @ConditionalOnMissingBean(Jaxb2Marshaller.class)
    @ConditionalOnProperty(prefix = "spirit.web", name = "use-jaxb-stream", havingValue = "true")
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(spiritWebProperties.getJaxbPackageToScan());

        return marshaller;
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    @ConditionalOnProperty(prefix = "spirit.web", name = "use-json-stream", havingValue = "true")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        if (isNotBlank(spiritWebProperties.getJsonDateFormat())) {
            objectMapper.setDateFormat(new SimpleDateFormat(
                spiritWebProperties.getJsonDateFormat()
            ));
        }

        return objectMapper;
    }
}
