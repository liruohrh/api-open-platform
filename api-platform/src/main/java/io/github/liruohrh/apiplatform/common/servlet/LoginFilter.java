package io.github.liruohrh.apiplatform.common.servlet;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.LoginUtils;
import io.github.liruohrh.apiwebcommon.utils.RequestUtils ;
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
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class LoginFilter extends OncePerRequestFilter {
  @Data
  static class ResourceMatcher{
    private HttpMethod method;
    private PathPattern pattern;
  }
  private final List<ResourceMatcher> whiteList;
  private final RedisTemplate<Object,Object> redisTemplate;
  private final UserService userService;

  public LoginFilter(
      List<String> whiteList,
      UserService userService,
      RedisTemplate<Object,Object> redisTemplate

  ) {
    PathPatternParser pathPatternParser = new PathPatternParser();
    this.whiteList = whiteList.stream().map(white->{
          String[] split = white.split(",");
          ResourceMatcher resourceMatcher = new ResourceMatcher();
          if(split.length == 1){
            resourceMatcher.setMethod(null);
            resourceMatcher.setPattern(pathPatternParser.parse(white));
          }else{
            resourceMatcher.setMethod(HttpMethod.resolve(split[0]));
            resourceMatcher.setPattern(pathPatternParser.parse(split[1]));
          }
          return resourceMatcher;
        })
        .collect(Collectors.toList());
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
    boolean needLogin = whiteList.stream().noneMatch(whitePath ->
        (whitePath.getMethod() == null || whitePath.getMethod().matches(method))
            && whitePath.getPattern().matches(pathContainer)
    );
    if(cookie == null && needLogin){
      hasNotLogin(request, response);
      return;
    }

    try {
      if(cookie != null) {
        Long loginUserId = null;
        if(needLogin){
          loginUserId = LoginUtils.getLoginState(cookie, redisTemplate);
          if (loginUserId == null) {
            hasNotLogin(request, response);
            return;
          }
        }
        final Long _loginUserId = loginUserId;
        final String apiToken = cookie.getValue();
        Supplier<Long> loginUserIdGetter = Suppliers.memoize(() -> _loginUserId == null ? null
            : LoginUtils.getLoginState(apiToken, redisTemplate));
        LoginUserHolder.set(
            needLogin,
            loginUserIdGetter,
            Suppliers.memoize(() -> loginUserIdGetter.get() == null ? null : userService.getById(loginUserIdGetter.get()))
        );
      }
      filterChain.doFilter(request, response);
    }finally{
      LoginUserHolder.clear();
    }
  }


  private void hasNotLogin(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.sendError(HttpStatus.UNAUTHORIZED.value());
  }
}
