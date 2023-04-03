package com.catdog.help.repository;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
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

    @Autowired UploadFileRepository uploadFileRepository;
    @Autowired BulletinBoardRepository bulletinBoardRepository;

    @Test
    void 저장_조회() {
        //given
        BulletinBoard oneUploadFileBoard = createBulletinBoard("oneUploadFileBoard");
        BulletinBoard uploadFilesBoard = createBulletinBoard("uploadFilesBoard");
        BulletinBoard noUploadFileBoard = createBulletinBoard("noUploadFileBoard");
        bulletinBoardRepository.save(oneUploadFileBoard);
        bulletinBoardRepository.save(uploadFilesBoard);
        bulletinBoardRepository.save(noUploadFileBoard);

        UploadFile uploadFile1 = getUploadFile(oneUploadFileBoard, "uploadName1");
        UploadFile uploadFile2 = getUploadFile(uploadFilesBoard, "uploadName2");
        UploadFile uploadFile3 = getUploadFile(uploadFilesBoard, "uploadName3");

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

    private UploadFile getUploadFile(BulletinBoard board, String uploadName) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setUploadFileName(uploadName);
        uploadFile.setStoreFileName("storeName");
        uploadFile.setBoard(board);
        return uploadFile;
    }

    private static BulletinBoard createBulletinBoard(String title) {
        BulletinBoard board = new BulletinBoard();
        board.setTitle(title);
        board.setContent("content");
        board.setRegion("region");
//        board.setUser(new User()); //jpa 아니면 여기서 error
        board.setDates(new Dates(LocalDateTime.now(), null, null));
        return board;
    }
}