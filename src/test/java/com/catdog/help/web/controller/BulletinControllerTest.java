package com.catdog.help.web.controller;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.bulletin.EditBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.image.ReadImageForm;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BulletinControllerTest {

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String REGION = "region";
    private static final String PAGE = "page";
    private static final String SIZE = "size";

    @InjectMocks
    private BulletinController bulletinController;

    @Mock
    private BulletinService bulletinService;

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
        mockMvc = MockMvcBuilders.standaloneSetup(bulletinController).
                setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    @DisplayName("게시글 저장 양식 호출")
    void getSaveForm() throws Exception {
        //expected
        mockMvc.perform(get("/bulletins/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(model().attributeExists("saveForm"))
                .andExpect(view().name("bulletins/create"));
    }

    @Test
    @DisplayName("게시글 저장 성공")
    void save() throws Exception {
        //given
        doReturn(2L).when(bulletinService)
                .save(any(SaveBulletinForm.class), eq("닉네임"));

        //expected
        mockMvc.perform(post("/bulletins/new")
                        .contentType(MULTIPART_FORM_DATA)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(REGION, "지역")
                )
                .andExpect(redirectedUrl("/bulletins/" + 2));
    }

    @Test
    @DisplayName("검증으로 인한 게시글 저장 실패")
    void failSaveByValidate() throws Exception {
        //expected
        mockMvc.perform(post("/bulletins/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "")
                        .param(CONTENT, "")
                        .param(REGION, "")
                )
                .andExpect(view().name("bulletins/create"));
    }

    @Test
    @DisplayName("게시글 목록 조회")
    void getList() throws Exception {
        //given
        Page page = Mockito.mock(Page.class);
        doReturn(page).when(bulletinService)
                .getPage(any(Pageable.class));

        //expected
        mockMvc.perform(get("/bulletins")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(PAGE, String.valueOf(0))
                        .param(SIZE, String.valueOf(10))
                )
                .andExpect(view().name("bulletins/list"));
    }

    @Test
    @DisplayName("게시글 단건 조회")
    void readOne() throws Exception {
        //given
        doNothing().when(viewUpdater)
                .addView(eq(2L), any(HttpServletRequest.class), any(HttpServletResponse.class));

        ReadBulletinForm form = getReadBulletinForm();
        doReturn(form).when(bulletinService)
                .read(2L);

        doReturn(false).when(likeService)
                .isLike(2L, "닉네임");

        List<CommentForm> forms = new ArrayList<>();
        doReturn(forms).when(commentService)
                .readByBoardId(2L);

        //expected
        mockMvc.perform(get("/bulletins/{id}", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("bulletins/detail"));
    }

    @Test
    @DisplayName("게시글 좋아요 버튼 클릭")
    void clickLikeButton() throws Exception {
        //given
        doNothing().when(likeService)
                .clickLike(2L, "닉네임");

        //expected
        mockMvc.perform(get("/bulletins/{id}/like", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/bulletins/" + 2));
    }

    @Test
    @DisplayName("게시글 수정 양식 호출 성공")
    void getEditForm() throws Exception {
        //given
        EditBulletinForm form = getBeforeEditForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(bulletinService)
                .getEditForm(2L);

        //expected
        mockMvc.perform(get("/bulletins/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("bulletins/edit"));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void edit() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(bulletinService)
                .update(any(EditBulletinForm.class));

        //expected
        mockMvc.perform(post("/bulletins/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(REGION, "지역")
                )
                .andExpect(redirectedUrl("/bulletins/" + 2));
    }

    @Test
    @DisplayName("검증으로 인한 게시글 수정 실패")
    void failEditByValidate() throws Exception {
        //given
        EditBulletinForm form = getBeforeEditForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(bulletinService)
                .getEditForm(2L);

        //expected
        mockMvc.perform(post("/bulletins/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(TITLE, "")
                        .param(CONTENT, "")
                        .param(REGION, "")
                )
                .andExpect(view().name("bulletins/edit"));
    }

    @Test
    @DisplayName("게시글 삭제 양식 호출")
    void getDeleteForm() throws Exception {
        //given
        ReadBulletinForm form = getReadBulletinForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(bulletinService)
                .read(2L);

        //expected
        mockMvc.perform(get("/bulletins/{id}/delete", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("bulletins/delete"));
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void delete() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(bulletinService)
                .delete(2L);

        //expected
        mockMvc.perform(post("/bulletins/{id}/delete", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/bulletins?page=0"));
    }

    @Test
    @DisplayName("작성자 외 접근으로 요청 실패")
    void failRequestWriterDiscord() throws Exception {
        //given
        doReturn("작성자").when(boardService)
                .getWriter(2L);

        //expected
        mockMvc.perform(get("/bulletins/{id}/edit", 2)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "다른사용자")
                )
                .andExpect(redirectedUrl("/"));
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

    private ReadBulletinForm getReadBulletinForm() {
        return ReadBulletinForm.builder()
                .board(getBulletin("제목"))
                .imageForms(List.of())
                .likeSize(1)
                .build();
    }

    private EditBulletinForm getBeforeEditForm() {
        Bulletin board = getBulletin("제목");
        List<ReadImageForm> oldImages = new ArrayList<>();
        EditBulletinForm form = new EditBulletinForm(board, oldImages);
        return form;
    }
}