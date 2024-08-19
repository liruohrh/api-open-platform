package io.github.liruohrh.apiplatform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import io.github.liruohrh.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.aop.PreAuth;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.req.PageReq;
import io.github.liruohrh.apiplatform.model.req.api.APISortReq;
import io.github.liruohrh.apiplatform.model.req.api.ApiSearchSortReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiAddReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.resp.api.ApiCallResp;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import io.github.liruohrh.apiplatform.model.vo.ApiSearchVo;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@PreAuth(mustRole = "ADMIN")
@RequestMapping("/http-api")
@RestController
public class APIController {

  private final HttpApiService httpApiService;
  private final ApiCallService apiCallService;
  private final UserService userService;
  public APIController(
      HttpApiService httpApiService,
      ApiCallService apiCallService,
      UserService userService
  ) {
    this.httpApiService = httpApiService;
    this.apiCallService = apiCallService;
    this.userService = userService;
  }


  @PostMapping("/search")
  public Resp<PageResp<ApiSearchVo>> searchAPI(
      @RequestBody PageReq<String, ApiSearchSortReq> pageReq) {
    return Resp.ok(httpApiService.page(
        pageReq.getCurrent() == null ? 1 : pageReq.getCurrent(),
        CommonConstant.PAGE_MAX_SIZE_API_SEARCH,
        pageReq.getSort() == null ? null : pageReq.getSort().toAPISortReq(),
        pageReq.getSearch() == null ? null : HttpApiResp.of(pageReq.getSearch()),
        r -> BeanUtil.copyProperties(r, ApiSearchVo.class)
    ));
  }


  /**
   * 分情况
   * 1. 用户可以上传：按用户名前缀
   * 这种情况下，用户需要自己上传token，自己验证token
   * 2. 仅管理员上传，直接转发 （当前模式）
   * 3. /demo 路径，在本项目中
   */
  @GetMapping("/debug/{apiId}")
  public Resp<ApiCallResp> callAPI(@PathVariable("apiId") Long apiId, HttpServletRequest req,
      HttpServletResponse resp) {

   ResponseEntity<byte[]>  responseEntity = httpApiService.debugCallAPI(apiId, req, resp);
    ApiCallResp apiCallResp = new ApiCallResp();
    if (ArrayUtil.isNotEmpty(responseEntity.getBody())) {
      apiCallResp.setBody(Base64.getEncoder().encodeToString(responseEntity.getBody()));
    }
    apiCallResp.setStatus(responseEntity.getStatusCodeValue());
    apiCallResp.setHeaders(responseEntity.getHeaders());
    return Resp.ok(apiCallResp);
  }

  @GetMapping("/{apiId}")
  public Resp<HttpApiResp> getAPIById(@PathVariable("apiId") Long apiId) {
    return Resp.ok(BeanUtil.copyProperties(httpApiService.getById(apiId), HttpApiResp.class));
  }

  @PostMapping("/list")
  public Resp<PageResp<HttpApiResp>> listAPI(
      @RequestBody PageReq<HttpApiResp, APISortReq> pageReq) {
    return Resp.ok(httpApiService.page(
        pageReq.getCurrent() == null ? 1 : pageReq.getCurrent(),
        CommonConstant.PAGE_MAX_SIZE_API,
        pageReq.getSort(),
        pageReq.getSearch(),
        r -> BeanUtil.copyProperties(r, HttpApiResp.class)
    ));
  }

  @PostMapping
  public Resp<Long> addAPI(@RequestBody HttpApiAddReq httpApiAddReq) {
    return Resp.ok(httpApiService.addAPI(httpApiAddReq));
  }

  @PostMapping("/{apiId}/launch")
  public Resp<Void> launchAPI(@PathVariable("apiId") Long apiId) {
    httpApiService.launchAPI(apiId);
    return Resp.ok(null);
  }

  @PostMapping("/{apiId}/roll-off")
  public Resp<Void> rollOffAPI(@PathVariable("apiId") Long apiId) {
    httpApiService.rollOffAPI(apiId);
    return Resp.ok(null);
  }

  @PutMapping
  public Resp<Void> updateAPI(@RequestBody HttpApiUpdateReq httpApiUpdateReq) {
    httpApiService.updateAPI(httpApiUpdateReq);
    return Resp.ok(null);
  }

  @DeleteMapping("/{apiId}")
  public Resp<Void> deleteAPI(@PathVariable("apiId") Long apiId) {
    httpApiService.deleteAPI(apiId);
    return Resp.ok(null);
  }
}
