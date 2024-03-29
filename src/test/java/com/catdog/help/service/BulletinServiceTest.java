package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.Like;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.repository.*;
import com.catdog.help.web.form.bulletin.EditBulletinForm;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.search.BulletinSearch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    SearchQueryRepository searchQueryRepository;


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
    void readOne() {
        //given
        Bulletin board = getBulletin("제목");
        List<UploadFile> imageForms = new ArrayList<>();
        imageForms.add(new UploadFile("업로드", "저장"));

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
        assertThat(form.getImages().get(0)).isInstanceOf(ReadImageForm.class);
        assertThat(form.getLikeSize()).isEqualTo(3);
    }

    @Test
    @DisplayName("닉네임으로 게시글 수 조회")
    void getCountByNickname() {
        //given
        Bulletin board = getBulletin("제목");
        List<Bulletin> boards = new ArrayList<>();
        boards.add(board);

        doReturn(1L).when(bulletinRepository)
                .countByNickname("닉네임");

        //when
        Long result = bulletinService.countByNickname("닉네임");

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("작성자가 해당 닉네임인 게시글 페이지 조회")
    void getPageByNickname() {
        //given
        List<Bulletin> bulletins = new ArrayList<>();
        Bulletin bulletin = getBulletin("제목");
        bulletins.add(bulletin);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Page page = new PageImpl(bulletins, pageable, 10);

        doReturn(page).when(bulletinRepository)
                .findPageByNickname("닉네임", pageable);

        //expected
        Page<PageBulletinForm> formPage = bulletinService.getPageByNickname("닉네임", pageable);
        assertThat(formPage.getContent().get(0).getTitle()).isEqualTo(bulletin.getTitle());
    }

    @Test
    @DisplayName("로그인 사용자가 좋아하는 게시글 수 조회")
    void countLikeBulletin() {
        //given
        Bulletin board = getBulletin("제목");
        Like.builder()
                .board(board)
                .user(board.getUser())
                .build();
        List<Bulletin> boards = new ArrayList<>();
        boards.add(board);

        doReturn(1L).when(bulletinRepository)
                .countLikeBulletinByNickname(board.getUser().getNickname());

        //when
        Long result = bulletinService.countLikeBulletin("닉네임");

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("해당 사용자가 좋아하는 게시글 페이지 조회")
    void getLikeBulletins() {
        //given
        User user = getUser();

        List<Bulletin> bulletins = new ArrayList<>();
        Bulletin bulletin = getBulletin("검색제목");
        bulletins.add(bulletin);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Page page = new PageImpl(bulletins, pageable, 10);

        doReturn(Optional.of(user)).when(userRepository)
                .findByNickname("닉네임");

        doReturn(page).when(bulletinRepository)
                .findLikeBulletins(user.getId(), pageable);

        //expected
        Page<PageBulletinForm> formPage = bulletinService.getLikeBulletins("닉네임", pageable);
        assertThat(formPage.getContent().get(0).getTitle()).isEqualTo(bulletin.getTitle());
    }

    @Test
    @DisplayName("검색 조건에 맞는 게시글 페이지 조회")
    void searchPageByCond() {
        //given
        List<Bulletin> bulletins = new ArrayList<>();
        Bulletin bulletin = getBulletin("검색제목");
        bulletins.add(bulletin);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Page page = new PageImpl(bulletins, pageable, 10);

        BulletinSearch search = BulletinSearch.builder()
                .title("검색제목")
                .region("부산")
                .build();

        doReturn(page).when(searchQueryRepository)
                .searchBulletin(search.getTitle(), search.getRegion(), pageable);

        //expected
        Page<PageBulletinForm> formPage = bulletinService.search(search, pageable);
        assertThat(formPage.getContent().get(0).getTitle()).isEqualTo(bulletin.getTitle());
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
        assertThat(form.getTitle()).isEqualTo(board.getTitle());

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
    void boardNotFoundExceptionById() {
        //given
        doReturn(Optional.empty()).when(bulletinRepository)
                .findById(1L);

        //expected
        assertThrows(BoardNotFoundException.class,
                ()-> bulletinService.delete(1L));
    }


    private Bulletin getBulletin(String title) {
        return Bulletin.builder()
                .user(getUser())
                .title(title)
                .content("내용")
                .region("부산")
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
                .region("부산")
                .images(images)
                .build();
    }

    private EditBulletinForm getAfterEditForm(String title) {
        Bulletin updatedBoard = getBulletin(title);
        List<ReadImageForm> oldImages = new ArrayList<>();
        return new EditBulletinForm(updatedBoard, oldImages);
    }
}