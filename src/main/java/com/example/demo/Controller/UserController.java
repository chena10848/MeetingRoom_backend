package com.example.demo.Controller;

import com.example.demo.Component.JwtUtil;
import com.example.demo.Request.User.InsertUserRequest;
import com.example.demo.Request.User.loginRequest;
import com.example.demo.Request.User.googleLoginUser;

import com.example.demo.Service.ApiResponse;
import com.example.demo.Service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.Component.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User API")
public class UserController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtUtil jwtUtil;


    public UserController (
            UserService userService,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    //註冊
    @PostMapping("/api/createUser")
    @Operation(summary = "註冊會員")
    public ApiResponse<String> insertUser(@RequestBody InsertUserRequest request) {
        if (request == null) {
            throw new NullPointerException("Request body is null");
        }
        return this.userService.insertUserInit(request);
    }

    @PostMapping("/api/login")
    @Operation(summary = "會員登入")
    public ApiResponse<String> loginUser(@RequestBody loginRequest request) {
        if (request == null) {
            throw new NullPointerException("Request body is null");
        }
        return this.userService.loginUserInit(request);
    }

    @PostMapping("/api/login/google")
    @Operation(summary = "Google登入")
    public ApiResponse<String> googleLoginUser(@RequestBody googleLoginUser request) {
        if (request == null) {
            throw new NullPointerException("Request body is null");
        }
        return this.userService.googleLoginUserInit(request);
    }

    @PostMapping("/api/logout")
    @Operation(summary = "會員登出")
    public ApiResponse<String> logoutUser(@RequestHeader("Authorization") String token) {
        if (token == null) {
            throw new NullPointerException("Token is null");
        }
        this.jwtUtil.validateToken(token);
        return this.userService.logoutUserInit(token);
    }
}
