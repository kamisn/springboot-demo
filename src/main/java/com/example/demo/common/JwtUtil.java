package com.example.demo.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;//读密钥

    @Value("${jwt.expire}")
    private long expire;//读过期时间

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        //作用就是把配置里的字符串密钥，转成真正签名要用的 SecretKey 对象。
        //配置文件里是字符串
        //JWT 库真正签名时要的是 SecretKey
        //所以要先做一次初始化转换
    }
    //
    public String createToken(long  userId, String username ,String role) {
        Date now = new Date();//当前时间
        Date expiration = new Date(now.getTime() + expire);//过期时间

        return Jwts.builder()
                .subject(String.valueOf(userId))//id
                .claim("username", username)//
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
//     eg:
//        {
//            "sub": "1",
//                "username": "zhangsan",
//                "iat": 1713920000,
//                "exp": 1713923600
//        }
    }

    public Claims parseToken(String token) {
        return Jwts.parser()//创建一个 JWT 解析器的构建起点
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)//主要的验证步骤，将上一步传入的head和payload用key再算一遍和signature进行对比
                .getPayload();

//        告诉解析器用哪把密钥验签
//        构建真正可用的解析器对象类似以下的思路
        //JwtParser parser = Jwts.parser()
        //        .verifyWith(key)
        //        .build();
//        解析 token，并校验签名，先把 token 按 . 分成三段解析 header 和 payload，检查这是不是“已签名的 JWT”
//        从解析结果里取出 payload
//        payload 被封装成 Claims 返回
    }

    public Integer getUserId(String token) {
        Claims claims = parseToken(token);
        return Integer.valueOf(claims.getSubject());
    }

    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
        //subject 本质上也是 claims 的一部分，
        // 它对应 JWT payload 中的 sub 字段，属于标准声明。
        // 像 username 这种是自定义声明，也同样存放在 claims 中。
        // 区别只是标准声明通常有专门的 getter，比如 getSubject()，
        // 而自定义声明一般通过 claims.get(key, type) 来获取。
    }
    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }
}