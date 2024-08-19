package io.github.liruohrh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.liruohrh.apicommon.error.ErrorCode;
import io.github.liruohrh.apicommon.error.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * 要注意，这里无法处理错误响应，需要在  NettyWriteResponseFilter(Order=-1) 之前的 GlobalFilter 才能处理响应
 */
@Order(-1)
@Component
@Slf4j
public class GlobalWebExceptionHandler implements WebExceptionHandler {


  private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder
      .json()
      .build();

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    ServerHttpResponse response = exchange.getResponse();
    if (response.isCommitted()) {
      return Mono.error(ex);
    }
    HttpHeaders headers = response.getHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    if (ex instanceof ResponseStatusException) {
      ResponseStatusException e = (ResponseStatusException) ex;
      response.setStatusCode(e.getStatus());
    } else {
      response.setStatusCode(HttpStatus.FORBIDDEN);
    }
    DataBufferFactory bufferFactory = response.bufferFactory();

    Resp<Void> resp = Resp.fail(ErrorCode.GATEWAY, ex.getMessage());
    log.error("ErrorCode={}", resp.getCode(), ex);
    try {
      DataBuffer dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(resp));
      return response.writeWith(Mono.just(dataBuffer));
    } catch (JsonProcessingException e) {
      log.error("", e);
      return Mono.error(e);
    }
  }
}
