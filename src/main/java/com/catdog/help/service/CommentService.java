package com.catdog.help.service;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaBoardRepository;
import com.catdog.help.repository.jpa.JpaCommentRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.UpdateCommentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final JpaCommentRepository jpaCommentRepository;
    private final JpaBoardRepository boardRepository;
    private final JpaUserRepository userRepository;


    @Transactional
    public Long createComment(CommentForm form, Long parentCommentId) {

        Board board = boardRepository.findById(form.getBoardId());
        User user = userRepository.findByNickname(form.getNickname());

        if (parentCommentId == -1L) {
            //parent comment
            Comment parentComment = getComment(form, board, user);
            jpaCommentRepository.save(parentComment);
            return parentComment.getId();
        } else {
            //child comment
            Comment findParentComment = jpaCommentRepository.findById(parentCommentId);
            Comment childComment = getComment(form, board, user);
            childComment.addParent(findParentComment);
            jpaCommentRepository.save(childComment);
            return childComment.getId();
        }
    }

    public CommentForm readComment(Long commentId) {
        Comment findComment = jpaCommentRepository.findById(commentId);
        return new CommentForm(findComment);
    }

    public List<CommentForm> readComments(Long boardId) {
        List<CommentForm> forms = new ArrayList<>();

        List<Comment> comments = jpaCommentRepository.findAll(boardId);
        for (Comment comment : comments) {
            forms.add(new CommentForm(comment));
        }
        return forms;
    }

    public UpdateCommentForm getUpdateCommentForm(Long commentId, String nickName) {
        Comment findComment = jpaCommentRepository.findById(commentId);
        return new UpdateCommentForm(findComment, nickName);
    }

    @Transactional
    public Long updateComment(UpdateCommentForm form) {
        Comment findComment = jpaCommentRepository.findById(form.getCommentId());
        findComment.updateComment(form);
        return findComment.getId();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment findComment = jpaCommentRepository.findById(commentId);
        jpaCommentRepository.delete(findComment);
    }


    /**============================= private method ==============================*/


    private static Comment getComment(CommentForm form, Board board, User user) {
        Comment comment = Comment.builder()
                .board(board)
                .user(user)
                .form(form)
                .build();
        return comment;
    }
}