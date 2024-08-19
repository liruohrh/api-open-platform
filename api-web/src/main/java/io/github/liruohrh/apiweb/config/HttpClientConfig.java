package io.github.liruohrh.apiweb.config;

import io.github.liruohrh.apiwebcommon.spring.SpringRestTemplateErrorHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {
  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplateBuilder()
        .errorHandler(new SpringRestTemplateErrorHandler())
        .build();
  }


}
