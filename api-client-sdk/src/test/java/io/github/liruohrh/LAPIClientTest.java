package io.github.liruohrh;

import io.github.liruohrh.apicommon.error.Resp;
import io.github.liruohrh.apicommon.model.vo.WeatherVo;
import io.github.liruohrh.client.LApiClient;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@Slf4j
@SpringBootTest(classes = LAPIClientAutoConfiguration.class)
public class LAPIClientTest {
  @Resource
  private LApiClient lApiClient;

  @Test
  public void testGetWeather() {
    Resp<WeatherVo> resp = lApiClient.getWeather("广州", null, null);
    Assertions.assertEquals(0, resp.getCode());
    Assertions.assertNotNull( resp.getData());
    Assertions.assertEquals("广州市", resp.getData().getCity());

    resp = lApiClient.getWeather("广州", "180.149.130.16", null);
    Assertions.assertNotEquals(0, resp.getCode());
    Assertions.assertTrue( resp.getMsg().contains("城市和ip只能二选一"));
  }
}
