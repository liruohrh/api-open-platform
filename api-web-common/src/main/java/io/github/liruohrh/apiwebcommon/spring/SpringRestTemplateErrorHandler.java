package io.github.liruohrh.apiwebcommon.spring;

import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.error.Resp;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class SpringRestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
      return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
      throw new BusinessException(new Resp<>(response.getRawStatusCode(), response.getStatusText(), null));
    }
  }