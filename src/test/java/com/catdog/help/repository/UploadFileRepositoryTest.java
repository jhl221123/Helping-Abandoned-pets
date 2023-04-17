package com.catdog.help.repository;

import com.catdog.help.TestData;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.repository.jpa.JpaBulletinBoardRepository;
import com.catdog.help.repository.jpa.JpaUploadFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class UploadFileRepositoryTest {

    @Autowired JpaUploadFileRepository uploadFileRepository;
    @Autowired JpaBulletinBoardRepository jpaBulletinBoardRepository;
    @Autowired TestData testData;

    @Test
    void 저장_조회() {
        //given
        BulletinBoard oneUploadFileBoard = testData.createBulletinBoardOnlyTitle("oneUploadFileBoard");
        BulletinBoard uploadFilesBoard = testData.createBulletinBoardOnlyTitle("uploadFilesBoard");
        BulletinBoard noUploadFileBoard = testData.createBulletinBoardOnlyTitle("noUploadFileBoard");
        jpaBulletinBoardRepository.save(oneUploadFileBoard);
        jpaBulletinBoardRepository.save(uploadFilesBoard);
        jpaBulletinBoardRepository.save(noUploadFileBoard);

        UploadFile uploadFile1 = testData.getUploadFile(oneUploadFileBoard, "uploadName1");
        UploadFile uploadFile2 = testData.getUploadFile(uploadFilesBoard, "uploadName2");
        UploadFile uploadFile3 = testData.getUploadFile(uploadFilesBoard, "uploadName3");

        //when
        uploadFileRepository.save(uploadFile1);
        uploadFileRepository.save(uploadFile2);
        uploadFileRepository.save(uploadFile3);
        List<UploadFile> oneUploadFile = uploadFileRepository.findUploadFiles(oneUploadFileBoard.getId());
        List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(uploadFilesBoard.getId());
        List<UploadFile> noUploadFile = uploadFileRepository.findUploadFiles(noUploadFileBoard.getId());

        //then
        assertThat(oneUploadFile.size()).isEqualTo(1);
        assertThat(uploadFiles.size()).isEqualTo(2);
        assertThat(noUploadFile.size()).isEqualTo(0);

    }
}