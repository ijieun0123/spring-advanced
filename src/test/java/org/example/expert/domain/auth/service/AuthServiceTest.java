package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.exception.AuthException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Test
    @Transactional
    void 회원가입(){
        // given
        final User newUser = User.builder()
                .email("ijieun@gmail.com")
                .password("Password123@")
                .userRole(UserRole.USER)
                .build();

        // when
        final User savedUser = userRepository.save(newUser);

        // then
        assertThat(savedUser.getEmail()).isEqualTo("ijieun@gmail.com");
        assertThat(savedUser.getPassword()).isEqualTo("Password123@");
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @Transactional
    void 중복_회원_예외(){
        // given
        SignupRequest signupRequest1 = new SignupRequest("ijieun@gmail.com", "Password123@", "USER");
        SignupRequest signupRequest2 = new SignupRequest("ijieun@gmail.com", "Password123@", "USER");

        authService.signup(signupRequest1);

        // when & then
        InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> {
            authService.signup(signupRequest2);
        });

        assertThat(e.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
    }

    @Test
    @Transactional
    void 로그인(){
        // given
        String email = "ijieun@gmail.com";
        String password = "Password123@";
        UserRole userRole = UserRole.USER;
        String encodedPassword = passwordEncoder.encode(password);
        MockHttpSession session = new MockHttpSession();

        User newUser = new User(email, encodedPassword, userRole);
        userRepository.save(newUser);

        SigninRequest signinRequest = new SigninRequest(email, password);

        // when
        SigninResponse signinResponse = authService.signin(signinRequest, session);

        // then
        assertNotNull(signinResponse.getBearerToken());
    }

    @Test
    @Transactional
    void signin_존재하지_않는_유저_로그인_테스트() {
        // given
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("ijieun@gmail.com");
        signinRequest.setPassword("Password123@");

        MockHttpSession session = new MockHttpSession();

        // when & then
        InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> {
            authService.signin(signinRequest, session);
        });

        assertThat(e.getMessage()).isEqualTo("가입되지 않은 유저입니다.");
    }

    @Test
    @Transactional
    void signin_잘못된_비밀번호_테스트() {
        // given
        String email = "ijieun@gmail.com";
        String password = "Password123@";
        UserRole userRole = UserRole.USER;

        MockHttpSession session = new MockHttpSession();

        User user = new User(email, passwordEncoder.encode(password), userRole);

        User savedUser = userRepository.save(user);

        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail(email);
        signinRequest.setPassword("Password123!");

        // when & then
        AuthException e = assertThrows(AuthException.class, () -> {
            authService.signin(signinRequest, session);
        });

        assertThat(e.getMessage()).isEqualTo("잘못된 비밀번호입니다.");
    }
}
