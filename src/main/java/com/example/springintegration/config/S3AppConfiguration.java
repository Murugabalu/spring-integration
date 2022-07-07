package com.example.springintegration.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.aws.support.S3RemoteFileTemplate;
import org.springframework.integration.aws.support.S3SessionFactory;
import org.springframework.integration.aws.support.filters.S3PersistentAcceptOnceFileListFilter;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.integration.aws.inbound.S3StreamingMessageSource;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.PollableChannel;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author n1556638
 */
@Service
public class S3AppConfiguration {

    @Bean
    @InboundChannelAdapter(value = "s3Channel")
    public MessageSource<InputStream> s3InboundStreamingMessageSource(S3RemoteFileTemplate template) {

        S3StreamingMessageSource messageSource = new S3StreamingMessageSource(template);
        messageSource.setRemoteDirectory("test-bucket");
        messageSource.setFilter(new S3PersistentAcceptOnceFileListFilter(new SimpleMetadataStore(),
                                                                         "streaming"));

        return messageSource;
    }

    @Bean
    public PollableChannel s3Channel() {
        return new QueueChannel();
    }

    @Bean
    public S3RemoteFileTemplate template(AmazonS3 amazonS3) {
        return new S3RemoteFileTemplate(new S3SessionFactory(amazonS3));
    }

    @Bean(name = "amazonS3")
    public AmazonS3 nonProdAmazonS3(BasicAWSCredentials basicAWSCredentials) {
        ClientConfiguration config = new ClientConfiguration();
        config.setProxyHost("localhost");
        config.setProxyPort(8090);

        return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName("ap-southeast-1"))
                                    .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                                    .withClientConfiguration(config)
                                    .build();
    }

    @Bean
    public BasicAWSCredentials basicAWSCredentials() {
        return new BasicAWSCredentials("access-key", "secret-key");
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata nonProdPoller() {

        return Pollers.cron("* */2 * * * *")
                      .get();
    }
}
