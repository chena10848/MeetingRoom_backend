package com.example.demo.Controller;

import com.example.demo.Component.JwtUtil;
import com.example.demo.Request.User.InsertUserRequest;
import com.example.demo.Request.User.loginRequest;
import com.example.demo.Request.User.googleLoginUser;
import com.example.demo.Service.ApiResponse;
import com.example.demo.Service.UserService;
import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertUser() {
        InsertUserRequest request = new InsertUserRequest();
        String userName = "test";
        String email = "test@gmail.com";
        String password = "test";

        when(userService.insertUserInit(request))
                .thenReturn(new ApiResponse<>("Successful", 200, ""));

        ApiResponse<String> response = userController.insertUser(request);

        assertEquals("Successful", response.getMessage());
        assertEquals(200, response.getStatus());
        verify(userService, times(1)).insertUserInit(request);
    }

    @Test
    void testLoginUser() {
        loginRequest request = new loginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userService.loginUserInit(request))
                .thenReturn(new ApiResponse<>("Successful", 200, "token123"));

        ApiResponse<String> response = userController.loginUser(request);

        assertEquals("Successful", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals("token123", response.getData());
        verify(userService, times(1)).loginUserInit(request);
    }

    @Test
    void testGoogleLoginUser() {
        googleLoginUser request = new googleLoginUser();
        request.setToken("dummyGoogleToken");

        when(userService.googleLoginUserInit(request))
                .thenReturn(new ApiResponse<>("Successful", 200, "googleToken123"));

        ApiResponse<String> response = userController.googleLoginUser(request);

        assertEquals("Successful", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals("googleToken123", response.getData());
        verify(userService, times(1)).googleLoginUserInit(request);
    }

    @Test
    void testLogoutUser() {
        String token = "dummyToken";
        Claims mockClaims = mock(Claims.class);
        when(jwtUtil.validateToken(anyString())).thenReturn(mockClaims);

        when(userService.logoutUserInit(token))
                .thenReturn(new ApiResponse<>("Successful", 200, ""));

        ApiResponse<String> response = userController.logoutUser(token);

        assertEquals("Successful", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals("", response.getData());
        verify(jwtUtil, times(1)).validateToken(token);

    }

}
