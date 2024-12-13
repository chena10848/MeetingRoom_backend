package com.example.demo.Service;

import com.example.demo.Entity.User;
import org.springframework.stereotype.Service;
import com.example.demo.Repository.UserRepository;
import com.example.demo.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.Config.SecurityConfig;
import com.example.demo.Component.JwtUtil;
import org.springframework.beans.factory.annotation.Value;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Request.User.loginRequest;
import com.example.demo.Request.User.InsertUserRequest;
import com.example.demo.Request.User.googleLoginUser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    public UserService(
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public ApiResponse<String> loginUserInit(loginRequest request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();

            List<Object> user = this.userRepository.getUserPassword(email);

            Object[] userData = (Object[]) user.get(0);
            String myPassword = (String) userData[2];
            Integer userId = (Integer) userData[0];
            String loginType = (String) userData[3];

            // 验证密码
            SecurityConfig sc = new SecurityConfig();
            if (sc.passwordEncoder().matches(password, myPassword) && loginType.equals("GENERALLY")) {
                String token = jwtUtil.generateToken(email, userId); // 生成 JWT Token
                return new ApiResponse<>("Successful", 200, token);
            } else {
                return new ApiResponse<>("Error", 500, "Invalid email or password");
            }

        } catch (Exception ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> googleLoginUserInit(googleLoginUser request) {
        try {
            String googleToken = request.getToken();
            System.out.println("Received token: " + googleToken);
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
                    .setAudience(Collections.singletonList("*"))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleToken);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                // 获取用户信息
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                //取得用戶訊息沒有的話薪曾有的畫檢查Type
                List<Object> user = this.userRepository.getUserPassword(email);
                Integer userId = 0;

                if(user.isEmpty()) {
                    //寫入使用者
                    User users = new User();
                    users.setUserName(name);
                    users.setEmail(email);
                    users.setLoginType("GOOGLE");
                    User savedUser = this.userRepository.save(users);
                    userId = savedUser.getId();
                } else {
                    Object[] userData = (Object[]) user.get(0);
                    userId = (Integer) userData[0];
                    //比對loginType
                    String loginType = (String) userData[3];

                    if (!loginType.equals("GOOGLE")) {
                        return new ApiResponse<>("Login Error.", 500, "");
                    }
                }


                String token = jwtUtil.generateToken(email, userId); // 生成 JWT Token
                return new ApiResponse<>("Successful", 200, token);
            } else {
                return new ApiResponse<>("Invalid ID token.", 500, "");
            }
        } catch(Exception ex){
            // 捕获异常并抛出自定义异常
            throw new UserNotFoundException("Error verifying Google token: " + ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> insertUserInit(InsertUserRequest request) {
        try {
            String userName = request.getUserName();
            String email = request.getEmail();
            String password = request.getPassword();

            SecurityConfig sc = new SecurityConfig();
            String encodedPassword = sc.passwordEncoder().encode(password);

            System.out.println(encodedPassword);

            User user = new User();
            user.setUserName(userName);
            user.setEmail(email);
            user.setPassword(encodedPassword);
            user.setLoginType("GENERALLY");
            this.userRepository.save(user);

            return new ApiResponse<>(
                    "Successful",
                    200,
                    ""
            );
        } catch (Exception ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> logoutUserInit(String token) {
        this.jwtUtil.blacklistToken(token);

        return new ApiResponse<>(
                "Successful",
                200,
                ""
        );
    }
}