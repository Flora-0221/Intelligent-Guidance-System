package com.IGsystem;

import com.IGsystem.controller.UserController;
import com.IGsystem.dto.LoginFormDTO;
import com.IGsystem.dto.RegisterFormDTO;
import com.IGsystem.dto.Result;
import com.IGsystem.dto.UserDTO;
import com.IGsystem.entity.User;
import com.IGsystem.service.UserService;
import com.IGsystem.utils.UserHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserHolder userHolder;

    @InjectMocks
    private UserController userController;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        session = new MockHttpSession();
    }

    @Test
    void testLogin_success() {
        // Given
        LoginFormDTO loginFormDTO = new LoginFormDTO();
        loginFormDTO.setPhone("1234567890");
        loginFormDTO.setPassword("password");

        // When
        when(userService.login(loginFormDTO, session)).thenReturn(Result.ok());

        // Then
        Result result = userController.login(loginFormDTO, session);
        assertTrue(result.getSuccess());
        assertNull(result.getErrorMsg());
    }

    @Test
    void testLogin_failure() {
        // Given
        LoginFormDTO loginFormDTO = new LoginFormDTO();
        loginFormDTO.setPhone("1234567890");
        loginFormDTO.setPassword("wrongpassword");

        // When
        when(userService.login(loginFormDTO, session)).thenReturn(Result.fail("Invalid credentials"));

        // Then
        Result result = userController.login(loginFormDTO, session);
        assertFalse(result.getSuccess());
        assertEquals("Invalid credentials", result.getErrorMsg());
    }

    @Test
    void testRegister_success() {
        // Given
        RegisterFormDTO registerFormDTO = new RegisterFormDTO();
        registerFormDTO.setPhone("1234567890");
        registerFormDTO.setPassword("password");
        registerFormDTO.setNickName("Test User");

        // When
        when(userService.register(registerFormDTO, session)).thenReturn(Result.ok());

        // Then
        Result result = userController.register(registerFormDTO, session);
        assertTrue(result.getSuccess());
        assertNull(result.getErrorMsg());
    }

    @Test
    void testRegister_failure() {
        // Given
        RegisterFormDTO registerFormDTO = new RegisterFormDTO();
        registerFormDTO.setPhone("1234567890");
        registerFormDTO.setPassword("password");
        registerFormDTO.setNickName("Test User");

        // When
        when(userService.register(registerFormDTO, session)).thenReturn(Result.fail("Registration failed"));

        // Then
        Result result = userController.register(registerFormDTO, session);
        assertFalse(result.getSuccess());
        assertEquals("Registration failed", result.getErrorMsg());
    }

    @Test
    void testMe_userExists() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setPhone("1234567890");

        // When
        when(userHolder.getUser()).thenReturn(userDTO);

        // Then
        Result result = userController.me();
        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void testMe_userNotFound() {
        // Given
        when(userHolder.getUser()).thenReturn(null);

        // Then
        Result result = userController.me();
        assertFalse(result.getSuccess());
        assertEquals("用户不存在", result.getErrorMsg());
    }

    @Test
    void testLogout_success() {
        // When
        Result result = userController.logout();

        // Then
        assertTrue(result.getSuccess());
        verify(userHolder, times(1)).removeUser();
    }

}
