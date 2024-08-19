package io.github.liruohrh.filter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
//@Component
public class DemoChangeRespFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return chain.filter(exchange.mutate().response(
        new ServerHttpResponseDecorator(exchange.getResponse()
        ) {
          // 等待响应时，在写入resp时才执行，如果响应
          @Override
          public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            if (body instanceof Flux) {
              if(getDelegate().getStatusCode().isError()){
                //这时才有响应，才做处理
              }
              Flux<? extends DataBuffer> fluxBody = Flux.from(body);
              return super.writeWith(
                  fluxBody.map(bodyBuffer -> {
                    try {
//                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                    } catch (Exception e) {
                      log.error("invokeCount error", e);
                    }
                    byte[] bodyBytes = new byte[bodyBuffer.readableByteCount()];
                    bodyBuffer.read(bodyBytes);
                    DataBufferUtils.release(bodyBuffer);//释放掉内存
                    // 构建日志
                    StringBuilder sb2 = new StringBuilder(200);
                    List<Object> rspArgs = new ArrayList<>();
                    rspArgs.add(getDelegate().getStatusCode());
                    String data = new String(bodyBytes, StandardCharsets.UTF_8); //data
                    sb2.append(data);
                    // 打印日志
                    log.info("响应结果：" + data);
                    return getDelegate().bufferFactory().wrap(bodyBytes);
                  }));
            }
            return super.writeWith(body);
          }
        }).build());
  }


  @Override
  public int getOrder() {
    return -2;
  }
}
