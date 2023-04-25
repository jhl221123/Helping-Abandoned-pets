package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.repository.BulletinRepository;
import com.catdog.help.repository.LikeRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.bulletin.EditBulletinForm;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.image.ReadImageForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    UploadFileRepository uploadFileRepository;

    @Mock
    LikeRepository likeRepository;


    @Test
    @DisplayName("게시글 저장")
    void save() {
        //given
        User user = getUser();
        Bulletin board = getBulletin("제목");
        SaveBulletinForm form = getSaveForm();

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
        List<ReadImageForm> imageForms = new ArrayList<>();

        doReturn(Optional.ofNullable(board)).when(bulletinRepository)
                .findById(board.getId());

        doReturn(imageForms).when(uploadFileRepository)
                .findByBoardId(board.getId());

        doReturn(3L).when(likeRepository)
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
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Page<Bulletin> page = Page.empty();

        doReturn(page).when(bulletinRepository)
                .findPageBy(pageable);

        //expected
        Page<PageBulletinForm> formPage = bulletinService.getPage(pageable);
        verify(bulletinRepository, times(1)).findPageBy(pageable); // TODO: 2023-04-25 map이 잘 작동하는지 확인 부족함.
    }

    @Test
    @DisplayName("작성자 닉네임 반환")
    void getWriter() {
        //given
        doReturn("닉네임").when(bulletinRepository)
                .findNicknameById(1L);

        //expected
        bulletinService.getWriter(1L);
        verify(bulletinRepository, times(1)).findNicknameById(1L);
    }

    @Test
    @DisplayName("게시글 수정 양식 호출")
    void getEditForm() {
        //given
        Bulletin board = getBulletin("제목");
        List<ReadImageForm> oldImages = new ArrayList<>();

        doReturn(Optional.ofNullable(board)).when(bulletinRepository)
                .findById(board.getId());

        doReturn(oldImages).when(uploadFileRepository)
                .findByBoardId(board.getId());

        //when
        EditBulletinForm form = bulletinService.getEditForm(board.getId());

        //then
        assertThat(form.getTitle()).isEqualTo("제목");

        //verify
        verify(bulletinRepository, times(1)).findById(board.getId());
        verify(uploadFileRepository, times(1)).findByBoardId(board.getId());
    }

    @Test
    @DisplayName("게시글 수정")
    void update() {
        //given
        Bulletin board = getBulletin("제목");

        EditBulletinForm form = getAfterEditForm("제목수정");

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
        Assertions.assertThrows(BoardNotFoundException.class,
                ()-> bulletinService.delete(1L));
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

    private SaveBulletinForm getSaveForm() {
        List<MultipartFile> images = new ArrayList<>();
        return SaveBulletinForm.builder()
                .title("제목")
                .content("내용")
                .region("지역")
                .images(images)
                .build();
    }

    private EditBulletinForm getAfterEditForm(String title) {
        Bulletin updatedBoard = getBulletin(title);
        List<ReadImageForm> oldImages = new ArrayList<>();
        return new EditBulletinForm(updatedBoard, oldImages);
    }
}