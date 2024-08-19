package io.github.liruohrh.apiplatform.rpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.liruohrh.apiplatform.service.ApiCallLogService;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.model.dto.ApiCallInfoDto;
import io.github.liruohrh.model.entity.ApiCall;
import io.github.liruohrh.model.entity.HttpApi;
import io.github.liruohrh.rpc.service.RpcHttpApiService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class RpcHttpApiServiceImpl implements RpcHttpApiService {
    private final HttpApiService httpApiService;
    private final ApiCallService apiCallService;
    private final ApiCallLogService apiCallLogService;

  public RpcHttpApiServiceImpl(
      HttpApiService httpApiService,
      ApiCallService apiCallService,
      ApiCallLogService apiCallLogService
  ) {
    this.httpApiService = httpApiService;
    this.apiCallService = apiCallService;
    this.apiCallLogService = apiCallLogService;
  }

  public ApiCallInfoDto getApiCallInfo(String path, String method, Long userId){
    HttpApi httpApi = httpApiService.getOne(new LambdaQueryWrapper<HttpApi>()
        .eq(HttpApi::getPath, path)
        .eq(HttpApi::getMethod, method)
    );
    return new ApiCallInfoDto(
        httpApi,
        apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
            .eq(ApiCall::getApiId, httpApi.getId())
            .eq(ApiCall::getCallerId, userId)
        )
    );
  }

  @Override
  public void onCallAPI(boolean isSuccess, int timeConsumingMs,  Boolean isFreeAPI,Long apiId, Long callerId) {
    httpApiService.afterCallAPI(isSuccess, timeConsumingMs, isFreeAPI, apiId, callerId);
  }
}
