package com.catdog.help.service;

import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.CommentNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.BoardRepository;
import com.catdog.help.repository.CommentRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.EditCommentForm;
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

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    @Transactional
    public Long save(CommentForm form, Long parentCommentId) {

        Board board = boardRepository.findById(form.getBoardId())
                .orElseThrow(BoardNotFoundException::new);
        User user = userRepository.findByNickname(form.getNickname())
                .orElseThrow(UserNotFoundException::new);

        if (parentCommentId == -1L) {
            //parent comment
            Comment parentComment = getComment(form, board, user);
            commentRepository.save(parentComment);
            return parentComment.getId();
        } else {
            //child comment
            Comment findParentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(CommentNotFoundException::new);
            Comment childComment = getComment(form, board, user);
            childComment.addParent(findParentComment);
            commentRepository.save(childComment);
            return childComment.getId();
        }
    }

    public String getWriter(Long id) {
        return commentRepository.findNicknameById(id);
    }

    public List<CommentForm> readByBoardId(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardId(boardId);
        return comments.stream()
                .map(CommentForm::new)
                .collect(Collectors.toList());
    }

    public EditCommentForm getEditForm(Long id, String nickname) {
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
        return new EditCommentForm(findComment, nickname);
    }

    @Transactional
    public void update(EditCommentForm form) {
        Comment findComment = commentRepository.findById(form.getCommentId())
                .orElseThrow(CommentNotFoundException::new);
        findComment.updateComment(form.getContent());
    }

    @Transactional
    public void delete(Long id) {
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
        commentRepository.delete(findComment);
    }


    /**============================= private method ==============================*/


    private Comment getComment(CommentForm form, Board board, User user) {
        return Comment.builder()
                .board(board)
                .user(user)
                .content(form.getContent())
                .build();
    }
}