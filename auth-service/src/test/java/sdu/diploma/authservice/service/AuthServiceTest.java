package sdu.diploma.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sdu.diploma.authservice.client.UserServiceClient;
import sdu.diploma.authservice.dto.LoginRequest;
import sdu.diploma.authservice.dto.RegisterRequest;
import sdu.diploma.authservice.entity.AuthUser;
import sdu.diploma.authservice.enums.Role;
import sdu.diploma.authservice.exception.BusinessException;
import sdu.diploma.authservice.repository.AuthUserRepository;
import sdu.diploma.authservice.repository.RefreshTokenRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthService authService;

    private AuthUser testUser;

    @BeforeEach
    void setUp() {
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
    }

    @Test
    void login_shouldSucceedWithValidCredentials() {
    }
}
