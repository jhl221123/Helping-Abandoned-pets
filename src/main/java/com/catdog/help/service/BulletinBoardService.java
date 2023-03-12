package com.catdog.help.service;

import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.bulletinboard.PageBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;

import java.io.IOException;
import java.util.List;

public interface BulletinBoardService {

    public Long createBoard(SaveBulletinBoardForm boardForm, String nickName) throws IOException;

    public BulletinBoardDto readBoard(Long id);

    public List<PageBulletinBoardForm> readAll();

    public Long updateBoard(UpdateBulletinBoardForm updateBulletinBoardForm) throws IOException;

    public UpdateBulletinBoardForm getUpdateForm(Long id);
}
