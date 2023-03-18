package com.catdog.help.service;

import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.UpdateCommentForm;

import java.util.List;

public interface CommentService {
    public Long createComment(CommentForm commentForm, Long parentCommentId);

    public CommentForm readComment(Long commentId);

    public List<CommentForm> readComments(Long boardId);

    public UpdateCommentForm getUpdateCommentForm(Long commentId, String nickName);

    public Long updateComment(UpdateCommentForm updateForm);

    public void deleteComment(Long commentId);
}
