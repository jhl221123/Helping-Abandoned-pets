package com.catdog.help.web.controller;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.service.LostService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.EditLostForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import com.catdog.help.web.form.search.LostSearch;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LostControllerTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String REGION = "region";
    private static final String BREED = "breed";
    private static final String LOST_DATE = "lostDate";
    private static final String LOST_PLACE = "lostPlace";
    private static final String GRATUITY = "gratuity";
    private static final String PAGE = "page";
    private static final String SIZE = "size";

    @InjectMocks
    private LostController lostController;

    @Mock
    private LostService lostService;

    @Mock
    private BoardService boardService;

    @Mock
    private ViewUpdater viewUpdater;

    @Mock
    private LikeService likeService;

    @Mock
    private CommentService commentService;

    private MockMvc mockMvc;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(lostController).
                setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }


    @Test
    @DisplayName("실종글 저장 양식 호출")
    void getSaveForm() throws Exception {
        //expected
        mockMvc.perform(get("/lost/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(model().attributeExists("saveForm"))
                .andExpect(view().name("lost/create"));
    }

    @Test
    @DisplayName("실종글 저장 성공")
    void save() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile("images", "test.png", "image/png", "test".getBytes());

        doReturn(2L).when(lostService)
                .save(any(SaveLostForm.class), eq("닉네임"));

        //expected
        mockMvc.perform(multipart("/lost/new").file(image)
                        .contentType(MULTIPART_FORM_DATA)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(REGION, "부산")
                        .param(BREED, "품종")
                        .param(LOST_DATE, String.valueOf(LocalDate.now()))
                        .param(LOST_PLACE, "실종장소")
                        .param(GRATUITY, String.valueOf(100000))
                )
                .andExpect(redirectedUrl("/lost/" + 2));
    }

    @Test
    @DisplayName("이미지파일을 업로드하지 않아 실종글 저장 실패")
    void failSaveByImage() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile("images", "", "", (byte[]) null);

        //expected
        mockMvc.perform(multipart("/lost/new").file(image)
                        .contentType(MULTIPART_FORM_DATA)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(REGION, "부산")
                        .param(BREED, "품종")
                        .param(LOST_DATE, String.valueOf(LocalDate.now()))
                        .param(LOST_PLACE, "실종장소")
                        .param(GRATUITY, String.valueOf(100000))
                )
                .andExpect(view().name("lost/create"));
    }

    @Test
    @DisplayName("검증으로 인한 실종글 저장 실패")
    void failSaveByValidate() throws Exception {
        //expected
        mockMvc.perform(post("/lost/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "")
                        .param(CONTENT, "")
                        .param(REGION, "")
                        .param(BREED, "")
                        .param(LOST_DATE, "")
                        .param(LOST_PLACE, "")
                        .param(GRATUITY, "")
                )
                .andExpect(view().name("lost/create"));
    }

    @Test
    @DisplayName("검색 조건에 맞는 실종글 페이지 조회")
    void getPage() throws Exception {
        //given
        Page page = Mockito.mock(Page.class);

        doReturn(page).when(lostService)
                .search(any(LostSearch.class), any(Pageable.class));

        //expected
        mockMvc.perform(get("/lost")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(REGION, "검색지역")
                        .param(PAGE, String.valueOf(0))
                        .param(SIZE, String.valueOf(10))
                )
                .andExpect(view().name("lost/list"));
    }

    @Test
    @DisplayName("실종글 단건 조회")
    void readOne() throws Exception {
        //given
        doNothing().when(viewUpdater)
                .addView(eq(2L), any(HttpServletRequest.class), any(HttpServletResponse.class));

        ReadLostForm form = getReadLostForm();
        doReturn(form).when(lostService)
                .read(2L);

        List<CommentForm> forms = new ArrayList<>();
        doReturn(forms).when(commentService)
                .readByBoardId(2L);

        //expected
        mockMvc.perform(get("/lost/{id}", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("lost/detail"));
    }

    @Test
    @DisplayName("실종글 수정 양식 호출 성공")
    void getEditForm() throws Exception {
        //given
        EditLostForm form = getBeforeEditForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(lostService)
                .getEditForm(2L);

        //expected
        mockMvc.perform(get("/lost/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("lost/edit"));
    }

    @Test
    @DisplayName("실종글 수정 성공")
    void edit() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(lostService)
                .update(any(EditLostForm.class));

        //expected
        mockMvc.perform(post("/lost/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(REGION, "부산")
                        .param(BREED, "품종")
                        .param(LOST_DATE, String.valueOf(LocalDate.now()))
                        .param(LOST_PLACE, "실종장소")
                        .param(GRATUITY, String.valueOf(100000))
                )
                .andExpect(redirectedUrl("/lost/" + 2));
    }

    @Test
    @DisplayName("검증으로 인한 실종글 수정 실패")
    void failEditByValidate() throws Exception {
        //given
        EditLostForm form = getBeforeEditForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(lostService)
                .getEditForm(2L);

        //expected
        mockMvc.perform(post("/lost/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "")
                        .param(CONTENT, "")
                        .param(REGION, "")
                        .param(BREED, "")
                        .param(LOST_DATE, "")
                        .param(LOST_PLACE, "")
                        .param(GRATUITY, "")
                )
                .andExpect(view().name("lost/edit"));
    }

    @Test
    @DisplayName("실종글 삭제 성공")
    void delete() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(lostService)
                .delete(2L);

        //expected
        mockMvc.perform(get("/lost/{id}/delete", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/lost?page=0"));
    }

    @Test
    @DisplayName("작성자 외 접근으로 요청 실패")
    void failRequestWriterDiscord() throws Exception {
        //given
        doReturn("작성자").when(boardService)
                .getWriter(2L);

        //expected
        mockMvc.perform(get("/lost/{id}/edit", 2)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "다른사용자")
                )
                .andExpect(redirectedUrl("/"));
    }


    private EditLostForm getBeforeEditForm() {
        User user = getUser();
        Lost board = getLost(user);
        List<ReadImageForm> oldImages = getReadImageForms();
        return new EditLostForm(board, oldImages);
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

    private ReadLostForm getReadLostForm() {
        User user = getUser();
        Lost board = getLost(user);
        return new ReadLostForm(board, List.of());
    }

    private Lost getLost(User user) {
        return Lost.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("부산")
                .breed("말티즈")
                .lostDate(LocalDate.of(2023, 05, 26))
                .lostPlace("행복아파트")
                .gratuity(100000)
                .build();
    }

    private User getUser() {
        return User.builder()
                .emailId("test@test.test")
                .password("12341234")
                .nickname("닉네임")
                .name("이름")
                .age(22)
                .gender(Gender.WOMAN)
                .build();
    }
}