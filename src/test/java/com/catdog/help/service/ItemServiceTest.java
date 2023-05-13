package com.catdog.help.service;

import com.catdog.help.domain.board.*;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.repository.*;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.item.EditItemForm;
import com.catdog.help.web.form.item.PageItemForm;
import com.catdog.help.web.form.item.ReadItemForm;
import com.catdog.help.web.form.item.SaveItemForm;
import com.catdog.help.web.form.search.ItemSearch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UploadFileRepository uploadFileRepository;

    @Mock
    ImageService imageService;

    @Mock
    LikeRepository likeRepository;

    @Mock
    SearchQueryRepository searchQueryRepository;


    @Test
    @DisplayName("나눔글 저장")
    void save() {
        //given
        User user = getUser();
        SaveItemForm form = getSaveItemForm("제목");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        doNothing().when(imageService)
                .addImage(any(Item.class), any(List.class));

        when(itemRepository.save(any(Item.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        //expected
        itemService.save(form, user.getNickname());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("나눔글 단건 조회 성공")
    void read() {
        //given
        Item board = getItem("제목");
        List<UploadFile> images = new ArrayList<>();

        doReturn(Optional.ofNullable(board)).when(itemRepository)
                .findById(board.getId());

        doReturn(images).when(uploadFileRepository)
                .findByBoardId(board.getId());

        doReturn(2L).when(likeRepository)
                .countByBoardId(board.getId());

        //expected
        ReadItemForm form = itemService.read(board.getId());
        assertThat(form.getItemName()).isEqualTo(board.getItemName());
    }

    @Test
    @DisplayName("닉네임으로 나눔글 수 조회")
    void getCountByNickname() {
        //given
        Item board = getItem("제목");
        List<Item> boards = new ArrayList<>();
        boards.add(board);

        doReturn(boards).when(itemRepository)
                .findAll();

        //when
        Long result = itemService.countByNickname("닉네임");

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("닉네임으로 나눔글 페이지 조회")
    void getPageByNickname() {
        //given
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        Page<Item> page = Page.empty();

        doReturn(page).when(itemRepository)
                .findPageByNickname("닉네임", pageable);

        //expected
        Page<PageItemForm> formPage = itemService.getPageByNickname("닉네임", pageable);
        verify(itemRepository, times(1)).findPageByNickname("닉네임", pageable); // TODO: 2023-04-25 map이 잘 작동하는지 확인 부족함.
    }

    @Test
    @DisplayName("로그인 사용자가 좋아하는 나눔글 수 조회")
    void countLikeItems() {
        //given
        Item board = getItem("제목");
        Like.builder()
                .board(board)
                .user(board.getUser())
                .build();
        List<Item> boards = new ArrayList<>();
        boards.add(board);

        doReturn(boards).when(itemRepository)
                .findAll();

        //when
        Long result = itemService.countLikeItem("닉네임");

        //then
        assertThat(result).isEqualTo(1L);
    }

    @Test
    @DisplayName("해당 사용자가 좋아하는 나눔글 페이지 조회")
    void getLikeBulletins() {
        //given
        User user = getUser();

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "board_id");
        Page<Item> page = Page.empty();

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        doReturn(page).when(itemRepository)
                .findLikeItems(user.getId(), pageable);

        //expected
        Page<PageItemForm> formPage = itemService.getLikeItems("닉네임", pageable);
        verify(userRepository, times(1)).findByNickname("닉네임");
        verify(itemRepository, times(1)).findLikeItems(user.getId(), pageable); // TODO: 2023-04-25 map이 잘 작동하는지 확인 부족함.
    }

    @Test
    @DisplayName("검색 조건에 맞는 나눔글 페이지 조회")
    void searchPageByCond() {
        //given
        Pageable pageable = PageRequest.of(0, 6, Sort.Direction.DESC, "id");
        Page<Item> page = Page.empty();

        ItemSearch search = ItemSearch.builder()
                .title("검색제목")
                .itemName("검색상품명")
                .build();

        doReturn(page).when(searchQueryRepository)
                .searchItem(search.getTitle(), search.getItemName(), pageable);

        //expected
        Page<PageItemForm> formPage = itemService.search(search, pageable);
        verify(searchQueryRepository, times(1)).searchItem(search.getTitle(), search.getItemName(), pageable);
    }

    @Test
    @DisplayName("나눔글 수정 양식 호출")
    void getEditForm() {
        //given
        Item board = getItem("제목");
        List<UploadFile> oldImages = getUploadFiles();

        doReturn(Optional.ofNullable(board)).when(itemRepository)
                .findById(board.getId());

        doReturn(oldImages).when(uploadFileRepository)
                .findByBoardId(board.getId());

        //when
        EditItemForm form = itemService.getEditForm(board.getId());

        //then
        assertThat(form.getTitle()).isEqualTo(board.getTitle());

        //verify
        verify(itemRepository, times(1)).findById(board.getId());
        verify(uploadFileRepository, times(1)).findByBoardId(board.getId());
    }

    @Test
    @DisplayName("나눔글 수정")
    void update() {
        //given
        Item board = getItem("제목");
        EditItemForm editForm = getAfterEditForm("제목수정");

        doReturn(Optional.ofNullable(board)).when(itemRepository)
                .findById(editForm.getId());

        doNothing().when(imageService)
                .updateLeadImage(editForm.getNewLeadImage(), editForm.getId());

        doNothing().when(imageService)
                .updateImage(board, editForm.getDeleteImageIds(), editForm.getNewImages());

        //when
        itemService.update(editForm);

        //then
        assertThat(board.getTitle()).isEqualTo("제목수정");
    }

    @Test
    @DisplayName("STILL -> COMP 상품 상태 변경")
    void changeSTILLToCOMP() {
        //given
        Item board = getItem("제목");

        doReturn(Optional.ofNullable(board)).when(itemRepository)
                .findById(board.getId());
        //when
        itemService.changeStatus(board.getId());

        //then
        assertThat(board.getStatus()).isEqualTo(ItemStatus.COMP);
    }

    @Test
    @DisplayName("COMP -> STILL 상품 상태 변경")
    void changeCOMPToSTILL() {
        //given
        Item board = getItem("제목");

        doReturn(Optional.ofNullable(board)).when(itemRepository)
                .findById(board.getId());
        itemService.changeStatus(board.getId());

        //when
        itemService.changeStatus(board.getId());

        //then
        assertThat(board.getStatus()).isEqualTo(ItemStatus.STILL);
    }

    @Test
    @DisplayName("나눔글 삭제")
    void delete() {
        //given
        Item board = getItem("제목");

        doReturn(Optional.ofNullable(board)).when(itemRepository)
                .findById(board.getId());

        //expected
        itemService.delete(board.getId());
        verify(itemRepository, times(1)).delete(board);
    }

    @Test
    @DisplayName("존재하지 않는 나눔글 아이디로 조회 시 예외 발생")
    void notFoundBoardExceptionById() {
        //given
        doReturn(Optional.empty()).when(itemRepository)
                .findById(1L);

        //expected
        Assertions.assertThrows(BoardNotFoundException.class,
                ()-> itemService.delete(1L));
    }


    private EditItemForm getAfterEditForm(String title) {
        Item updatedBoard = getItem(title);
        List<ReadImageForm> readImageForms = getReadUploadFileForms();
        return getEditItemForm(updatedBoard, readImageForms);
    }

    private EditItemForm getEditItemForm(Item board, List<ReadImageForm> forms) {
        return EditItemForm.builder()
                .findBoard(board)
                .readForms(forms)
                .build();
    }

    private List<ReadImageForm> getReadUploadFileForms() {
        ReadImageForm readForm = getReadUploadFileForm();
        List<ReadImageForm> forms = new ArrayList<>();
        forms.add(readForm);
        return forms;
    }

    private ReadImageForm getReadUploadFileForm() {
        UploadFile image = getUploadFile();
        return new ReadImageForm(image);
    }

    private List<UploadFile> getUploadFiles() {
        List<UploadFile> oldImages = new ArrayList<>();
        UploadFile image = getUploadFile();
        oldImages.add(image);
        return oldImages;
    }

    private UploadFile getUploadFile() {
        return UploadFile.builder()
                .uploadFileName("uploadA")
                .storeFileName("storeA")
                .build();
    }

    private SaveItemForm getSaveItemForm(String title) {
        return SaveItemForm.builder()
                .title(title)
                .content("내용")
                .itemName("테스트상품")
                .price(1000)
                .region("부산")
                .images(List.of())
                .build();
    }

    private Item getItem(String title) {
        User user = getUser();
        return Item.builder()
                .user(user)
                .title(title)
                .content("내용")
                .itemName("테스트상품")
                .price(1000)
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
}