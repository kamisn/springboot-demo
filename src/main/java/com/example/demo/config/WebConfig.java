package com.example.demo.config;

import com.example.demo.JwtInterceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                //addPathPatterns("/**")
                //   拦截所有请求。
                .addPathPatterns("/**")
//        excludePathPatterns("/user/login", "/user/register")
//        登录和注册接口放行，不需要 token。
                .excludePathPatterns(
                        "/user/login",
                        "/user/register"
                );
    }
}
