package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.JwtUtil;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.vo.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {
    public final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    public UserService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }
    public LoginResponse login(LoginRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        String token = jwtUtil.createToken(user.getId(), user.getUsername());
        return  new LoginResponse(token, user.getUsername(), user.getId());
    }
    public Long register(RegisterRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            // 3. 抛出自定义业务异常：参数错误
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        User existedUser = userMapper.selectByUsername(request.getUsername());
        if (existedUser != null) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATE);
        }
        User user = new User();
        // 设置用户名
        user.setUsername(request.getUsername());
        // 设置密码
        user.setPassword(request.getPassword());
        // 设置昵称
        user.setNickname(request.getNickname());
        // 普通用户
        user.setRole("USER");
        // 状态正常
        user.setStatus(1);

        // 13. 调用Mapper插入数据到数据库
        userMapper.insertUser(user);

        // 14. 返回插入后自动生成的用户ID
        return user.getId();
    }
}
