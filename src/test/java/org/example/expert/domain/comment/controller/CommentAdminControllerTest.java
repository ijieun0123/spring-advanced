package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.comment.service.CommentAdminService;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static sun.security.krb5.internal.KDCOptions.with;

@WebMvcTest(CommentAdminController.class)
class CommentAdminControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentAdminService commentAdminService;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    JwtUtil jwtUtil;

    @Test
    void 댓글_삭제_성공() throws Exception{
        AuthUser authUser = new AuthUser(1L, "email", UserRole.ADMIN);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "contents", "weather", user);
        Comment comment = new Comment("contents", user, todo);

        ReflectionTestUtils.setField(comment, "id", 1L);

        commentRepository.save(comment);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("role", UserRole.ADMIN);

        mvc.perform(delete("/admin/comments/1").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void 댓글_삭제_실패() throws Exception {
        mvc.perform(delete("/admin/comments/100"))
                .andExpect(status().is4xxClientError());
    }
}