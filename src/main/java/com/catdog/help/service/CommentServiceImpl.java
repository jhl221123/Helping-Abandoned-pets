package com.catdog.help.service;

import com.catdog.help.domain.DateList;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.repository.CommentRepository;
import com.catdog.help.repository.UserRepository;
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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BulletinBoardRepository bulletinBoardRepository;
    private final UserRepository userRepository;


    @Transactional
    public Long createComment(CommentForm commentForm, Long parentCommentId) {

        BulletinBoard board = bulletinBoardRepository.findOne(commentForm.getBoardId());
        User user = userRepository.findByNickName(commentForm.getNickName());

        if (parentCommentId == -1L) {
            //parent comment
            Comment parentComment = getComment(commentForm, board, user);
            commentRepository.save(parentComment);
            return parentComment.getId();
        } else {
            //child comment
            Comment findParentComment = commentRepository.findById(parentCommentId);
            Comment childComment = getComment(commentForm, board, user);
            childComment.addParent(findParentComment);
            commentRepository.save(childComment);
            return childComment.getId();
        }
    }

    public CommentForm readComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId);
        return getCommentForm(findComment);
    }

    public List<CommentForm> readComments(Long boardId) {
        List<CommentForm> commentForms = new ArrayList<>();

        List<Comment> comments = commentRepository.findAll(boardId);
        if (comments == null) {
            return null;
        }
        log.info("======================================={}", comments);
        for (Comment comment : comments) {
            log.info("{}",comment.getBoard());
            log.info("{}",comment.getUser());
            log.info("=========================================={}", comment.getChild());
            commentForms.add(getCommentForm(comment));
        }
        return commentForms;
    }

    public UpdateCommentForm getUpdateCommentForm(Long commentId, String nickName) {
        Comment findComment = commentRepository.findById(commentId);
        return getUpdateForm(findComment, nickName);
    }

    @Transactional
    public Long updateComment(UpdateCommentForm updateForm) {
        Comment findComment = commentRepository.findById(updateForm.getId());
        findComment.setContent(updateForm.getContent());
        findComment.setDateList(new DateList(findComment.getDateList().getCreateDate(), LocalDateTime.now(), null));
        return findComment.getId();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment findComment = commentRepository.findById(commentId);
        commentRepository.delete(findComment);
    }


    /**============================= private method ==============================*/


    private static Comment getComment(CommentForm commentForm, BulletinBoard board, User user) {
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setUser(user);
        comment.setContent(commentForm.getContent());
        comment.setDateList(new DateList(LocalDateTime.now(), null, null)); //댓글 생성에만 사용가능
        return comment;
    }

    private static CommentForm getCommentForm(Comment comment) {
        CommentForm commentForm = new CommentForm();
        commentForm.setId(comment.getId());
        commentForm.setBoardId(comment.getBoard().getId());
        commentForm.setNickName(comment.getUser().getNickName());
        commentForm.setContent(comment.getContent());
        if (!comment.getChild().isEmpty()) {
            for (Comment child : comment.getChild()) {
                CommentForm childCommentForm = getCommentForm(child);
                commentForm.getChild().add(childCommentForm);
            }
        }
        commentForm.setDateList(comment.getDateList());
        return commentForm;
    }

    private static UpdateCommentForm getUpdateForm(Comment findComment, String nickName) {
        UpdateCommentForm updateForm = new UpdateCommentForm();
        updateForm.setId(findComment.getId());
        updateForm.setNickName(nickName);
        updateForm.setContent(findComment.getContent());
        return updateForm;
    }
}