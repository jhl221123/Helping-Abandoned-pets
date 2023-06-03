package com.catdog.help.web.controller;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.item.EditItemForm;
import com.catdog.help.web.form.item.ReadItemForm;
import com.catdog.help.web.form.item.SaveItemForm;
import com.catdog.help.web.form.search.ItemSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String ITEM_NAME = "itemName";
    private static final String PRICE = "price";
    private static final String REGION = "region";
    private static final String PAGE = "page";
    private static final String SIZE = "size";

    @InjectMocks
    ItemController itemController;

    @Mock
    ItemService itemService;

    @Mock
    BoardService boardService;

    @Mock
    LikeService likeService;

    @Mock
    ViewUpdater viewUpdater;

    private MockMvc mockMvc;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }


    @Test
    @DisplayName("나눔글 작성 양식 호출")
    void getSaveForm() throws Exception {
        //expected
        mockMvc.perform(get("/items/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("items/create"));
    }

    @Test
    @DisplayName("나눔글 작성 성공")
    void saveBoard() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile("images", "test.png", "image/png", "test".getBytes());

        doNothing().when(itemService)
                .save(any(SaveItemForm.class), eq("닉네임"));

        //expected
        mockMvc.perform(multipart("/items/new").file(image)
                        .contentType(MULTIPART_FORM_DATA)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(ITEM_NAME, "상품명")
                        .param(PRICE, String.valueOf(1000))
                        .param(REGION, "부산")
                )
                .andExpect(redirectedUrl("/items?page=0"));
    }

    @Test
    @DisplayName("이미지파일을 업로드하지 않아 나눔글 작성 실패")
    void failSaveByImage() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile("images", "", "", (byte[]) null);

        //expected
        mockMvc.perform(multipart("/items/new").file(image)
                        .contentType(MULTIPART_FORM_DATA)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(ITEM_NAME, "상품명")
                        .param(PRICE, String.valueOf(1000))
                        .param(REGION, "부산")
                )
                .andExpect(view().name("items/create"));
    }

    @Test
    @DisplayName("검증으로 인해 나눔글 작성 실패")
    void failSaveByValidate() throws Exception {
       //expected
        mockMvc.perform(post("/items/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "")
                        .param(CONTENT, "")
                        .param(ITEM_NAME, "")
                        .param(PRICE, "")
                        .param(REGION, "")
                )
                .andExpect(view().name("items/create"));
    }

    @Test
    @DisplayName("검색 조건에 맞는 나눔글 페이지 조회")
    void getPage() throws Exception {
        //given
        Page page = Mockito.mock(Page.class);
        doReturn(page).when(itemService)
                .search(any(ItemSearch.class), any(Pageable.class));

        //expected
        mockMvc.perform(get("/items")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(TITLE, "검색제목")
                        .param(ITEM_NAME, "검색상품명")
                        .param(PAGE, String.valueOf(0))
                        .param(SIZE, String.valueOf(6))
                )
                .andExpect(view().name("items/list"));
    }

    @Test
    @DisplayName("나눔글 단건 조회")
    void readOne() throws Exception {
        //given
        doNothing().when(viewUpdater)
                .addView(eq(2L), any(HttpServletRequest.class), any(HttpServletResponse.class));

        ReadItemForm form = getReadItemForm();
        doReturn(form).when(itemService)
                .read(2L);

        doReturn(false).when(likeService)
                .isLike(2L, "닉네임");

        //expected
        mockMvc.perform(get("/items/{id}", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("items/detail"));
    }

    @Test
    @DisplayName("나눔글 좋아요 버튼 클릭")
    void clickLikeButton() throws Exception {
        //given
        doNothing().when(likeService)
                .clickLike(2L, "닉네임");

        //expected
        mockMvc.perform(get("/items/{id}/like", 2)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/items/" + 2));
    }

    @Test
    @DisplayName("나눔글 수정 양식 호출 성공")
    void getEditForm() throws Exception {
        //given
        EditItemForm form = getBeforeEditForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(itemService)
                .getEditForm(2L);

        //expected
        mockMvc.perform(get("/items/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("items/edit"));
    }

    @Test
    @DisplayName("나눔글 수정 성공")
    void edit() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(itemService)
                .update(any(EditItemForm.class));

        //expected
        mockMvc.perform(post("/items/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(ITEM_NAME, "상품명")
                        .param(PRICE, String.valueOf(1000))
                        .param(REGION, "부산")
                )
                .andExpect(redirectedUrl("/items/" + 2));
    }

    @Test
    @DisplayName("검증으로 인한 나눔글 수정 실패")
    void failEditByValidate() throws Exception {
        //given
        EditItemForm form = getBeforeEditForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(itemService)
                .getEditForm(2L);

        //expected
        mockMvc.perform(post("/items/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "")
                        .param(CONTENT, "")
                        .param(ITEM_NAME, "")
                        .param(PRICE, "")
                        .param(REGION, "")
                )
                .andExpect(view().name("items/edit"));
    }

    @Test
    @DisplayName("상품 상태 변경 성공")
    void changeItemStatus() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(itemService)
                .changeStatus(2L);

        //expected
        mockMvc.perform(get("/items/{id}/status", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/items/" + 2));
    }

    @Test
    @DisplayName("나눔글 삭제 성공")
    void delete() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(itemService)
                .delete(2L);

        //expected
        mockMvc.perform(get("/items/{id}/delete", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/items?page=0"));
    }

    @Test
    @DisplayName("작성자 외 접근으로 요청 실패")
    void failRequestWriterDiscord() throws Exception {
        //given
        doReturn("작성자").when(boardService)
                .getWriter(2L);

        //expected
        mockMvc.perform(get("/items/{id}/edit", 2)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "다른사용자")
                )
                .andExpect(redirectedUrl("/"));
    }


    private EditItemForm getBeforeEditForm() {
        Item board = getItem("제목");
        List<ReadImageForm> oldImages = getReadImageForms();
        return new EditItemForm(board, oldImages);
    }

    private List<ReadImageForm> getReadImageForms() {
        List<ReadImageForm> oldImages = new ArrayList<>();
        UploadFile image = getImage();
        ReadImageForm imageForm = new ReadImageForm(image);
        oldImages.add(imageForm);
        return oldImages;
    }

    private ReadItemForm getReadItemForm() {
        UploadFile image = getImage();
        ReadImageForm imageForm = new ReadImageForm(image);

        return ReadItemForm.builder()
                .findBoard(getItem("제목"))
                .images(List.of(imageForm))
                .likeSize(2)
                .build();
    }

    private UploadFile getImage() {
        return UploadFile.builder()
                .uploadFileName("대표이미지")
                .storeFileName("저장이름")
                .build();
    }

    private SaveItemForm getSaveItemForm(String title) {
        return SaveItemForm.builder()
                .title(title)
                .content("내용")
                .itemName("테스트상품")
                .price(1000)
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