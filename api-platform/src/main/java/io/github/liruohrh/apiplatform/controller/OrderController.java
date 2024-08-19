package io.github.liruohrh.apiplatform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.model.enume.OrderStatusEnum;
import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import io.github.liruohrh.apiplatform.model.req.PageReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderCreateReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSearchReq;
import io.github.liruohrh.apiplatform.model.req.order.OrderSortReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.OrderVo;
import io.github.liruohrh.apiplatform.service.ApiCallService;
import io.github.liruohrh.apiplatform.service.HttpApiService;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.model.entity.ApiCall;
import io.github.liruohrh.model.entity.HttpApi;
import io.github.liruohrh.model.entity.Order;
import java.util.stream.Collectors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
@Validated
public class OrderController {

  private final Snowflake snowflake = IdUtil.getSnowflake(0, 0);

  private final OrderService orderService;
  private final HttpApiService httpApiService;
  private final ApiCallService apiCallService;

  public OrderController(
      OrderService orderService,
      HttpApiService httpApiService,
      ApiCallService apiCallService
  ) {
    this.orderService = orderService;
    this.httpApiService = httpApiService;
    this.apiCallService = apiCallService;
  }

  @GetMapping("/{orderId}")
  public Resp<OrderVo> getOrderById(@PathVariable("orderId") String orderId) {
    return Resp.ok(BeanUtil.copyProperties(orderService.getOne(new LambdaQueryWrapper<Order>()
        .eq(Order::getOrderId, orderId)), OrderVo.class));
  }

  @PostMapping("/list")
  public Resp<PageResp<OrderVo>> listOrder(
      @RequestBody PageReq<OrderSearchReq, OrderSortReq> pageReq) {
    if (pageReq.getCurrent() == null) {
      pageReq.setCurrent(1);
    }
    OrderSearchReq search = pageReq.getSearch();
    OrderSortReq sort = pageReq.getSort();
    LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
    if (search != null) {
      if (search.getPrice() != null) {
        search.getPrice().query(queryWrapper, Order::getPrice);
      }
      if (search.getCtime() != null) {
        search.getCtime().query(queryWrapper, Order::getCtime);
      }
      if (search.getUtime() != null) {
        search.getUtime().query(queryWrapper, Order::getUtime);
      }
      if (search.getAmount() != null) {
        search.getAmount().query(queryWrapper, Order::getAmount);
      }
      if (search.getActualPayment() != null) {
        search.getActualPayment().query(queryWrapper, Order::getActualPayment);
      }
      if (search.getStatus() != null) {
        queryWrapper.eq(Order::getStatus, search.getStatus());
      }
      if (search.getApiId() != null) {
        queryWrapper.eq(Order::getApiId, search.getApiId());
      }
    }
    if (sort != null) {
      queryWrapper.orderBy(sort.getCtime() != null, sort.getCtime() == SortEnum.ASC,
          Order::getCtime);
      queryWrapper.orderBy(sort.getUtime() != null, sort.getUtime() == SortEnum.ASC,
          Order::getUtime);
      queryWrapper.orderBy(sort.getActualPayment() != null, sort.getActualPayment() == SortEnum.ASC,
          Order::getActualPayment);
      queryWrapper.orderBy(sort.getAmount() != null, sort.getAmount() == SortEnum.ASC,
          Order::getAmount);
    }
    Page<Order> userPageQuery = orderService.page(
        new Page<>(pageReq.getCurrent(), CommonConstant.PAGE_MAX_SIZE_ORDER),
        queryWrapper
    );

    return Resp.ok(new PageResp<>(
        userPageQuery.getRecords().stream()
            .map(entity -> BeanUtil.copyProperties(entity, OrderVo.class))
            .collect(Collectors.toList()),
        userPageQuery.getTotal(),
        userPageQuery.getCurrent(),
        userPageQuery.getPages(),
        userPageQuery.getSize()
    ));
  }

