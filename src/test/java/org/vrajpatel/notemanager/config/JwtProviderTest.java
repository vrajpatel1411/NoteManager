package org.vrajpatel.notemanager.config;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtProviderTest {

    @InjectMocks
    private JwtProvider jwtProvider;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authentication = new UsernamePasswordAuthenticationToken("test@example.com", null);
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void generateToken_Success() {
        String token = jwtProvider.generateToken(authentication);
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void getEmailFromToken_Success() {
        String token = jwtProvider.generateToken(authentication);
        String email = jwtProvider.getEmailFromToken("Bearer " + token);
        assertEquals("test@example.com", email);
    }

    @Test
    void getEmailFromToken_InvalidToken() {
        assertThrows(Exception.class, () -> jwtProvider.getEmailFromToken("Bearer invalid.token"));
    }
}