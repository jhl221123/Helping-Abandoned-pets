package com.catdog.help.repository;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UploadFileRepositoryTest {

    @Autowired UploadFileRepository uploadFileRepository;
    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("업로드 파일 저장")
    void save() {
        //given
        UploadFile file = getUploadFile("업로드이름");
        file.addBoard(getBulletin());

        //when
        UploadFile savedFile = uploadFileRepository.save(file);

        //then
        assertThat(savedFile.getUploadFileName()).isEqualTo(file.getUploadFileName());
    }

    @Test
    @DisplayName("업로드 파일 단건 조회")
    void findById() {
        //given
        UploadFile file = getUploadFile("업로드이름");
        file.addBoard(getBulletin());
        uploadFileRepository.save(file);

        //when
        UploadFile findFile = uploadFileRepository.findById(file.getId()).get();

        //then
        assertThat(findFile).isEqualTo(file);
    }

    @Test
    @DisplayName("게시글 아이디로 모두 조회")
    void findByBoardId() {
        //given
        Bulletin board = getBulletin();
        saveUploadFiles(board);

        //when
        List<UploadFile> files = uploadFileRepository.findByBoardId(board.getId());

        //then
        assertThat(files.size()).isEqualTo(2);
        assertThat(files.get(0).getUploadFileName()).isEqualTo("사진A");
    }

    @Test
    @DisplayName("파일 삭제")
    void delete() {
        //given
        UploadFile file = getUploadFile("업로드이름");
        file.addBoard(getBulletin());
        uploadFileRepository.save(file);

        assertThat(uploadFileRepository.count()).isEqualTo(1L);

        //when
        uploadFileRepository.delete(file);

        //then
        assertThat(uploadFileRepository.count()).isEqualTo(0L);
    }


    private void saveUploadFiles(Bulletin board) {
        UploadFile file_A = getUploadFile("사진A");
        UploadFile file_B = getUploadFile("사진B");
        file_A.addBoard(board);
        file_B.addBoard(board);
        uploadFileRepository.save(file_A);
        uploadFileRepository.save(file_B);
    }

    private UploadFile getUploadFile(String uploadName) {
        return UploadFile.builder()
                .uploadFileName(uploadName)
                .storeFileName("저장된이름")
                .build();
    }

    private Bulletin getBulletin() {
        User user = getUser();
        userRepository.save(user);

        return Bulletin.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }

    private User getUser() {
        return User.builder()
                .emailId("test@test.test")
                .password("12345678")
                .nickname("닉네임")
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}