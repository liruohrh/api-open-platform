package io.github.liruohrh.apiplatform.config;

import io.github.liruohrh.apiplatform.common.servlet.LoginFilter;
import io.github.liruohrh.apiplatform.common.servlet.SinglePageHistoryModeRedirectFilter;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.controller.OssController;
import io.github.liruohrh.apiplatform.service.UserService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.PathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

  /**
   * @issue 无法跨域携带cookie
   * @note 较新的浏览器强制要求设置SameSite来控制跨域。
   * StandardContext#startInternal()会创建一个默认的CookieProcessor UNSET即不设置。
   * TomcatServletWebServerFactory#configureCookieProcessor 会给session设置一个，或者注入了CookieSameSiteSupplier时。
   * 直接用TomcatContextCustomizer设置一个也行。
   */
  @Bean
  public CookieSameSiteSupplier cookieSameSiteSupplier() {
    return CookieSameSiteSupplier.ofNone()
        .whenHasName(CommonConstant.COOKIE_LOGIN_NAME);
  }

  @Bean
  public WebMvcConfigurer webMvcConfigurer(){
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:8000",
                "http://127.0.0.1:8000",
                "http://localhost",
                "http://127.0.0.1"
            )
            .allowedMethods("GET", "POST", "DELETE", "PUT")
            .allowCredentials(true)
            .allowedHeaders("*")
            .exposedHeaders("*")
            .maxAge(10 * 60 * 10);
      }

      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/oss/static/**")
            .addResourceLocations(new PathResource(OssController.ROOT_DIR));
      }
    };
  }
  @Bean
  public FilterRegistrationBean<LoginFilter> loginFilter(
      APIPlatformProperties apiPlatformProperties,
      UserService userService,
      RedisTemplate<Object,Object> redisTemplate
  ){
    FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new LoginFilter(
        apiPlatformProperties.getLogin().getWhiteList(),
        userService,
        redisTemplate
    ));
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE+200);
    return registrationBean;
  }
  @Bean
  public FilterRegistrationBean<SinglePageHistoryModeRedirectFilter> singlePageHistoryModeRedirectFilter(){
    FilterRegistrationBean<SinglePageHistoryModeRedirectFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new SinglePageHistoryModeRedirectFilter());
    registrationBean.addUrlPatterns("/pages/*");
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
    return registrationBean;
  }
}
