package org.example.expert.domain.user.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserAdminService userAdminService;

    @Test
    void 유저_롤_바꾸기_성공(){
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");

        // when
        userAdminService.changeUserRole(userId, userRoleChangeRequest);

        //  then
        assertEquals(user.getUserRole(), UserRole.ADMIN);
    }
}
