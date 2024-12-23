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
import org.junit.jupiter.api.DisplayName;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
    @DisplayName("註冊成功場景測試")
    void testInsertUser_Success() {
        InsertUserRequest request = new InsertUserRequest();
        request.setUserName("test");
        request.setEmail("test@gmail.com");
        request.setPassword("Test123!");

        when(userService.insertUserInit(request))
                .thenReturn(new ApiResponse<>("Successful", 200, ""));

        ApiResponse<String> response = userController.insertUser(request);

        assertEquals("Successful", response.getMessage());
        assertEquals(200, response.getStatus());
        verify(userService, times(1)).insertUserInit(request);

        ArgumentCaptor<InsertUserRequest> requestCaptor = ArgumentCaptor.forClass(InsertUserRequest.class);
        verify(userService, times(1)).insertUserInit(requestCaptor.capture());
        InsertUserRequest capturedRequest = requestCaptor.getValue();
        
        assertEquals("test", capturedRequest.getUserName());
        assertEquals("test@gmail.com", capturedRequest.getEmail());
        assertEquals("Test123!", capturedRequest.getPassword());
    }

    @Test
    @DisplayName("註冊失敗 - 郵箱已存在")
    void testInsertUser_EmailExists() {
        InsertUserRequest request = new InsertUserRequest();
        request.setUserName("test");
        request.setEmail("existing@gmail.com");
        request.setPassword("Test123!");

        when(userService.insertUserInit(request))
                .thenReturn(new ApiResponse<>("Email already exists", 400, null));

        ApiResponse<String> response = userController.insertUser(request);

        assertEquals("Email already exists", response.getMessage());
        assertEquals(400, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("登入成功場景測試")
    void testLoginUser_Success() {
        loginRequest request = new loginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userService.loginUserInit(request))
                .thenReturn(new ApiResponse<>("Successful", 200, "token123"));

        ApiResponse<String> response = userController.loginUser(request);

        assertEquals("Successful", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals("token123", response.getData());

        ArgumentCaptor<loginRequest> requestCaptor = ArgumentCaptor.forClass(loginRequest.class);
        verify(userService, times(1)).loginUserInit(requestCaptor.capture());
        loginRequest capturedRequest = requestCaptor.getValue();
        
        assertEquals("test@example.com", capturedRequest.getEmail());
        assertEquals("password", capturedRequest.getPassword());
    }

    @Test
    @DisplayName("登入失敗 - 密碼錯誤")
    void testLoginUser_WrongPassword() {
        loginRequest request = new loginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(userService.loginUserInit(request))
                .thenReturn(new ApiResponse<>("Invalid credentials", 401, null));

        ApiResponse<String> response = userController.loginUser(request);

        assertEquals("Invalid credentials", response.getMessage());
        assertEquals(401, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Google登入成功場景測試")
    void testGoogleLoginUser_Success() {
        googleLoginUser request = new googleLoginUser();
        request.setToken("validGoogleToken");

        when(userService.googleLoginUserInit(request))
                .thenReturn(new ApiResponse<>("Successful", 200, "googleToken123"));

        ApiResponse<String> response = userController.googleLoginUser(request);

        assertEquals("Successful", response.getMessage());
        assertEquals(200, response.getStatus());
        assertEquals("googleToken123", response.getData());

        ArgumentCaptor<googleLoginUser> requestCaptor = ArgumentCaptor.forClass(googleLoginUser.class);
        verify(userService, times(1)).googleLoginUserInit(requestCaptor.capture());
        googleLoginUser capturedRequest = requestCaptor.getValue();
        
        assertEquals("validGoogleToken", capturedRequest.getToken());
    }

    @Test
    @DisplayName("Google登入失敗 - 無效Token")
    void testGoogleLoginUser_InvalidToken() {
        googleLoginUser request = new googleLoginUser();
        request.setToken("invalidGoogleToken");

        when(userService.googleLoginUserInit(request))
                .thenReturn(new ApiResponse<>("Invalid Google token", 401, null));

        ApiResponse<String> response = userController.googleLoginUser(request);

        assertEquals("Invalid Google token", response.getMessage());
        assertEquals(401, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("登出成功場景測試")
    void testLogoutUser_Success() {
        String token = "validToken";
        Claims mockClaims = mock(Claims.class);
        when(jwtUtil.validateToken(token)).thenReturn(mockClaims);
        when(userService.logoutUserInit(token))
                .thenReturn(new ApiResponse<>("Successful", 200, ""));

        ApiResponse<String> response = userController.logoutUser(token);

        assertEquals("Successful", response.getMessage());
        assertEquals(200, response.getStatus());
        verify(jwtUtil, times(1)).validateToken(token);
        verify(userService, times(1)).logoutUserInit(token);
    }

    @Test
    @DisplayName("登出失敗 - 無效Token")
    void testLogoutUser_InvalidToken() {
        String token = "invalidToken";
        when(jwtUtil.validateToken(token)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        assertThrows(ResponseStatusException.class, () -> userController.logoutUser(token));
        verify(jwtUtil, times(1)).validateToken(token);
        verify(userService, never()).logoutUserInit(anyString());
    }

    @Test
    @DisplayName("登出失敗 - Token為空")
    void testLogoutUser_EmptyToken() {
        String token = "";
        when(jwtUtil.validateToken(token)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        assertThrows(ResponseStatusException.class, () -> userController.logoutUser(token));
        verify(jwtUtil, times(1)).validateToken(token);
        verify(userService, never()).logoutUserInit(anyString());
    }

    @Test
    @DisplayName("註冊失敗 - 請求體為空")
    void testInsertUser_NullRequest() {
        when(userService.insertUserInit(null))
                .thenReturn(new ApiResponse<>("Request body is null", 400, null));

        ApiResponse<String> response = userController.insertUser(null);

        assertEquals("Request body is null", response.getMessage());
        assertEquals(400, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("登入失敗 - 請求體為空")
    void testLoginUser_NullRequest() {
        when(userService.loginUserInit(null))
                .thenReturn(new ApiResponse<>("Request body is null", 400, null));

        ApiResponse<String> response = userController.loginUser(null);

        assertEquals("Request body is null", response.getMessage());
        assertEquals(400, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Google登入失敗 - 請求體為空")
    void testGoogleLoginUser_NullRequest() {
        when(userService.googleLoginUserInit(null))
                .thenReturn(new ApiResponse<>("Request body is null", 400, null));

        ApiResponse<String> response = userController.googleLoginUser(null);

        assertEquals("Request body is null", response.getMessage());
        assertEquals(400, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("登出失敗 - Token為null")
    void testLogoutUser_NullToken() {
        when(jwtUtil.validateToken(null)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is null"));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userController.logoutUser(null)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("註冊失敗 - 無效的電子郵件格式")
    void testInsertUser_InvalidEmail() {
        InsertUserRequest request = new InsertUserRequest();
        request.setUserName("test");
        request.setEmail("invalid-email");
        request.setPassword("Test123!");

        when(userService.insertUserInit(request))
                .thenReturn(new ApiResponse<>("Invalid email format", 400, null));

        ApiResponse<String> response = userController.insertUser(request);
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("註冊失敗 - 密碼太短")
    void testInsertUser_ShortPassword() {
        InsertUserRequest request = new InsertUserRequest();
        request.setUserName("test");
        request.setEmail("test@gmail.com");
        request.setPassword("123");

        when(userService.insertUserInit(request))
                .thenReturn(new ApiResponse<>("Password too short", 400, null));

        ApiResponse<String> response = userController.insertUser(request);
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("登出失敗 - JWT過期")
    void testLogoutUser_ExpiredToken() {
        String token = "expiredToken";
        when(jwtUtil.validateToken(token))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired"));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userController.logoutUser(token)
        );
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    @DisplayName("登出失敗 - JWT格式錯誤")
    void testLogoutUser_MalformedToken() {
        String token = "malformedToken";
        when(jwtUtil.validateToken(token))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed token"));

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> userController.logoutUser(token)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

}
