package io.github.liruohrh.apiplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apicommon.error.APIException;
import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.error.ErrorCode;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.common.util.RequestUtils;
import io.github.liruohrh.apiplatform.mapper.HttpApiMapper;
import io.github.liruohrh.apiplatform.model.enume.APIStatusEnum;
import io.github.liruohrh.apiplatform.model.enume.RoleEnum;
import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import io.github.liruohrh.apiplatform.model.req.api.APISortReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiAddReq;
import io.github.liruohrh.apiplatform.model.req.api.HttpApiUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.resp.api.HttpApiResp;
import io.github.liruohrh.apiplatform.service.ApiCallLogService;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.model.entity.ApiCall;
import io.github.liruohrh.model.entity.ApiCallLog;
import io.github.liruohrh.model.entity.HttpApi;
import io.github.liruohrh.model.entity.User;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author LYM
 * @description 针对表【http_api】的数据库操作Service实现
 * @createDate 2024-08-12 17:39:30
 */
@Slf4j
@Service
public class HttpApiServiceImpl extends ServiceImpl<HttpApiMapper, HttpApi>
    implements HttpApiService {

  private final ApiCallLogService apiCallLogService;
  private final ApiCallService apiCallService;
  private final RestTemplate restTemplate = new RestTemplateBuilder()
      .errorHandler(new ResponseErrorHandler() {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
          return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
        }
      })
      .build();

  public HttpApiServiceImpl(
      ApiCallService apiCallService,
      ApiCallLogService apiCallLogService
  ) {
    this.apiCallService = apiCallService;
    this.apiCallLogService = apiCallLogService;
  }

  public <T> PageResp<T> page(
      int current,
      int size,
      APISortReq sort,
      HttpApiResp search,
      Function<HttpApi, T> converter
  ) {
    LambdaQueryWrapper<HttpApi> queryWrapper = new LambdaQueryWrapper<>();
    if (search != null) {
      if (StrUtil.isNotEmpty(search.getName())) {
        queryWrapper.like(HttpApi::getName, search.getName());
        search.setName(null);
      }
      if (StrUtil.isNotEmpty(search.getDescription())) {
        queryWrapper.like(HttpApi::getDescription, search.getDescription());
        search.setDescription(null);
      }
      queryWrapper.setEntity(BeanUtil.copyProperties(search, HttpApi.class));
    }
    if (sort != null) {
      queryWrapper.orderBy(
          sort.getPrice() != null,
          SortEnum.ASC == sort.getPrice(),
          HttpApi::getPrice
      ).orderBy(
          sort.getOrderVolume() != null,
          SortEnum.ASC == sort.getOrderVolume(),
          HttpApi::getOrderVolume
      ).orderBy(
          sort.getScore() != null,
          SortEnum.ASC == sort.getScore(),
          HttpApi::getScore
      ).orderBy(
          sort.getCtime() != null,
          SortEnum.ASC == sort.getCtime(),
          HttpApi::getCtime
      ).orderBy(
          sort.getUtime() != null,
          SortEnum.ASC == sort.getUtime(),
          HttpApi::getUtime
      );
    }

    Page<HttpApi> pageResult = page(
        new Page<>(current, size),
        queryWrapper
    );
    PageResp<T> pageResp = new PageResp<>();
    pageResp.setData(pageResult.getRecords().stream()
        .map(converter).collect(Collectors.toList()));
    pageResp.setPages(pageResult.getPages());
    pageResp.setTotal(pageResult.getTotal());
    pageResp.setCurrent(pageResult.getCurrent());
    pageResp.setSize(pageResult.getSize());
    return pageResp;
  }


  @Override
  public Long addAPI(HttpApiAddReq httpApiAddReq) {
    HttpApi httpApi = BeanUtil.copyProperties(httpApiAddReq, HttpApi.class);
    if (httpApi.getPrice().equals(0.0)) {
      httpApi.setFreeTimes(-1);
    }
    httpApi.setOwnerId(LoginUserHolder.getUserId());
    httpApi.setStatus(APIStatusEnum.ROLL_OFF.getValue());

    if (exists(null, httpApi.getName())) {
      throw new BusinessException(Resp.fail(ErrorCode.ALREADY_EXISTS, "already has this API name"));
    }
    save(httpApi);
    return httpApi.getId();
  }

  @Override
  public void launchAPI(Long apiId) {
    validUpdate(apiId, LoginUserHolder.get());

    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<HttpApi>()
        .eq(HttpApi::getId, apiId)
        .set(HttpApi::getStatus, APIStatusEnum.LAUNCH.getValue())
    ));
  }

  @Override
  public void rollOffAPI(Long apiId) {
    validUpdate(apiId, LoginUserHolder.get());

    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<HttpApi>()
        .eq(HttpApi::getId, apiId)
        .set(HttpApi::getStatus, APIStatusEnum.ROLL_OFF.getValue())
    ));
  }

  @Override
  public void updateAPI(HttpApiUpdateReq httpApiUpdateReq) {
    User loginUser = LoginUserHolder.get();
    HttpApi updateHttpApi = BeanUtil.copyProperties(httpApiUpdateReq, HttpApi.class);

    HttpApi httpApi = validUpdate(updateHttpApi.getId(), loginUser);

    if (StrUtil.isNotEmpty(updateHttpApi.getName())) {
      if (updateHttpApi.getName().equals(httpApi.getName())) {
        updateHttpApi.setName(null);
      } else if (exists(null, updateHttpApi.getName())) {
        throw new BusinessException(
            Resp.fail(ErrorCode.ALREADY_EXISTS, "already has this API name"));
      }
    }
    MustUtils.dbSuccess(updateById(updateHttpApi));
  }

  private boolean exists(Long id, String name) {
    LambdaQueryWrapper<HttpApi> queryWrapper = new LambdaQueryWrapper<HttpApi>()
        .select(HttpApi::getId);
    if (id != null) {
      return getOne(queryWrapper
          .eq(HttpApi::getId, id)) != null;
    } else if (name != null) {
      return getOne(queryWrapper
          .eq(HttpApi::getName, name)) != null;
    }
    return false;
  }

  /**
   * 存在，且操作者必须是用户或者管理员
   */
  private HttpApi validUpdate(Long updateHttpApi, User loginUser) {
    HttpApi httpApi = getById(updateHttpApi);
    if (httpApi == null) {
      throw new ParamException(Resp.fail(ErrorCode.NOT_EXISTS, "API不存在"));
    }
    if (!loginUser.getId().equals(httpApi.getOwnerId()) && !RoleEnum.ADMIN.eq(
        loginUser.getRole())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    return httpApi;
  }

  @Override
  public void deleteAPI(Long apiId) {
    User loginUser = LoginUserHolder.get();

    validUpdate(apiId, loginUser);

    MustUtils.dbSuccess(removeById(apiId));
  }


  @Override
  public ResponseEntity<byte[]> debugCallAPI(Long apiId, HttpServletRequest req,
      HttpServletResponse resp) {
    User caller = LoginUserHolder.get();

    //检查调用权限
    HttpApi httpApi = getOne(new LambdaQueryWrapper<HttpApi>()
        .select(HttpApi::getMethod, HttpApi::getProtocol, HttpApi::getDomain, HttpApi::getPath,
            HttpApi::getPrice)
        .eq(HttpApi::getId, apiId)
        .eq(HttpApi::getStatus, APIStatusEnum.LAUNCH.getValue())
    );
    if (httpApi == null) {
      throw new ParamException("API不存在或者没上线");
    }
    ApiCall apiCall = apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
        .eq(ApiCall::getApiId, apiId)
        .eq(ApiCall::getCallerId, caller.getId())
    );
    if (apiCall == null) {
      throw new BusinessException(ErrorCode.NOT_EXISTS, "未下过订单");
    }
    if (!httpApi.getPrice().equals(0.0)) {
      //检查是否还有剩余次数
      if (apiCall.getLeftTimes() < 1) {
        throw new BusinessException(ErrorCode.HAS_NOT_MORE_TIMES);
      }
    }

    //call
    String appKey = caller.getAppKey();
    String appSecret = caller.getAppSecret();
    MultiValueMap<String, String> headers = RequestUtils.getHeaders2(req);
    headers.set("appKey", appKey);
    headers.set("appSecret", appSecret);
    String url = httpApi.getProtocol() + "://" + httpApi.getDomain() + httpApi.getPath() +
        (StrUtil.isEmpty(req.getQueryString()) ? "" : "?" + req.getQueryString());
    RequestEntity requestEntity;
    try {
      ServletInputStream inputStream = req.getInputStream();
      if (StrUtil.isEmpty(req.getHeader(HttpHeaders.CONTENT_TYPE))
          && inputStream.available() <= 0) {
        requestEntity = new RequestEntity<>(
            headers,
            HttpMethod.resolve(httpApi.getMethod().toUpperCase()),
            URI.create(url)
        );
      } else {
        requestEntity = new RequestEntity<>(
            IoUtil.readBytes(inputStream),
            headers,
            HttpMethod.resolve(httpApi.getMethod().toUpperCase()),
            URI.create(url)
        );
      }
    } catch (IOException e) {
      log.warn("用户{} debug API[{}], [{}, {}] 时, 读取用户请求失败",
          caller.getId(),
          apiId, httpApi.getMethod(), url
      );
      throw new APIException("read req fail");
    }

    long start = System.currentTimeMillis();
    ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
    long end = System.currentTimeMillis();

    afterCallAPI(
        responseEntity.getStatusCode().isError(),
        (int) (end - start),
        !httpApi.getPrice().equals(0.0),
        apiId, caller.getId()
    );

    log.info(
        "API Debug, remoteAddr={}, apiId={}, userId={}, statusCode={}, consumingMs={}, body={}",
        req.getRemoteAddr(), apiId, caller.getId(),
        responseEntity.getStatusCode(), end - start,
        MediaType.APPLICATION_JSON.getSubtype()
            .equals(responseEntity.getHeaders().getContentType().getSubtype())
            ? new String(responseEntity.getBody(), StandardCharsets.UTF_8)
            : "not json, but " + responseEntity.getHeaders().getContentType()
    );
    return responseEntity;
  }

  /**
   * 调用成功 且 不是免费时，扣费
   * 记录调用日志
   * 更新执行次数
   */
  @Override
  public void afterCallAPI(boolean isSuccess, int timeConsumingMs, Boolean isFreeAPI, Long apiId,
      Long callerId) {
    if (isSuccess && Boolean.FALSE.equals(isFreeAPI)) {
      MustUtils.dbSuccess(apiCallService.update(new LambdaUpdateWrapper<ApiCall>()
          .eq(ApiCall::getId, apiId)
          .eq(ApiCall::getCallerId, callerId)
          .setSql("left_times = left_times - 1")
      ));
    }
    MustUtils.dbSuccess(apiCallLogService.save(new ApiCallLog(
        null, apiId, callerId, isSuccess, timeConsumingMs
    )));
    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<HttpApi>()
        .eq(HttpApi::getId, apiId)
        .setSql("invoke_count = invoke_count + 1")
    ));
  }

}




