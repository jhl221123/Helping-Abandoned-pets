package com.catdog.help.service;

import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.UpdateBulletinBoardForm;
import com.catdog.help.web.form.SaveBulletinBoardForm;

import java.util.List;

public interface BulletinBoardService {

    public Long createBoard(SaveBulletinBoardForm boardForm);

    public BulletinBoardDto readBoard(Long id);

    public Long updateBoard(UpdateBulletinBoardForm updateBulletinBoardForm);

    public List<BulletinBoardDto> readAll();

    public UpdateBulletinBoardForm getUpdateForm(Long id);
}
