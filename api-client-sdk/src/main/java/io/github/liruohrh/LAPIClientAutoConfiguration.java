package io.github.liruohrh;

import io.github.liruohrh.client.LApiClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(LAPIClientProperties.class)
@AutoConfiguration
public class LAPIClientAutoConfiguration {
    public static final String SERVER_HOST = "http://localhost:88";
    @Bean
    LApiClient turboAPIClient(LAPIClientProperties lapiClientProperties){
        return new LApiClient(
            lapiClientProperties.getAppKey(),
            lapiClientProperties.getAppSecret()
        );
    }
}