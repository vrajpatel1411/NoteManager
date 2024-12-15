package org.vrajpatel.notemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vrajpatel.notemanager.config.JwtProvider;
import org.vrajpatel.notemanager.exception.UserException;
import org.vrajpatel.notemanager.model.Notes;
import org.vrajpatel.notemanager.model.Users;
import org.vrajpatel.notemanager.repository.UsersRepository;
import org.vrajpatel.notemanager.request.LoginRequest;
import org.vrajpatel.notemanager.response.AuthResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserServiceImplementation customUserServiceImplementation;
    @Autowired
    private JwtProvider jwtProvider;

    public ResponseEntity<AuthResponse> createUser(Users user) throws UserException {

        String email = user.getEmail().toLowerCase();
        String isEmailExist =usersRepository.isEmailExist(email);

        if(isEmailExist!=null) {
            throw new UserException("User already exist");
        }

        Users createUser= new Users();
        createUser.setEmail(email);
        createUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createUser.setNotes(new ArrayList<Notes>());
        Users savedUser=usersRepository.save(createUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt=jwtProvider.generateToken(authentication);
        AuthResponse authResponse =new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Sign Up Successful");
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<AuthResponse> loginService(LoginRequest loginRequest) throws BadCredentialsException, UsernameNotFoundException {
        String email = loginRequest.getEmail().toLowerCase();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticate(email,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt=jwtProvider.generateToken(authentication);
        AuthResponse authResponse =new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Login Successful");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String email, String password) throws BadCredentialsException, UsernameNotFoundException {
        UserDetails userDetails=customUserServiceImplementation.loadUserByUsername(email);
        if(userDetails==null) {
            throw new UsernameNotFoundException("User not found");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword(), userDetails.getAuthorities());
    }




}
