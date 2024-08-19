package io.github.liruohrh.model.dto;

import io.github.liruohrh.model.entity.ApiCall;
import io.github.liruohrh.model.entity.HttpApi;
import java.io.Serializable;
import lombok.Data;

@Data
public class ApiCallInfoDto implements Serializable {
  private HttpApi httpApi;
  private ApiCall apiCall;

  public ApiCallInfoDto(HttpApi httpApi, ApiCall apiCall) {
    this.httpApi = httpApi;
    this.apiCall = apiCall;
  }
  private static final long serialVersionUID = 1L;
}
