// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /order */
export async function createOrder(body: API.OrderCreateReq, options?: { [key: string]: any }) {
  return request<API.RespOrderVo>('/order', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /order/${param0} */
export async function getOrderById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getOrderByIdParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RespOrderVo>(`/order/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /order/${param0} */
export async function optForOrder(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.optForOrderParams,
  options?: { [key: string]: any },
) {
  const { orderId: param0, ...queryParams } = params;
  return request<API.RespVoid>(`/order/${param0}`, {
    method: 'PUT',
    params: {
      ...queryParams,
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /order/list */
export async function listOrder(
  body: API.PageReqOrderSearchReqOrderSortReq,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespOrderVo>('/order/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
