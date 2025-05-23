declare namespace API {
  type ApiCallResp = {
    status?: number;
    body?: string;
    headers?: { all?: Record<string, any>; empty?: boolean };
  };

  type ApiSearchSortReq = {
    price?: 'DESC' | 'ASC';
    orderVolume?: 'DESC' | 'ASC';
    score?: 'DESC' | 'ASC';
    ctime?: 'DESC' | 'ASC';
  };

  type ApiSearchVo = {
    id?: number;
    logoUrl?: string;
    name?: string;
    description?: string;
    price?: number;
    orderVolume?: number;
    score?: number;
    ctime?: number;
  };

  type APISortReq = {
    price?: 'DESC' | 'ASC';
    orderVolume?: 'DESC' | 'ASC';
    score?: 'DESC' | 'ASC';
    ctime?: 'DESC' | 'ASC';
    utime?: 'DESC' | 'ASC';
  };

  type callAPIParams = {
    apiId: number;
  };

  type deleteAPIParams = {
    apiId: number;
  };

  type getAPIByIdParams = {
    apiId: number;
  };

  type getCaptchaParams = {
    email: string;
  };

  type getOrderByIdParams = {
    orderId: string;
  };

  type getUserByIdParams = {
    userId: number;
  };

  type HttpApiAddReq = {
    description: string;
    logoUrl?: string;
    name: string;
    domain?: string;
    protocol: string;
    path: string;
    method: 'GET' | 'HEAD' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'TRACE';
    params?: string;
    reqHeaders?: string;
    reqBody?: string;
    respHeaders?: string;
    respBody?: string;
    respSuccess?: string;
    respFail?: string;
    errorCodes?: string;
    price: number;
    freeTimes?: number;
  };

  type HttpApiResp = {
    id?: number;
    ownerId?: number;
    description?: string;
    name?: string;
    logoUrl?: string;
    url?: string;
    method?: string;
    protocol?: string;
    domain?: string;
    path?: string;
    params?: string;
    reqHeaders?: string;
    reqBody?: string;
    respHeaders?: string;
    respBody?: string;
    respSuccess?: string;
    respFail?: string;
    errorCodes?: string;
    price?: number;
    freeTimes?: number;
    completedCount?: number;
    commentCount?: number;
    status?: number;
    ctime?: number;
    utime?: number;
  };

  type HttpApiUpdateReq = {
    id: number;
    description?: string;
    logoUrl?: string;
    name?: string;
    domain?: string;
    protocol: string;
    path: string;
    method?: 'GET' | 'HEAD' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'TRACE';
    params?: string;
    reqHeaders?: string;
    reqBody?: string;
    respHeaders?: string;
    respBody?: string;
    respSuccess?: string;
    respFail?: string;
    errorCodes?: string;
    price?: number;
    freeTimes?: number;
  };

  type launchAPIParams = {
    apiId: number;
  };

  type optForOrderParams = {
    orderId: string;
    isPay?: boolean;
    isCancel?: boolean;
  };

  type OrderCreateReq = {
    apiId: number;
    amount?: number;
  };

  type OrderNumber = {
    value?: number;
    isBefore?: boolean;
  };

  type OrderSearchReq = {
    apiId?: number;
    actualPayment?: OrderNumber;
    amount?: OrderNumber;
    status?: number;
    price?: OrderNumber;
    ctime?: OrderNumber;
    utime?: OrderNumber;
  };

  type OrderSortReq = {
    ctime?: 'DESC' | 'ASC';
    actualPayment?: 'DESC' | 'ASC';
    amount?: 'DESC' | 'ASC';
    utime?: 'DESC' | 'ASC';
  };

  type OrderVo = {
    id?: number;
    orderId?: string;
    apiId?: number;
    userId?: number;
    actualPayment?: number;
    amount?: number;
    status?: number;
    price?: number;
    ctime?: number;
    utime?: number;
  };

  type PageReqHttpApiRespAPISortReq = {
    search?: HttpApiResp;
    current?: number;
    size?: number;
    sort?: APISortReq;
  };

  type PageReqOrderSearchReqOrderSortReq = {
    search?: OrderSearchReq;
    current?: number;
    size?: number;
    sort?: OrderSortReq;
  };

  type PageReqStringApiSearchSortReq = {
    search?: string;
    current?: number;
    size?: number;
    sort?: ApiSearchSortReq;
  };

  type PageReqUserVoUserSortReq = {
    search?: UserVo;
    current?: number;
    size?: number;
    sort?: UserSortReq;
  };

  type PageRespApiSearchVo = {
    data?: ApiSearchVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespHttpApiResp = {
    data?: HttpApiResp[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespOrderVo = {
    data?: OrderVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type PageRespUserVo = {
    data?: UserVo[];
    total?: number;
    current?: number;
    pages?: number;
    size?: number;
  };

  type RespApiCallResp = {
    code?: number;
    msg?: string;
    data?: ApiCallResp;
  };

  type RespBoolean = {
    code?: number;
    msg?: string;
    data?: boolean;
  };

  type RespHttpApiResp = {
    code?: number;
    msg?: string;
    data?: HttpApiResp;
  };

  type RespLong = {
    code?: number;
    msg?: string;
    data?: number;
  };

  type RespOrderVo = {
    code?: number;
    msg?: string;
    data?: OrderVo;
  };

  type RespPageRespApiSearchVo = {
    code?: number;
    msg?: string;
    data?: PageRespApiSearchVo;
  };

  type RespPageRespHttpApiResp = {
    code?: number;
    msg?: string;
    data?: PageRespHttpApiResp;
  };

  type RespPageRespOrderVo = {
    code?: number;
    msg?: string;
    data?: PageRespOrderVo;
  };

  type RespPageRespUserVo = {
    code?: number;
    msg?: string;
    data?: PageRespUserVo;
  };

  type RespUser = {
    code?: number;
    msg?: string;
    data?: User;
  };

  type RespUserVo = {
    code?: number;
    msg?: string;
    data?: UserVo;
  };

  type RespVoid = {
    code?: number;
    msg?: string;
    data?: Record<string, any>;
  };

  type rollOffAPIParams = {
    apiId: number;
  };

  type User = {
    id?: number;
    nickname?: string;
    username?: string;
    passwd?: string;
    email?: string;
    personalDescription?: string;
    avatarUrl?: string;
    appKey?: string;
    appSecret?: string;
    apiPrefix?: string;
    role?: string;
    status?: number;
    ctime?: number;
    deleted?: number;
  };

  type UserLoginReq = {
    loginType: 'CODE' | 'PASSWD';
    username?: string;
    passwd?: string;
    email?: string;
    captcha?: string;
  };

  type UserNewPasswdReq = {
    newPasswd?: string;
    email?: string;
    captcha?: string;
  };

  type UserRegisterReq = {
    nickname: string;
    username?: string;
    passwd?: string;
    email?: string;
    captcha?: string;
  };

  type UserSortReq = {
    ctime?: 'DESC' | 'ASC';
  };

  type UserUpdateReq = {
    id: number;
    avatarUrl?: string;
    nickname?: string;
    username?: string;
    personalDescription?: string;
    email?: string;
    captcha?: string;
    role?: string;
    status?: number;
  };

  type UserVo = {
    id?: number;
    nickname?: string;
    username?: string;
    email?: string;
    personalDescription?: string;
    avatarUrl?: string;
    appKey?: string;
    role?: string;
    status?: number;
    ctime?: number;
  };
}
