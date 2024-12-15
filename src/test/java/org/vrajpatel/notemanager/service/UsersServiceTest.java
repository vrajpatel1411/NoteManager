package org.vrajpatel.notemanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vrajpatel.notemanager.config.JwtProvider;
import org.vrajpatel.notemanager.exception.UserException;
import org.vrajpatel.notemanager.model.Users;
import org.vrajpatel.notemanager.repository.UsersRepository;
import org.vrajpatel.notemanager.request.LoginRequest;
import org.vrajpatel.notemanager.response.AuthResponse;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserServiceImplementation customUserServiceImplementation;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UsersService usersService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new Users();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
    }

    @AfterEach
    void tearDown() {
        if (testUser != null && testUser.getEmail() != null) {
            Users user = usersRepository.findByEmail(testUser.getEmail());
            if (user != null) {
                usersRepository.delete(user);
            }
        }
    }

    @Test
    void createUser_Success() throws UserException {
        when(usersRepository.isEmailExist(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usersRepository.save(any(Users.class))).thenReturn(testUser);
        when(jwtProvider.generateToken(any())).thenReturn("jwtToken");

        ResponseEntity<AuthResponse> response = usersService.createUser(testUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sign Up Successful", response.getBody().getMessage());
        assertNotNull(response.getBody().getJwt());
    }

    @Test
    void createUser_EmailAlreadyExists() {
        when(usersRepository.isEmailExist(anyString())).thenReturn("existing@example.com");

        assertThrows(UserException.class, () -> usersService.createUser(testUser));
    }

    @Test
    void loginService_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        UserDetails userDetails = new User("test@example.com", "encodedPassword", new ArrayList<>());
        when(customUserServiceImplementation.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtProvider.generateToken(any())).thenReturn("jwtToken");

        ResponseEntity<AuthResponse> response = usersService.loginService(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Login Successful", response.getBody().getMessage());
        assertNotNull(response.getBody().getJwt());
    }

    @Test
    void loginService_InvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("wrongpassword");

        UserDetails userDetails = new User("test@example.com", "encodedPassword", new ArrayList<>());
        when(customUserServiceImplementation.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> usersService.loginService(loginRequest));
    }

    @Test
    void loginService_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password");

        when(customUserServiceImplementation.loadUserByUsername(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> usersService.loginService(loginRequest));
    }
}