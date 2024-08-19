package io.github.liruohrh.apiplatform.common.util;

import cn.hutool.crypto.digest.DigestUtil;
import io.github.liruohrh.apiplatform.common.holder.RequestHolder;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.constant.RedisConstant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;

public class LoginUtils {
  public static Long getLoginState(
      String apiToken,
      RedisTemplate<Object, Object> redisTemplate
  ) {
    Number loginUserId = (Number) redisTemplate.opsForValue()
        .get(RedisConstant.PREFIX_LOGIN + apiToken);
    if(loginUserId == null ) {
      return null;
    }
    return loginUserId.longValue();
  }

  public static Long getLoginState(
      Cookie cookie,
      RedisTemplate<Object, Object> redisTemplate
  ) {
    Number loginUserId = (Number) redisTemplate.opsForValue()
        .get(RedisConstant.PREFIX_LOGIN + cookie.getValue());
    if(loginUserId == null ) {
      return null;
    }
    return loginUserId.longValue();
  }


  public static boolean clearLoginState(
      RedisTemplate<Object, Object> redisTemplate
  ) {
    String apiToken = getLoginIdentity(RequestHolder.get());
    if (apiToken == null) {
      return false;
    }

    Object loginUserId = redisTemplate.opsForValue()
        .getAndDelete(RedisConstant.PREFIX_LOGIN + apiToken);
    RequestHolder.getResp().addCookie(getCookie(apiToken, 0));
    return loginUserId != null;
  }

  private static String getLoginIdentity(HttpServletRequest req) {
    Cookie cookie = RequestUtils.getCookie(req, CommonConstant.COOKIE_LOGIN_NAME);
    if (cookie == null) {
      return null;
    }
    return cookie.getValue();
  }

  public static void setLoginState(
      RedisTemplate<Object, Object> redisTemplate,
      Long userId
  ) {
    byte[] bytes1 = DigestUtil.md5(userId + "");
    byte[] bytes2 = DigestUtil.md5(bytes1);
    byte[] bytes = new byte[bytes2.length + bytes2.length];
    System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
    System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
    String apiToken = Base64.getEncoder().encodeToString(bytes);

    redisTemplate.opsForValue().set(RedisConstant.PREFIX_LOGIN + apiToken, userId, RedisConstant.EXPIRE_LOGIN, TimeUnit.MILLISECONDS);

    HttpServletResponse resp = RequestHolder.getResp();
//    ResponseCookie loginStateCookie = ResponseCookie.from(CommonConstant.COOKIE_LOGIN_NAME, apiToken)
//        .sameSite(SameSite.NONE.attributeValue())
//        .secure(true)
//        .httpOnly(true)
//        .maxAge(Duration.ofMillis(RedisConstant.EXPIRE_LOGIN))
//        .build();
//    resp.setHeader(HttpHeaders.SET_COOKIE, loginStateCookie.toString());

    resp.addCookie(getCookie(apiToken, (int) (RedisConstant.EXPIRE_LOGIN / 1000)));
  }
  private static Cookie getCookie(String value, int maxAge){
    Cookie loginStateCookie = new Cookie(CommonConstant.COOKIE_LOGIN_NAME, value);
    loginStateCookie.setSecure(true);
    loginStateCookie.setPath("/");
    loginStateCookie.setHttpOnly(true);
    loginStateCookie.setMaxAge(maxAge);
    return loginStateCookie;
  }
}
