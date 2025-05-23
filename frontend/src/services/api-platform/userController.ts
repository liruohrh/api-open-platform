// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 GET /user */
export async function getLoginUser(options?: { [key: string]: any }) {
  return request<API.RespUser>('/user', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /user */
export async function updateUser(body: API.UserUpdateReq, options?: { [key: string]: any }) {
  return request<API.RespBoolean>('/user', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /user/${param0} */
export async function getUserById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserByIdParams,
  options?: { [key: string]: any },
) {
  const { userId: param0, ...queryParams } = params;
  return request<API.RespUserVo>(`/user/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 PUT /user/app-secret */
export async function resetUserAppSecret(options?: { [key: string]: any }) {
  return request<API.RespVoid>('/user/app-secret', {
    method: 'PUT',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/list */
export async function listUser(
  body: API.PageReqUserVoUserSortReq,
  options?: { [key: string]: any },
) {
  return request<API.RespPageRespUserVo>('/user/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/login */
export async function login(body: API.UserLoginReq, options?: { [key: string]: any }) {
  return request<API.RespUser>('/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.RespVoid>('/user/logout', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/passwd */
export async function newUserPasswd(body: API.UserNewPasswdReq, options?: { [key: string]: any }) {
  return request<API.RespVoid>('/user/passwd', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /user/register */
export async function register(body: API.UserRegisterReq, options?: { [key: string]: any }) {
  return request<API.RespVoid>('/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
