package io.github.liruohrh.apiplatform;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableDubbo
@ComponentScan(basePackages = {
    "io.github.liruohrh.apiplatform",
    "io.github.liruohrh.apiwebcommon.exception"
})
@SpringBootApplication
public class ApiPlatformApplication {

  public static void main(String[] args) {
    new SpringApplication(ApiPlatformApplication.class).run(args);
  }

}
