package com.catdog.help.web.controller;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.inquiry.EditInquiryForm;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class InquiryControllerTest {

    private static final String NICKNAME = "nickname";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String SECRET = "secret";
    private static final String PAGE = "page";
    private static final String SIZE = "size";

    @InjectMocks
    InquiryController inquiryController;

    @Mock
    InquiryService inquiryService;

    @Mock
    UserService userService;

    @Mock
    CommentService commentService;

    @Mock
    BoardService boardService;

    private MockMvc mockMvc;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(inquiryController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build(); // TODO: 2023-04-25 pageable 파라미터 바인딩 시켜주는 이칭구 블로그 글 작성 고려
    }


    @Test
    @DisplayName("문의글 작성 양식 호출")
    void getSaveForm() throws Exception {
        //expected
        mockMvc.perform(get("/inquiries/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("inquiries/create"));
    }

    @Test
    @DisplayName("문의글 작성 성공")
    void saveBoard() throws Exception {
        //given
        doReturn(2L).when(inquiryService)
                .save(any(SaveInquiryForm.class), eq("닉네임"));

        //expected
        mockMvc.perform(post("/inquiries/new")
                        .contentType(MULTIPART_FORM_DATA)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(NICKNAME, "닉네임")
                        .param(TITLE, "제목")
                        .param(CONTENT, "내용")
                        .param(SECRET, String.valueOf(false))
                )
                .andExpect(redirectedUrl("/inquiries/" + 2L));
    }

    @Test
    @DisplayName("검증으로 인해 문의글 작성 실패")
    void failSaveByValidate() throws Exception {
        //expected
        mockMvc.perform(post("/inquiries/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(NICKNAME, "")
                        .param(TITLE, "")
                        .param(CONTENT, "")
                        .param(SECRET, "")
                )
                .andExpect(view().name("inquiries/create"));
    }

    @Test
    @DisplayName("문의글 페이지 조회")
    void getPage() throws Exception {
        //given
        doReturn(false).when(userService)
                .isManager("닉네임");

        Page page = Mockito.mock(Page.class);
        doReturn(page).when(inquiryService)
                .getPage(any(Pageable.class));

        //expected
        mockMvc.perform(get("/inquiries")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(PAGE, String.valueOf(0))
                        .param(SIZE, String.valueOf(10))
                )
                .andExpect(view().name("inquiries/list"));
    }

    @Test
    @DisplayName("문의글 단건 조회")
    void readOne() throws Exception {
        //given
        ReadInquiryForm form = getReadInquiryForm();
        doReturn(form).when(inquiryService)
                .read(2L);

        List<CommentForm> forms = new ArrayList<>();
        doReturn(forms).when(commentService)
                .readByBoardId(2L);

        //expected
        mockMvc.perform(get("/inquiries/{id}", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("inquiries/detail"));
    }

    @Test
    @DisplayName("문의글 수정 양식 호출 성공")
    void getEditForm() throws Exception {
        //given
        EditInquiryForm form = getBeforeEditForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(inquiryService)
                .getEditForm(2L);

        //expected
        mockMvc.perform(get("/inquiries/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("inquiries/edit"));
    }

    @Test
    @DisplayName("문의글 수정 성공")
    void edit() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(inquiryService)
                .update(any(EditInquiryForm.class));

        //expected
        mockMvc.perform(post("/inquiries/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(NICKNAME, "닉네임")
                        .param(TITLE, "제목수정")
                        .param(CONTENT, "내용수정")
                        .param(SECRET, String.valueOf(true))
                )
                .andExpect(redirectedUrl("/inquiries/" + 2));
    }

    @Test
    @DisplayName("검증으로 인한 문의글 수정 실패")
    void failEditByValidate() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        //expected
        mockMvc.perform(post("/inquiries/{id}/edit", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(NICKNAME, "")
                        .param(TITLE, "")
                        .param(CONTENT, "")
                        .param(SECRET, "")
                )
                .andExpect(view().name("inquiries/edit"));
    }

    @Test
    @DisplayName("문의글 삭제 양식 호출")
    void getDeleteForm() throws Exception {
        //given
        ReadInquiryForm form = getReadInquiryForm();

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doReturn(form).when(inquiryService)
                .read(2L);

        //expected
        mockMvc.perform(get("/inquiries/{id}/delete", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(view().name("inquiries/delete"));
    }

    @Test
    @DisplayName("문의글 삭제 성공")
    void delete() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(inquiryService)
                .delete(2L);

        //expected
        mockMvc.perform(post("/inquiries/{id}/delete", 2L)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/inquiries?page=0"));
    }

    @Test
    @DisplayName("작성자 외 접근으로 요청 실패")
    void failRequestWriterDiscord() throws Exception {
        //given
        doReturn("작성자").when(boardService)
                .getWriter(2L);

        //expected
        mockMvc.perform(get("/inquiries/{id}/edit", 2)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "다른사용자")
                )
                .andExpect(redirectedUrl("/"));
    }


    private EditInquiryForm getBeforeEditForm() {
        Inquiry board = getInquiry("제목");
        EditInquiryForm form = new EditInquiryForm(board);
        return form;
    }

    private ReadInquiryForm getReadInquiryForm() {
        Inquiry board = getInquiry("제목");
        return new  ReadInquiryForm(board);
    }

    private Inquiry getInquiry(String title) {
        User user = getUser();
        return Inquiry.builder()
                .user(user)
                .title(title)
                .content("내용")
                .secret(false)
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