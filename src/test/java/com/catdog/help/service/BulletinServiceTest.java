package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.NotFoundBoardException;
import com.catdog.help.repository.BulletinRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.JpaLikeBoardRepository;
import com.catdog.help.repository.jpa.JpaUploadFileRepository;
import com.catdog.help.web.form.bulletinboard.PageBulletinForm;
import com.catdog.help.web.form.bulletinboard.ReadBulletinForm;
import com.catdog.help.web.form.bulletinboard.SaveBulletinForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinForm;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class BulletinServiceTest {

    @InjectMocks
    BulletinService bulletinService;

    @Mock
    BulletinRepository bulletinRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ImageService imageService;

    @Mock
    JpaUploadFileRepository uploadFileRepository;

    @Mock
    JpaLikeBoardRepository likeBoardRepository;


    @Test
    @DisplayName("게시글 저장")
    void save() {
        //given
        User user = getUser();
        Bulletin board = getBulletin("제목");
        SaveBulletinForm form = getSaveBulletinForm();

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        doNothing().when(imageService)
                .addImage(any(Bulletin.class), any(List.class));

        doReturn(board).when(bulletinRepository)
                .save(any(Bulletin.class));

        //when
        Long id = bulletinService.save(form, "닉네임");

        //then
        assertThat(id).isEqualTo(board.getId());

        //verify
        verify(userRepository, times(1)).findByNickname("닉네임");
        verify(imageService, times(1)).addImage(any(Bulletin.class), any(List.class));
        verify(bulletinRepository, times(1)).save(any(Bulletin.class));
    }

    @Test
    @DisplayName("게시글 단건 조회")
    void readBoards() {
        //given
        Bulletin board = getBulletin("제목");
        List<ReadUploadFileForm> imageForms = new ArrayList<>();

        doReturn(Optional.ofNullable(board)).when(bulletinRepository)
                .findById(board.getId());

        doReturn(imageForms).when(uploadFileRepository)
                .findUploadFiles(board.getId());

        doReturn(3L).when(likeBoardRepository)
                .countByBoardId(board.getId());

        //when
        ReadBulletinForm form = bulletinService.read(board.getId());

        //then
        assertThat(form.getTitle()).isEqualTo("제목");
        assertThat(form.getImages()).isEqualTo(imageForms);
        assertThat(form.getLikeSize()).isEqualTo(3);
    }

    @Test
    @DisplayName("페이지 조회")
    void readPage() {
        //given
        Page<Bulletin> page = Page.empty();

        doReturn(page).when(bulletinRepository)
                .findPageBy(any(Pageable.class));

        //when
        Page<PageBulletinForm> forms = bulletinService.getPage(0);

        //verify
        verify(bulletinRepository, times(1)).findPageBy(any(Pageable.class));
    }

    @Test
    @DisplayName("게시글 수정 양식 호출")
    void getUpdateForm() {
        //given
        Bulletin board = getBulletin("제목");
        List<ReadUploadFileForm> oldImages = new ArrayList<>();

        doReturn(Optional.ofNullable(board)).when(bulletinRepository)
                .findById(board.getId());

        doReturn(oldImages).when(uploadFileRepository)
                .findUploadFiles(board.getId());

        //when
        UpdateBulletinForm form = bulletinService.getUpdateForm(board.getId());

        //then
        assertThat(form.getTitle()).isEqualTo("제목");

        //verify
        verify(bulletinRepository, times(1)).findById(board.getId());
        verify(uploadFileRepository, times(1)).findUploadFiles(board.getId());
    }

    @Test
    @DisplayName("게시글 수정")
    void update() {
        //given
        Bulletin board = getBulletin("제목");

        Bulletin updatedBoard = getBulletin("제목수정");
        UpdateBulletinForm form = new UpdateBulletinForm(updatedBoard, new ArrayList<>());

        doReturn(Optional.ofNullable(board)).when(bulletinRepository)
                .findById(form.getId());

        doNothing().when(imageService)
                .updateImage(board, form.getDeleteImageIds(), form.getNewImages());

        //when
        bulletinService.update(form);

        //then
        assertThat(board.getTitle()).isEqualTo("제목수정");
    }

    @Test
    @DisplayName("게시글 삭제")
    void delete() {
        //given
        Bulletin board = getBulletin("제목");

        doReturn(Optional.ofNullable(board)).when(bulletinRepository)
                .findById(board.getId());

        //expected
        bulletinService.delete(board.getId());
        verify(bulletinRepository, times(1)).delete(board);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 아이디로 조회 시 예외 발생")
    void notFoundBoardExceptionById() {
        //given
        doReturn(Optional.empty()).when(bulletinRepository)
                .findById(1L);

        //expected
        Assertions.assertThrows(NotFoundBoardException.class,
                ()-> bulletinService.read(1L));
    }


    private Bulletin getBulletin(String title) {
        return Bulletin.builder()
                .user(getUser())
                .title(title)
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

    private SaveBulletinForm getSaveBulletinForm() {
        List<MultipartFile> images = new ArrayList<>();
        return SaveBulletinForm.builder()
                .title("제목")
                .content("내용")
                .region("지역")
                .images(images)
                .build();
    }
}