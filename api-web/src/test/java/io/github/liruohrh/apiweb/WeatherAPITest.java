package io.github.liruohrh.apiweb;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.model.vo.WeatherVo;
import io.github.liruohrh.apiweb.model.VVHanWeatherResp;
import io.github.liruohrh.apiweb.model.VVHanWeatherResp.Weather;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class WeatherAPITest {

  @Test
  public void test1() {
    RestTemplate restTemplate = new RestTemplateBuilder().build();
    ResponseEntity<VVHanWeatherResp> responseEntity = restTemplate.getForEntity(
        "https://api.vvhan.com/api/weather?city=北京", VVHanWeatherResp.class);
//    String body = responseEntity.getBody();
//    System.out.println(body);

    VVHanWeatherResp vvHanWeatherResp = responseEntity.getBody();
    if (vvHanWeatherResp == null) {
      throw new BusinessException("服务繁忙，请稍后再试。");
    }
    WeatherVo weatherVo = new WeatherVo();
    if (CollUtil.isNotEmpty(vvHanWeatherResp.getWeather())) {
      weatherVo.setWeatherList(vvHanWeatherResp.getWeather().stream()
          .map(Weather::to)
          .collect(Collectors.toList()));
    }
    if (vvHanWeatherResp.getAirQuality() != null) {
      weatherVo.setAir(vvHanWeatherResp.getAirQuality().to());
    }
    if (StrUtil.isNotEmpty(vvHanWeatherResp.getCity())) {
      weatherVo.setCity(vvHanWeatherResp.getCity());
    }
  }
}
