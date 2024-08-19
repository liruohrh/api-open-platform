package io.github.liruohrh.apiplatform.common.servlet;

import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.LoginUtils;
import io.github.liruohrh.apiplatform.common.util.RequestUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class LoginFilter extends OncePerRequestFilter {
  private final List<PathPattern> whiteList;
  private final RedisTemplate<Object,Object> redisTemplate;
  private final UserService userService;

  public LoginFilter(
      List<String> whiteList,
      UserService userService,
      RedisTemplate<Object,Object> redisTemplate

  ) {
    PathPatternParser pathPatternParser = new PathPatternParser();
    this.whiteList = whiteList.stream().map(pathPatternParser::parse).collect(Collectors.toList());
    this.redisTemplate = redisTemplate;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String method = request.getMethod();
    if(
        !(HttpMethod.GET.matches(method)
        || HttpMethod.POST.matches(method)
        || HttpMethod.PUT.matches(method)
         || HttpMethod.DELETE.matches(method))
    ){
      filterChain.doFilter(request, response);
      return;
    }

    Cookie cookie = RequestUtils.getCookie(request, CommonConstant.COOKIE_LOGIN_NAME);

    String requestURI = request.getRequestURI();
    requestURI = requestURI.replace(request.getContextPath(), "");
    PathContainer pathContainer = PathContainer.parsePath(requestURI);
    if(whiteList.stream().anyMatch(whitePath->whitePath.matches(pathContainer))){
//      if(cookie != null){
//        Long loginUserId = LoginUtils.getLoginState(cookie, redisTemplate);
//        if(loginUserId == null){
//          hasNotLogin(request, response);
//          return;
//        }
//        LoginUserHolder.set(loginUserId, ()->userService.getById(loginUserId));
//        return;
//      }
      filterChain.doFilter(request, response);
      return;
    }

    //必须登录
    if(cookie == null){
      hasNotLogin(request, response);
      return;
    }
    Long loginUserId = LoginUtils.getLoginState(cookie, redisTemplate);
    if(loginUserId == null){
      hasNotLogin(request, response);
      return;
    }
    LoginUserHolder.set(loginUserId, ()->userService.getById(loginUserId));
    filterChain.doFilter(request, response);
  }

  private void hasNotLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.sendError(HttpStatus.UNAUTHORIZED.value());
  }
}
