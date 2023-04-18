package com.catdog.help.service;

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

import java.util.List;
import java.util.stream.Collectors;

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

    public CommentForm readComment(Long id) {
        return new CommentForm(jpaCommentRepository.findById(id));
    }

    public List<CommentForm> readComments(Long boardId) {
        List<Comment> comments = jpaCommentRepository.findAll(boardId);
        return comments.stream()
                .map(CommentForm::new)
                .collect(Collectors.toList());
    }

    public UpdateCommentForm getUpdateCommentForm(Long id, String nickname) {
        Comment findComment = jpaCommentRepository.findById(id);
        return new UpdateCommentForm(findComment, nickname);
    }

    @Transactional
    public void updateComment(UpdateCommentForm form) {
        Comment findComment = jpaCommentRepository.findById(form.getCommentId());
        findComment.updateComment(form.getContent());
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment findComment = jpaCommentRepository.findById(id);
        jpaCommentRepository.delete(findComment);
    }


    /**============================= private method ==============================*/


    private Comment getComment(CommentForm form, Board board, User user) {
        Comment comment = Comment.builder()
                .board(board)
                .user(user)
                .content(form.getContent())
                .build();
        return comment;
    }
}