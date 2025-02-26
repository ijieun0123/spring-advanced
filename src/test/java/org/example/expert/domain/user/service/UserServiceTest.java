package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void user_단건_조회_성공(){
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserResponse findUser = userService.getUser(userId);

        // then
        assertEquals(findUser.getId(), user.getId());
        assertEquals(findUser.getEmail(), user.getEmail());
    }

    @Test
    void user_단건_조회_유저가_없을_때 (){
        // given
        Long userId = 1L;

        // when
        InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> {
            userService.getUser(userId);
        });

        // then
        assertEquals(e.getMessage(), "User not found");
    }

    @Test
    @Transactional
    void 비밀번호_변경_성공(){
        // given
        Long userId = 1L;
        String oldPassword = "Password123@";
        String newPassword = "Password123!";
        String oldEncodedPassword = "oldEncodedPassword";
        String newEncodedPassword = "newEncodedPassword";

        User user = new User("ijieun123@gmail.com", oldPassword, UserRole.USER);
        user.setPassword(oldEncodedPassword);

        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(oldPassword, newPassword);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, oldEncodedPassword)).willReturn(true);
        given(passwordEncoder.matches(newPassword, oldEncodedPassword)).willReturn(false);
        given(passwordEncoder.encode(newPassword)).willReturn(newEncodedPassword);

        // when
        userService.changePassword(userId, userChangePasswordRequest);

        // then
        assertEquals(newEncodedPassword, user.getPassword());
        verify(userRepository).findById(userId);
    }
}
