package org.example.expert.domain.todo.service;

import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    public void todos_전체_조회(){
        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "contents", "weather", user);

        List<Todo> todoList = new ArrayList<>();
        todoList.add(todo);
        todoList.add(todo);
        todoList.add(todo);

        Page<Todo> responsePages = new PageImpl<>(todoList);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "modifiedAt"));;

        given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(responsePages);

        // when
        Page<TodoResponse> resultList = todoService.getTodos(1, 10);

        // then
        assertThat(resultList.getTotalElements()).isEqualTo(3);
    }

}
