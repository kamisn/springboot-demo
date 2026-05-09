package com.example.demo.JwtInterceptor;

import com.example.demo.common.CurrentUserContext;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.JwtUtil;
import com.example.demo.common.Result;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final JsonMapper jsonMapper;

    public JwtInterceptor(JwtUtil jwtUtil, JsonMapper jsonMapper) {
        this.jwtUtil = jwtUtil;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 跨域预检请求先放行。你现在没写前端也可以留着。
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从请求头中获取 Authorization
        //请求在传入后端的事哦先被tomcat包装成request对象，
        // 用这个对象去实现HttpServletRequest request,
        //  HttpServletResponse response,这两个接口里写好的方法，
        //  这里使用了getheader方法，传入了Authorization，
        //   查出了里面写的token或者bearer token
        String authorization = request.getHeader("Authorization");



        if (!StringUtils.hasText(authorization)) {
            writeUnauthorized(response);
            return false;
        }

        // 3. 兼容两种写法：
        // Authorization: Bearer xxx
        // Authorization: xxx
        String token = authorization;
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }

        try {
            // 4. 解析 token
            Long userId = Long.valueOf(jwtUtil.getUserId(token));
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            // 5. 保存当前用户信息，后续 Service 可以直接取
            CurrentUserContext.set(userId, username,role);

            // 6. 放行
            return true;
        } catch (Exception e) {
            writeUnauthorized(response);
            return false;
            //try中解析token失败返回false
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // 在 JWT 拦截器的 preHandle() 中解析 token，
        // 并把当前用户信息存入 CurrentUserContext。
        // 这个上下文底层使用的是 ThreadLocal，
        // 这样 Service 层可以在当前请求线程中获取登录用户信息。
        // 因为 Tomcat 使用线程池，请求结束后线程不会销毁，而是会被复用。
        // 如果不清理 ThreadLocal，下一个请求复用同一个线程时，可能读到上一个用户的信息，造成用户身份污染。
        // 所以我在拦截器的 afterCompletion() 中调用 CurrentUserContext.clear()，
        // 请求结束后主动移除当前线程保存的用户信息
        CurrentUserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");

        Result<String> result = Result.fail(
                ErrorCode.UNAUTHORIZED.getCode(),
                ErrorCode.UNAUTHORIZED.getMessage()
        );

        response.getWriter().write(jsonMapper.writeValueAsString(result));
    }
}