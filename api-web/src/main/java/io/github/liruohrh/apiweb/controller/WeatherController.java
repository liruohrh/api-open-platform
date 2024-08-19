package io.github.liruohrh.apiweb.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.error.ErrorCode;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apicommon.error.Resp;
import io.github.liruohrh.apicommon.model.vo.WeatherVo;
import io.github.liruohrh.apiweb.model.VVHanWeather1Resp;
import io.github.liruohrh.apiweb.model.VVHanWeatherReq;
import io.github.liruohrh.apiweb.model.VVHanWeatherResp;
import io.github.liruohrh.apiweb.model.VVHanWeatherResp.Weather;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/weather")
@RestController
public class WeatherController {

  private final RestTemplate restTemplate;

  public WeatherController(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @GetMapping
  public Resp<WeatherVo> currentWeekWeather(
      VVHanWeatherReq weatherReq
  ) {
    if(StrUtil.isAllNotEmpty(weatherReq.getCity(), weatherReq.getIp())){
      throw new ParamException("城市和ip只能二选一");
    }

    String baseUrl = "https://api.vvhan.com/api/weather";
    HashMap<String, Object> argMap = new HashMap<>();
    if (StrUtil.isNotEmpty(weatherReq.getCity())) {
      argMap.put("city", weatherReq.getCity());
    }
    if (StrUtil.isNotEmpty(weatherReq.getIp())) {
      argMap.put("ip", weatherReq.getIp());
    }
    if (StrUtil.isNotEmpty(weatherReq.getType())) {
      argMap.put("type", weatherReq.getType());
    }
    String weatherApiQueryString = argMap.entrySet().stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining("&"));

    if (StrUtil.isEmpty(weatherReq.getType())) {
      ResponseEntity<VVHanWeather1Resp>  responseEntity = restTemplate.getForEntity(
          baseUrl + "?" + weatherApiQueryString,
          VVHanWeather1Resp.class);
      VVHanWeather1Resp vvHanWeatherResp = responseEntity.getBody();
      if (vvHanWeatherResp == null) {
        throw new BusinessException(Resp.fail(ErrorCode.SYSTEM_BUSY, "服务繁忙，请稍后再试。"));
      }
      WeatherVo weatherVo = new WeatherVo();
      if (vvHanWeatherResp.getWeather() != null) {
        weatherVo.setWeatherList(Collections.singletonList(vvHanWeatherResp.getWeather().to()));
      }
      if (vvHanWeatherResp.getAirQuality() != null) {
        weatherVo.setAir(vvHanWeatherResp.getAirQuality().to());
      }
      if (StrUtil.isNotEmpty(vvHanWeatherResp.getCity())) {
        weatherVo.setCity(vvHanWeatherResp.getCity());
      }
      return Resp.ok(weatherVo);
    }else{
      ResponseEntity<VVHanWeatherResp>  responseEntity = restTemplate.getForEntity(
          baseUrl + "?" + weatherApiQueryString,
          VVHanWeatherResp.class);
      VVHanWeatherResp vvHanWeatherResp = responseEntity.getBody();
      if (vvHanWeatherResp == null) {
        throw new BusinessException(Resp.fail(ErrorCode.SYSTEM_BUSY, "服务繁忙，请稍后再试。"));
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
      return Resp.ok(weatherVo);
    }
  }
}
