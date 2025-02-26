package org.example.expert.domain.todo.service;

import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    public void todo_생성(){
        // given
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "contents", "weather", user);

        given(todoRepository.save(any())).willReturn(todo);

        // when
        Long savedTodoId = todoRepository.save(todo).getId();

        // then
        assertThat(todo.getId()).isEqualTo(savedTodoId);
    }

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

    @Test
    void todo_단건_조회(){
        // given
        Long todoId = 1L;
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);
        Todo todo = new Todo("title", "contents", "weather", user);

        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));

        // when
        TodoResponse findTodo = todoService.getTodo(todoId);

        // then
        Assertions.assertEquals(findTodo.getTitle(), todo.getTitle());
        Assertions.assertEquals(findTodo.getContents(), todo.getContents());
        Assertions.assertEquals(findTodo.getWeather(), todo.getWeather());
        Assertions.assertEquals(findTodo.getUser().getEmail(), todo.getUser().getEmail());
        Assertions.assertEquals(findTodo.getUser().getId(), todo.getUser().getId());
    }
}
