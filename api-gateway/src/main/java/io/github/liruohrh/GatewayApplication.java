package io.github.liruohrh;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@EnableDubbo
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class})
public class GatewayApplication {

  public static void main(String[] args) {
    new SpringApplication(GatewayApplication.class).run(args);
  }
  @Bean
  public CorsWebFilter corsWebFilter(){
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    //1、配置跨域
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addExposedHeader("*");

    corsConfiguration.addAllowedMethod(HttpMethod.GET);
    corsConfiguration.addAllowedMethod(HttpMethod.POST);
    corsConfiguration.addAllowedMethod(HttpMethod.PUT);
    corsConfiguration.addAllowedMethod(HttpMethod.DELETE);

    corsConfiguration.addAllowedOrigin( "http://localhost:8000");
    corsConfiguration.addAllowedOrigin( "http://127.0.0.1:8000");
    corsConfiguration.addAllowedOrigin( "http://localhost");
    corsConfiguration.addAllowedOrigin( "http://127.0.0.1");
    corsConfiguration.setAllowCredentials(true);

    corsConfiguration.setMaxAge(10L * 60 * 10);

    source.registerCorsConfiguration("/**",corsConfiguration);
    return new CorsWebFilter(source);
  }
}
