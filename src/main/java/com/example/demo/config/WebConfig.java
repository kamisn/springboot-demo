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
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/error",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/static/**",
                        "/user/login",
                        "/user/register"
                );

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }
}
