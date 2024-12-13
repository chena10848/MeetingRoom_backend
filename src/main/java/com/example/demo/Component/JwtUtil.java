package com.example.demo.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Set<String> blacklistedTokens = new HashSet<>(); // 黑名單存儲

    // 生成 Token
    public String generateToken(String username, Integer userId) {
        long expirationTimes = 3600000;

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimes))
                .signWith(secretKey)
                .compact();
    }

    // 驗證 Token
    public Claims validateToken(String token) throws JwtException {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // 移除 "Bearer " 前缀
        }

        if (blacklistedTokens.contains(token)) {
            throw new JwtException("Token 已失效（黑名單中）");
        }

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Integer extractUserInfo(String token) {
        Claims claims = validateToken(token);

        return ((Integer) claims.get("userId"));
    }

    // 將 Token 加入黑名單
    public void blacklistToken(String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // 移除 "Bearer " 前缀
        }

        blacklistedTokens.add(token);
    }

    // 檢查 Token 是否在黑名單中
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}