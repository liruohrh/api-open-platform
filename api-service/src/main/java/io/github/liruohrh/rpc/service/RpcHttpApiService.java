package io.github.liruohrh.rpc.service;

import io.github.liruohrh.model.dto.ApiCallInfoDto;

public interface RpcHttpApiService {
  ApiCallInfoDto getApiCallInfo(String path, String method, Long userId);

  void onCallAPI(boolean isSuccess, int timeConsumingMs,  Boolean isFreeAPI,Long apiId, Long callerId);
}
