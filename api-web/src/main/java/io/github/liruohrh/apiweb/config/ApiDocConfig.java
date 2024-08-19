package io.github.liruohrh.apiweb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class ApiDocConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("API Web Demo")
            .description("api demo")
            .version("1.0.0")
            .contact(new Contact()
                .email("2372221537@qq.com")
                .url("https://github.com/liruohrh")
                .name("liruohrh")
            )
        );
  }
}
