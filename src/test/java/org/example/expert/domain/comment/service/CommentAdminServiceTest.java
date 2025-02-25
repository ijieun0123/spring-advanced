package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    void comment_단건_삭제(){
        // given
        Long commentId = 1L;

        // when
        commentAdminService.deleteComment(commentId);

        // then
        verify(commentRepository).deleteById(commentId);
    }
}
