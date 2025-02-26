package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @Test
    void 댓글_작성_성공() throws Exception {
        Long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.ADMIN);
        User user = User.fromAuthUser(authUser);
        CommentSaveRequest commentSaveRequest = new CommentSaveRequest("contents");
        CommentSaveResponse commentSaveResponse = new CommentSaveResponse(1L, "contents", new UserResponse(user.getId(), user.getEmail()));

        given(commentService.saveComment(any(AuthUser.class), eq(todoId), any(CommentSaveRequest.class)))
                .willReturn(commentSaveResponse);

        mvc.perform(post("/comments")
                .param("todoId", String.valueOf(todoId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentSaveRequest)))
                .andExpect(status().isOk());
    }

}
