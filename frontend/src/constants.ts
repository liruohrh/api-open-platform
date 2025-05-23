export const isDev = () => {
  return process.env.NODE_ENV === 'development';
};
export const getBackendBaseURL = () => {
  if (isDev()) {
    return 'http://127.0.0.1:9000/api-platform';
  } else {
    return 'http://127.0.0.1:8080';
  }
};
export const loginPath = '/user/login';
export const loginWhiteList = ['/api/info', '/api/list', '/user/register', '/welcome', '/'];
export const loginWhiteListRegexps = [/\/api\/info\/\w+/];
export const NeedLogin = (path: string): boolean => {
  if (
    loginWhiteList.some((nlPath) => path === nlPath || path === nlPath + '/') ||
    loginWhiteListRegexps.some((regexp) => path.match(regexp))
  ) {
    return false;
  }
  return true;
};
