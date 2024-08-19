package io.github.liruohrh;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties("lapi.client")
public class LAPIClientProperties {
  private String appKey;
  private String appSecret;
}
