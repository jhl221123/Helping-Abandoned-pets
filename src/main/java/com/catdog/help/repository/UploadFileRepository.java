package com.catdog.help.repository;

import com.catdog.help.domain.board.UploadFile;

import java.util.List;

public interface UploadFileRepository {

    public Long save(UploadFile uploadFile);

    public UploadFile findById(Long id);

    public List<UploadFile> findUploadFiles(Long boardId);

    public Long delete(UploadFile uploadFile);
}
