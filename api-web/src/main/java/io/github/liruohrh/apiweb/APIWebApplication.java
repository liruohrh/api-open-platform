package io.github.liruohrh.apiweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
    "io.github.liruohrh.apiweb",
    "io.github.liruohrh.apiwebcommon.exception"
})
@SpringBootApplication
public class APIWebApplication {
  public static void main(String[] args) {
    new SpringApplication(APIWebApplication.class).run(args);
  }
}
