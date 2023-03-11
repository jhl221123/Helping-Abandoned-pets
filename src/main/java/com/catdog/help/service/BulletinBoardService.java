package com.catdog.help.service;

import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.UpdateBulletinBoardForm;
import com.catdog.help.web.form.SaveBulletinBoardForm;

import java.util.List;

public interface BulletinBoardService {

    public Long createBoard(SaveBulletinBoardForm boardForm, String nickName);

    public BulletinBoardDto readBoard(Long id);

    public List<BulletinBoardDto> readAll();

    public Long updateBoard(UpdateBulletinBoardForm updateBulletinBoardForm);

    public UpdateBulletinBoardForm getUpdateForm(Long id);
}
