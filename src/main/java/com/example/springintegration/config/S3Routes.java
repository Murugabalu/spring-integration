package com.example.springintegration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

import java.io.InputStream;

@Configuration
public class S3Routes {

    @Bean
    public IntegrationFlow downloadFlow(MessageSource<InputStream> s3InboundStreamingMessageSource) {

        return IntegrationFlows.from(s3InboundStreamingMessageSource)
                               .channel("s3Channel")
                               .handle("QueryServiceImpl", "processFile")
                               .get();
    }

}
