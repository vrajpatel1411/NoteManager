package org.vrajpatel.notemanager.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vrajpatel.notemanager.exception.UserException;
import org.vrajpatel.notemanager.model.Users;
import org.vrajpatel.notemanager.request.LoginRequest;
import org.vrajpatel.notemanager.response.AuthResponse;
import org.vrajpatel.notemanager.service.UsersService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody Users user) throws UserException {
        try {
            return usersService.createUser(user);
        }
        catch (UserException e) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(authResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) throws BadCredentialsException, UsernameNotFoundException {
        try {


            return usersService.loginService(loginRequest);
        }
        catch (UsernameNotFoundException | BadCredentialsException e) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(authResponse,HttpStatus.BAD_REQUEST);
        }
    }
}
