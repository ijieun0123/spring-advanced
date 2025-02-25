package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil); // 명시적으로 주입
    }

    @Test
    void 회원가입(){
        // given
        final User newUser = User.builder()
                .email("ijieun@gmail.com")
                .password("Password123@")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // when
        final User savedUser = userRepository.save(newUser);

        // then
        assertThat(savedUser.getEmail()).isEqualTo("ijieun@gmail.com");
        assertThat(savedUser.getPassword()).isEqualTo("Password123@");
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void 중복_회원_예외(){
        // given
        SignupRequest signupRequest = new SignupRequest("ijieun@gmail.com", "Password123@", "USER");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        // when & then
        InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> {
            authService.signup(signupRequest);
        });

        assertThat(e.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
    }
}
