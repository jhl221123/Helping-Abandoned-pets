package com.catdog.help.repository;

import com.catdog.help.domain.board.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    @Query("select u from UploadFile u where u.board.id = :boardId")
    List<UploadFile> findByBoardId(@Param("boardId") Long boardId);
}