  /**
   * 为付费接口完成操作，支付完则增加次数
   */
  @PutMapping("/{orderId}")
  public Resp<Void> optForOrder(
      @PathVariable("orderId") String orderId,
      @RequestParam(value = "isPay", required = false) Boolean isPay,
      @RequestParam(value = "isCancel", required = false) Boolean isCancel
  ) {
    if (ObjUtil.isAllEmpty(isPay, isCancel) || (Boolean.FALSE.equals(isPay) && Boolean.FALSE.equals(
        isCancel))) {
      throw new ParamException("支付还是取消订单?");
    }

    //更新订单状态
    Order order = orderService.getById(orderId);
    if (order == null) {
      throw new ParamException("订单不存在");
    }
    Long callerId = LoginUserHolder.getUserId();
    MustUtils.dbSuccess(orderService.update(new LambdaUpdateWrapper<Order>()
        .eq(Order::getOrderId, orderId)
        .eq(Order::getUserId, callerId)
        .set(Order::getStatus,
            Boolean.TRUE.equals(isPay) ? OrderStatusEnum.PAID.getValue()
                : OrderStatusEnum.CANCEL.getValue())
    ));

    //是支付 且 前面支付成功，则添加次数
    if (Boolean.TRUE.equals(isPay)) {
      Long apiId = order.getApiId();
      ApiCall apiCall = apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
          .eq(ApiCall::getApiId, apiId)
          .eq(ApiCall::getCallerId, callerId)
      );
      if (apiCall == null) {
        //第一次买，加apiCall
        ApiCall newApiCall = new ApiCall();
        newApiCall.setApiId(apiId);
        newApiCall.setCallerId(callerId);
        apiCallService.save(newApiCall);
      } else {
        //增加次数
        MustUtils.dbSuccess(apiCallService.update(new LambdaUpdateWrapper<ApiCall>()
            .eq(ApiCall::getApiId, apiId)
            .eq(ApiCall::getCallerId, callerId)
            .set(ApiCall::getLeftTimes, apiCall.getLeftTimes() + order.getAmount())
        ));
      }
    }
    return Resp.ok(null);
  }


  /**
   * 1.免费接口：直接完成订单，第一次没有ApiCall，第二次有ApiCall（此时抛出异常，不能购买多个免费接口）
   * 2.使用免费次数：直接完成订单，设置apiCall.freeUsed=true（一次为true就是重复购买免费次数，抛出异常），且添加次数
   * 3.购买使用次数：仅创建订单
   * @return
   */
  @PostMapping
  public Resp<OrderVo> createOrder(
      @Validated @RequestBody OrderCreateReq orderCreateReq) {
    Order order = null;
    Long apiId = orderCreateReq.getApiId();
    HttpApi httpApi = httpApiService.getOne(new LambdaQueryWrapper<HttpApi>()
        .select(HttpApi::getPrice, HttpApi::getFreeTimes)
        .eq(HttpApi::getId, apiId)
    );

    ApiCall apiCall = apiCallService.getOne(new LambdaQueryWrapper<ApiCall>()
        .eq(ApiCall::getApiId, apiId)
        .eq(ApiCall::getCallerId, LoginUserHolder.getUserId())
    );
    //免费接口，创建一个ApiCall，直接完成Order
    if (httpApi.getPrice().equals(0.0)) {
      //免费的，但第一次购买，因此apiCall为空
      if (apiCall == null) {
        ApiCall newApiCall = new ApiCall();
        newApiCall.setApiId(apiId);
        newApiCall.setCallerId(LoginUserHolder.getUserId());
        apiCallService.save(newApiCall);
        order = new Order();
        order.setApiId(apiId);
        order.setUserId(LoginUserHolder.getUserId());
        order.setStatus(OrderStatusEnum.PAID.getValue());
        order.setActualPayment(0.0);
        order.setAmount(0);
        order.setPrice(0.0);
        order.setOrderId(snowflake.nextIdStr());
        orderService.save(order);
      } else {
        //第二次购买免费接口，抛出异常
        throw new ParamException("不能重复购买免费接口");
      }
    } else {
      //如果免费次数没用，则用
      if (Boolean.TRUE.equals(orderCreateReq.getFree()) && (apiCall == null
          || !apiCall.getFreeUsed())) {
        if (apiCall == null) {
          ApiCall newApiCall = new ApiCall();
          newApiCall.setApiId(apiId);
          newApiCall.setCallerId(LoginUserHolder.getUserId());
          newApiCall.setFreeUsed(true);
          newApiCall.setLeftTimes(httpApi.getFreeTimes());
          apiCallService.save(newApiCall);
        }
        order = new Order();
        order.setApiId(apiId);
        order.setUserId(LoginUserHolder.getUserId());
        order.setStatus(OrderStatusEnum.PAID.getValue());
        order.setActualPayment(0.0);
        order.setAmount(0);
        order.setPrice(0.0);
        order.setOrderId(snowflake.nextIdStr());
        orderService.save(order);
      } else {
        //付费接口，保存后，等待完成订单时再添加次数
        Integer amount = orderCreateReq.getAmount();
        if (amount == null || amount < 0) {
          throw new ParamException("数量不能为空");
        }
        order = new Order();
        order.setApiId(apiId);
        order.setUserId(LoginUserHolder.getUserId());
        order.setStatus(OrderStatusEnum.WAIT_PAY.getValue());
        order.setActualPayment(0.0);
        order.setAmount(amount);
        order.setPrice(httpApi.getPrice());
        order.setOrderId(snowflake.nextIdStr());
        orderService.save(order);
      }
    }
    return Resp.ok(BeanUtil.copyProperties(orderService.getById(order.getId()), OrderVo.class));
  }

}
