package com.example.demo.config;

import com.example.demo.JwtInterceptor.JwtInterceptor;
import com.example.demo.interceptor.AdminInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final AdminInterceptor adminInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor, AdminInterceptor adminInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.adminInterceptor = adminInterceptor;
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
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }
}
