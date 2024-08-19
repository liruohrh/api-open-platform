package io.github.liruohrh.apiplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.liruohrh.model.entity.HttpApi;
import io.github.liruohrh.apiplatform.model.req.api.APISortReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiAddReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

/**
 * @author LYM
 * @description 针对表【http_api】的数据库操作Service
 * @createDate 2024-08-12 17:39:30
 */
public interface HttpApiService extends IService<HttpApi> {
  <T> PageResp<T> page(
      int current,
      int size,
      APISortReq sort,
      HttpApiResp search,
      Function<HttpApi, T> converter
  );
  Long addAPI(HttpApiAddReq httpApiAddReq);

  void launchAPI(Long apiId);

  void rollOffAPI(Long apiId);

  void updateAPI(HttpApiUpdateReq httpApiUpdateReq);

  void deleteAPI(Long apiId);

  ResponseEntity<byte[]> debugCallAPI(Long apiId, HttpServletRequest req, HttpServletResponse resp);
  void afterCallAPI(boolean isSuccess, int timeConsumingMs, Boolean isFreeAPI, Long apiId, Long callerId);
}
