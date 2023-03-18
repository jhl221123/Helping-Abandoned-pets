package com.catdog.help.service;

import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.bulletinboard.PageBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.comment.CommentForm;

import java.io.IOException;
import java.util.List;

public interface BulletinBoardService {

    public Long createBoard(SaveBulletinBoardForm boardForm, String nickName) throws IOException;

    public BulletinBoardDto readBoard(Long id);

    public List<PageBulletinBoardForm> readAll();

    public UpdateBulletinBoardForm getUpdateForm(Long id);

    public Long updateBoard(UpdateBulletinBoardForm updateBulletinBoardForm) throws IOException;

    public void deleteBoard(Long boardId);

    public boolean checkLike(Long boardId, String nickName);

    public boolean clickLike(Long boardId, String nickName);

    public void addViews(Long boardId);

}
