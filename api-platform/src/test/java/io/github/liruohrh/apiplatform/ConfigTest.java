package io.github.liruohrh.apiplatform;

import java.util.Optional;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

@SpringBootTest(classes = {ConfigTest.Config.class})
public class ConfigTest {
  @Configuration
  public static class Config{}
  @Resource
  ConfigurableEnvironment environment;
  @Test
  public void test() {
    Optional<PropertySource<?>> first = environment.getPropertySources().stream()
        .filter(ps -> ps.getName().contains("file:../private/mail.yml")).findFirst();
    Assertions.assertTrue(first.isPresent());
    Assertions.assertTrue(first.get().containsProperty("spring.mail.username"));
    Assertions.assertTrue(first.get().containsProperty("spring.mail.password"));
  }
}
