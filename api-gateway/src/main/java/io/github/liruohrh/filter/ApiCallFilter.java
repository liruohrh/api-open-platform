package io.github.liruohrh.filter;

import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.error.ErrorCode;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apicommon.utils.SignUtils;
import io.github.liruohrh.config.ApiGatewayProperties;
import io.github.liruohrh.model.dto.ApiCallInfoDto;
import io.github.liruohrh.model.entity.ApiCall;
import io.github.liruohrh.model.entity.HttpApi;
import io.github.liruohrh.model.entity.User;
import io.github.liruohrh.rpc.service.RpcHttpApiService;
import io.github.liruohrh.rpc.service.RpcUserService;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ApiCallFilter implements GlobalFilter, Ordered {

  @DubboReference
  private RpcUserService rpcUserService;
  @DubboReference
  private RpcHttpApiService rpcHttpApiService;

  private final ApiGatewayProperties.ReplayAttack replayAttack;
  private final RedissonClient redissonClient;

  public ApiCallFilter(
      ApiGatewayProperties apiGatewayProperties,
      RedissonClient redissonClient
  ) {
    this.replayAttack = apiGatewayProperties.getReplayAttack();
    this.redissonClient = redissonClient;
  }


  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest req = exchange.getRequest();
    if (!req.getPath().toString().startsWith("/api/")) {
      return chain.filter(exchange);
    }

    HttpHeaders headers = req.getHeaders();
    String appKey = headers.getFirst(SignUtils.KEY_APP_KEY);
    String nonce = headers.getFirst(SignUtils.KEY_NONCE);
    String timestamp = headers.getFirst(SignUtils.KEY_TIMESTAMP);
    String extra = headers.getFirst(SignUtils.KEY_EXTRA);
    String sign = headers.getFirst(SignUtils.KEY_SIGN);
    if (StringUtils.isAnyEmpty(
        appKey, nonce, timestamp, extra, sign
    )) {
      log.warn(
          "path=[{}], remoteAddr=[{}], reason=[签名头有些是空值], appKey=[{}], nonce=[{}], timestamp=[{}], extra=[{}], sign=[{}]",
          req.getPath(), req.getRemoteAddress(),
          appKey, nonce, timestamp, extra, sign
      );
      throw new ParamException("invalid call request headers");
    }
    //防重放
    //timestamp不能超过现在一定时间
    if (System.currentTimeMillis() - Long.parseLong(timestamp) > replayAttack.getMaxAliveTime()) {
      log.warn("path=[{}], remoteAddr=[{}], reason=[签名时间戳超过{}ms], timestamp=[{}]",
          req.getPath(), req.getRemoteAddress(),
          replayAttack.getMaxAliveTime(),
          timestamp
      );
      throw new ParamException("invalid call request headers");
    }
    //一段时间内，nonce只有一个
    RBucket<Object> nonceString = redissonClient.getBucket("apiplatform:replayattack:" + nonce);
    if (!nonceString.setIfAbsent(nonce, Duration.ofMillis(5 * 60 * 1000))) {
      log.warn("path=[{}], remoteAddr=[{}], reason=[nonce已存在], nonce=[{}]",
          req.getPath(), req.getRemoteAddress(), nonce
      );
      throw new ParamException("invalid call request headers");
    }

    User caller = rpcUserService.getUserByAppKey(appKey);
    if (caller == null) {
      log.warn("path=[{}], remoteAddr=[{}], reason=[根据appKey找不到用户], appKey=[{}]",
          req.getPath(), req.getRemoteAddress(), appKey
      );
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    String appSecret = caller.getAppSecret();
    if (!SignUtils.verify(
        appKey, appSecret,
        nonce, timestamp, extra, sign
    )) {
      log.warn(
          "path=[{}], remoteAddr=[{}], reason=[错误的sign], sign=[{}], appKey=[{}], userId=[{}]",
          req.getPath(), req.getRemoteAddress(), sign, appKey, caller.getId()
      );
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "非法调用");
    }

    //检查调用权限
    if (req.getMethod() == null) {
      throw new ParamException("请求无方法");
    }
    ApiCallInfoDto apiCallInfo = rpcHttpApiService.getApiCallInfo(
        req.getPath().toString(),
        req.getMethod().name(),
        caller.getId()
    );
    HttpApi httpApi = apiCallInfo.getHttpApi();
    ApiCall apiCall = apiCallInfo.getApiCall();
    if (httpApi == null) {
      throw new ParamException("API不存在或者没上线");
    }
    if (apiCall == null) {
      throw new BusinessException(ErrorCode.NOT_EXISTS, "未下过订单");
    }
    if (!httpApi.getPrice().equals(0.0)) {
      //检查是否还有剩余次数
      if (apiCall.getLeftTimes() < 1) {
        throw new BusinessException(ErrorCode.HAS_NOT_MORE_TIMES);
      }
    }
    return handlerResp(exchange, chain, caller, apiCallInfo);
  }

  /**
   * 处理响应
   */
  public Mono<Void> handlerResp(
      ServerWebExchange exchange,
      GatewayFilterChain chain,
      User caller,
      ApiCallInfoDto apiCallInfo
  ) {
    //todo 耗时计算有点不精确，以后再找转发开始的地方
    long start = System.currentTimeMillis();
    return chain.filter(exchange.mutate().response(
        new ServerHttpResponseDecorator(exchange.getResponse()
        ) {
          @Override
          public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            long end = System.currentTimeMillis();
            long timeConsumingMs = end - start;
            if (body instanceof Flux) {
              Flux<? extends DataBuffer> fluxBody = Flux.from(body);
              return super.writeWith(
                  fluxBody.map(bodyBuffer -> {
                    //调用成功，扣费
                    HttpApi httpApi = apiCallInfo.getHttpApi();
                    //todo 响应成功就是成功吗？会不会成功获取数据才算成功比较好？但这样会不会被刷接口
                    rpcHttpApiService.onCallAPI(true, (int) timeConsumingMs,
                        httpApi.getPrice().equals(0.0), httpApi.getId(), caller.getId());

                    byte[] bodyBytes = new byte[bodyBuffer.readableByteCount()];
                    bodyBuffer.read(bodyBytes);
                    DataBufferUtils.release(bodyBuffer);

                    String bodyString = null, remoteAddr = null;
                    if (getDelegate().getHeaders().getContentType() == null) {
                      bodyString = "content-type is null";
                    } else if (MediaType.APPLICATION_JSON.getSubtype()
                        .equals(getDelegate().getHeaders().getContentType().getSubtype())) {
                      bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
                    } else {
                      bodyString = "not json, but " + getDelegate().getHeaders().getContentType();
                    }
                    if (exchange.getRequest().getRemoteAddress() == null) {
                      remoteAddr = "RemoteAddress is null";
                    } else {
                      remoteAddr = exchange.getRequest().getRemoteAddress().toString();
                    }

                    log.info(
                        "API CALL, remoteAddr={}, apiId={}, userId={}, statusCode={}, consumingMs={}, body={}",
                        remoteAddr,
                        httpApi.getId(),
                        caller.getId(),
                        getDelegate().getStatusCode(),
                        end - start,
                        bodyString
                    );
                    return getDelegate().bufferFactory().wrap(bodyBytes);
                  }));
            }else{
              return super.writeWith(body);
            }
          }
        }).build());
  }

  @Override
  public int getOrder() {
    return -2;
  }
}
