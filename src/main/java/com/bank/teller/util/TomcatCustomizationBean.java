package com.bank.teller.util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class TomcatCustomizationBean {

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(9010);
        factory.setSessionTimeout(10, TimeUnit.MINUTES);

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();

        try {
            File keystore = new ClassPathResource("keystore/keystore.jks").getFile();
            File truststore = new ClassPathResource("keystore/keystore.jks").getFile();
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(9011);
            
            protocol.setSSLEnabled(true);
            
//protocol.setSSLEnabled(false);
            protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass("password");
            protocol.setTruststoreFile(truststore.getAbsolutePath());
            protocol.setTruststorePass("password");
            protocol.setKeyAlias("tomcat");
            protocol.setClientAuth("false");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        factory.addAdditionalTomcatConnectors(connector);
        return factory;
    }
}