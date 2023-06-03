package com.catdog.help.service;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.repository.LostRepository;
import com.catdog.help.repository.SearchQueryRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.EditLostForm;
import com.catdog.help.web.form.lost.PageLostForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import com.catdog.help.web.form.search.LostSearch;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class LostServiceTest {

    @InjectMocks
    private LostService lostService;

    @Mock
    private LostRepository lostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private UploadFileRepository uploadFileRepository;

    @Mock
    private SearchQueryRepository searchQueryRepository;


    @Test
    @DisplayName("실종글 저장 성공")
    void saveLostBoard() {
        //given
        User user = getUser();
        Lost board = getLost(user, "제목");
        SaveLostForm form = getSaveLostForm();

        doReturn(Optional.of(user)).when(userRepository)
                .findByNickname("닉네임");

        doNothing().when(imageService)
                .addImage(any(Lost.class), any(List.class));

        doReturn(board).when(lostRepository)
                .save(any(Lost.class));

        //when
        lostService.save(form, "닉네임");

        //then
        verify(userRepository, times(1)).findByNickname("닉네임");
        verify(imageService, times(1)).addImage(any(Lost.class), any(List.class));
        verify(lostRepository, times(1)).save(any(Lost.class));
    }

    @Test
    @DisplayName("실종글 단건 조회")
    void readOne() {
        //given
        User user = getUser();
        Lost board = getLost(user, "제목");
        List<UploadFile> imageForms = new ArrayList<>();
        imageForms.add(new UploadFile("업로드", "저장"));

        doReturn(Optional.of(board)).when(lostRepository)
                .findById(board.getId());

        doReturn(imageForms).when(uploadFileRepository)
                .findByBoardId(board.getId());

        //when
        ReadLostForm form = lostService.read(board.getId());

        //then
        assertThat(form.getTitle()).isEqualTo("제목");
        assertThat(form.getImages().get(0)).isInstanceOf(ReadImageForm.class);
    }

    @Test
    @DisplayName("키는 지역, 값은 지역별 실종글 수를 가지는 맵을 반환")
    void getCountByRegion() {
        //given
        User user = getUser();
        Lost board = getLost(user, "제목");
        List<Lost> boards = new ArrayList<>();
        boards.add(board);

        doReturn(boards).when(lostRepository)
                .findAll();

        //when
        Map<String, Long> result = lostService.getCountByRegion();

        //then
        assertThat(result.get("부산")).isEqualTo(1L);
    }

    @Test
    @DisplayName("닉네임으로 실종글 수 조회")
    void getCountByNickname() {
        //given
        User user = getUser();
        Lost board = getLost(user, "제목");
        List<Lost> boards = new ArrayList<>();
        boards.add(board);

        doReturn(1L).when(lostRepository)
                .countByNickname("닉네임");

        //when
        Long result = lostService.countByNickname("닉네임");

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("닉네임으로 실종글 페이지 조회")
    void getPageByNickname() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Page<Lost> page = Page.empty();

        doReturn(page).when(lostRepository)
                .findPageByNickname("닉네임", pageable);

        //expected
        Page<PageLostForm> formPage = lostService.getPageByNickname("닉네임", pageable);
        verify(lostRepository, times(1)).findPageByNickname("닉네임", pageable);
    }

    @Test
    @DisplayName("검색 조건에 맞는 실종글 페이지 조회")
    void searchPageByCond() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Page<Lost> page = Page.empty();

        LostSearch search = new LostSearch("부산");

        doReturn(page).when(searchQueryRepository)
                .searchLost(search.getRegion(), pageable);

        //expected  TODO: 2023-04-25 map이 잘 작동하는지 확인 부족함.
        Page<PageLostForm> formPage = lostService.search(search, pageable);
        verify(searchQueryRepository, times(1)).searchLost(search.getRegion(), pageable);
    }

    @Test
    @DisplayName("실종글 수정 양식 호출")
    void getEditForm() {
        //given
        User user = getUser();
        Lost board = getLost(user, "제목");
        List<UploadFile> oldImages = getUploadFiles();

        doReturn(Optional.of(board)).when(lostRepository)
                .findById(board.getId());

        doReturn(oldImages).when(uploadFileRepository)
                .findByBoardId(board.getId());

        //when
        EditLostForm form = lostService.getEditForm(board.getId());

        //then
        assertThat(form.getTitle()).isEqualTo(board.getTitle());

        //verify
        verify(lostRepository, times(1)).findById(board.getId());
        verify(uploadFileRepository, times(1)).findByBoardId(board.getId());
    }

    @Test
    @DisplayName("실종글 수정 성공")
    void update() {
        //given
        Lost board = getLost(getUser(), "제목");
        EditLostForm form = getAfterEditLostForm(getUser(), "제목수정");

        doReturn(Optional.of(board)).when(lostRepository)
                .findById(form.getId());

        doNothing().when(imageService)
                .updateImage(board, form.getDeleteImageIds(), form.getNewImages());

        //when
        lostService.update(form);

        //then
        assertThat(board.getTitle()).isEqualTo("제목수정");
    }

    @Test
    @DisplayName("실종글 삭제")
    void delete() {
        //given
        Lost board = getLost(getUser(), "제목");

        doReturn(Optional.of(board)).when(lostRepository)
                .findById(board.getId());

        //expected
        lostService.delete(board.getId());
        verify(lostRepository, times(1)).delete(board);
    }

    @Test
    @DisplayName("존재하지 않는 실종글 아이디로 조회 시 예외 발생")
    void boardNotFoundExceptionById() {
        //given
        doReturn(Optional.empty()).when(lostRepository)
                .findById(1L);

        //expected
        assertThrows(BoardNotFoundException.class,
                ()-> lostService.delete(1L));
    }


    private List<ReadImageForm> getReadImageForms() {
        ReadImageForm form = new ReadImageForm(
                UploadFile.builder()
                        .uploadFileName("uploadFileName")
                        .storeFileName("storeFileName")
                        .build()
        );
        List<ReadImageForm> oldImages = new ArrayList<>();
        oldImages.add(form);
        return oldImages;
    }

    private List<UploadFile> getUploadFiles() {
        List<UploadFile> oldImages = new ArrayList<>();
        UploadFile file = UploadFile.builder()
                .uploadFileName("uploadFileName")
                .storeFileName("storeFileName")
                .build();
        oldImages.add(file);
        return oldImages;
    }

    private EditLostForm getAfterEditLostForm(User user, String title) {
        Lost board = getLost(user, title);
        List<ReadImageForm> readImageForms = getReadImageForms();
        return new EditLostForm(board, readImageForms);
    }

    private SaveLostForm getSaveLostForm() {
        return SaveLostForm.builder()
                .title("제목")
                .content("내용")
                .region("부산")
                .breed("품종")
                .lostDate(LocalDate.now())
                .lostPlace("실종장소")
                .gratuity(100000)
                .images(List.of())
                .build();
    }

    private Lost getLost(User user, String title) {
        return Lost.builder()
                .user(user)
                .title(title)
                .content("내용")
                .region("부산")
                .breed("품종")
                .lostDate(LocalDate.now())
                .lostPlace("실종장소")
                .gratuity(100000)
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