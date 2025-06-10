package io.github.liruohrh.apiwebcommon.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtils {
  public static HttpServletRequest getRequest()
  {
    return getRequestAttributes().getRequest();
  }
  public static HttpServletResponse getResponse()
  {
    return getRequestAttributes().getResponse();
  }
  public static HttpSession getSession()
  {
    return getRequest().getSession();
  }

  /**
   * org.springframework.web.filter.RequestContextFilter。
   * 默认是OrderedRequestContextFilter（一般在倒数第二个）。
   *    WebMvcAutoConfigurationAdapter#requestContextFilter()
   * 如果有业务Filter需要用，则自定义注入OrderedRequestContextFilter，但更推荐在业务Filter直接使用请求、响应对象。
   * @return
   */
  public static ServletRequestAttributes getRequestAttributes()
  {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    return (ServletRequestAttributes) attributes;
  }

}
