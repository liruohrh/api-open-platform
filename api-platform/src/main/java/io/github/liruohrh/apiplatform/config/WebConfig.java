package io.github.liruohrh.apiplatform.config;

import io.github.liruohrh.apiplatform.common.servlet.HolderFilter;
import io.github.liruohrh.apiplatform.common.servlet.LoginFilter;
import io.github.liruohrh.apiplatform.common.servlet.SinglePageHistoryModeRedirectFilter;
import io.github.liruohrh.apiplatform.controller.OssController;
import io.github.liruohrh.apiplatform.service.UserService;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.PathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
  @Bean
  public TomcatContextCustomizer sameSiteCookiesConfig() {
    return context -> {
      final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
      cookieProcessor.setSameSiteCookies(SameSiteCookies.NONE.getValue());
      context.setCookieProcessor(cookieProcessor);
    };
  }

  @Bean
  public WebMvcConfigurer webMvcConfigurer(){
    return new WebMvcConfigurer() {
//      @Override
//      public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            .allowedOrigins(
//                "http://localhost:8000",
//                "http://127.0.0.1:8000",
//                "http://localhost",
//                "http://127.0.0.1"
//            )
//            .allowedMethods("GET", "POST", "DELETE", "PUT")
//            .allowCredentials(true)
//            .allowedHeaders("*")
//            .exposedHeaders("*")
//            .maxAge(10 * 60 * 10);
//      }

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
  public FilterRegistrationBean<HolderFilter> holderFilterFilter(){
    FilterRegistrationBean<HolderFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new HolderFilter());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
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
