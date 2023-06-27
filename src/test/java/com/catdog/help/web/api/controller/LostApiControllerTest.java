package com.catdog.help.web.api.controller;

import com.catdog.help.domain.board.Comment;
import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LostService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.api.Base64Image;
import com.catdog.help.web.api.request.lost.EditLostRequest;
import com.catdog.help.web.api.request.lost.SaveLostRequest;
import com.catdog.help.web.api.response.comment.CommentResponse;
import com.catdog.help.web.api.response.lost.PageLostResponse;
import com.catdog.help.web.api.response.lost.ReadLostResponse;
import com.catdog.help.web.api.response.lost.SaveLostResponse;
import com.catdog.help.web.controller.ViewUpdater;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.EditLostForm;
import com.catdog.help.web.form.lost.PageLostForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import com.catdog.help.web.form.search.LostSearch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LostApiControllerTest {

    @InjectMocks
    private LostApiController lostApiController;

    @Mock
    private LostService lostService;

    @Mock
    private BoardService boardService;

    @Mock
    private CommentService commentService;

    @Mock
    private ViewUpdater viewUpdater;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(lostApiController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("실종글 저장후 id 반환")
    void saveLostSuccess() throws Exception {
        //given
        Base64Image image = Base64Image.builder()
                .originalName("업로드파일이름")
                .base64File("base64file")
                .build();

        List<Base64Image> images = new ArrayList<>();
        images.add(image);


        SaveLostRequest request = SaveLostRequest.builder()
                .title("제목")
                .content("내용")
                .region("부산")
                .breed("실종동물 품종")
                .lostDate(LocalDate.now())
                .lostPlace("실종장소")
                .gratuity(10000)
                .images(images)
                .build();

        SaveLostResponse response = new SaveLostResponse(2L);

        String json = objectMapper
                .registerModule(new JavaTimeModule()) //LocalDateTime 직렬화
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(request);

        String result = objectMapper.writeValueAsString(response);

        doReturn(2L).when(lostService)
                .save(any(SaveLostForm.class), eq("닉네임"));

        //expect
        mockMvc.perform(post("/api/lost/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .content(json)
                )
                .andExpect(content().json(result));
    }

    @Test
    @DisplayName("실종글 페이지 조회 성공")
    void getPage() throws Exception {
        //given
        Page<PageLostForm> pageForms = new PageImpl<>(getPageLostForms(), PageRequest.of(0, 6), 0);
        Page<PageLostResponse> response = pageForms.map(PageLostResponse::new);

        String result = objectMapper
                .registerModule(new JavaTimeModule()) //LocalDateTime 직렬화
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(response);

        doReturn(pageForms).when(lostService)
                .search(any(LostSearch.class), any(Pageable.class));

        //expect
        mockMvc.perform(get("/api/lost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new MockCookie("JSESSIONID", "session_value"))
                        .param("region", "부산")
                )
                .andExpect(content().json(result));
    }

    @Test
    @DisplayName("실종글 단건 조회 성공")
    void readLost() throws Exception {
        //given
        ReadLostForm lostForm = new ReadLostForm(getLost(), getReadImageForms());
        List<CommentResponse> commentResponses = getCommentResponses(getCommentForm(getLost().getUser(), getLost()));
        ReadLostResponse response = getReadLostResponse(lostForm, commentResponses);

        String result = objectMapper
                .registerModule(new JavaTimeModule()) //LocalDateTime 직렬화
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(response);

        List<CommentForm> commentForms = getCommentForms(getCommentForm(getLost().getUser(), getLost()));

        doReturn(commentForms).when(commentService)
                .readByBoardId(2L);

        doReturn(lostForm).when(lostService)
                .read(2L);

        doNothing().when(viewUpdater)
                        .addView(eq(2L), any(HttpServletRequest.class), any(HttpServletResponse.class));

        //expect
        mockMvc.perform(get("/api/lost/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new MockCookie("JSESSIONID", "session_value"))
                )
                .andExpect(content().json(result));
    }

    @Test
    @DisplayName("실종글 수정 성공")
    void edit() throws Exception {
        //given
        EditLostRequest editRequest = getEditLostRequest();
        String request = objectMapper
                .registerModule(new JavaTimeModule())
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(editRequest);

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(lostService)
                .update(any(EditLostForm.class));

        //expected
        mockMvc.perform(post("/api/lost/{id}/edit", 2L)
                        .contentType(APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .content(request)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("실종글 삭제 성공")
    void deleteLostBoard() throws Exception {
        //given
        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        //expected
        mockMvc.perform(post("/api/lost/{id}/delete", 2L)
                        .contentType(APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("작성자 불일치로 실종글 삭제 실패 후 NotAuthorizedException 발생")
    void failDeleteLostBoardByNotAuthorizedException() throws Exception {
        //given
        doReturn("작성자").when(boardService)
                .getWriter(2L);

        //expected
        assertThrows(NestedServletException.class, // TODO: 2023-06-26 NotAuthorizedException 으로 수정
                () -> mockMvc.perform(post("/api/lost/{id}/delete", 2L)
                        .contentType(APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_USER, "다른 사용자")
                ));
    }


    private EditLostRequest getEditLostRequest() {
        return EditLostRequest.builder()
                .id(2L)
                .title("제목")
                .content("내용")
                .region("부산")
                .breed("품종")
                .lostDate(LocalDate.now())
                .lostPlace("실종장소")
                .gratuity(100000)
                .newLeadImage(getBase64Image())
                .newImages(getBase64Images())
                .deleteImageIds(List.of(5L, 6L))
                .build();
    }

    private List<Base64Image> getBase64Images() {
        Base64Image base64Image = getBase64Image();
        List<Base64Image> newImages = new ArrayList<>();
        newImages.add(base64Image);
        return newImages;
    }

    private Base64Image getBase64Image() {
        return Base64Image.builder()
                .originalName("uploadFileName")
                .base64File("base64File")
                .build();
    }

    private List<PageLostForm> getPageLostForms() {
        PageLostForm pageForm = new PageLostForm(getLost(), getReadImageForm());
        List<PageLostForm> forms = new ArrayList<>();
        forms.add(pageForm);
        return forms;
    }

    private ReadLostResponse getReadLostResponse(ReadLostForm lostForm, List<CommentResponse> commentResponses) {
        return ReadLostResponse.builder()
                .form(lostForm)
                .commentResponses(commentResponses)
                .build();
    }

    private List<CommentResponse> getCommentResponses(CommentForm commentForm) {
        CommentResponse commentResponse = new CommentResponse(commentForm);
        List<CommentResponse> commentResponses = new ArrayList<>();
        commentResponses.add(commentResponse);
        return commentResponses;
    }

    private List<CommentForm> getCommentForms(CommentForm commentForm) {
        List<CommentForm> commentForms = new ArrayList<>();
        commentForms.add(commentForm);
        return commentForms;
    }

    private CommentForm getCommentForm(User user, Lost board) {
        Comment comment = getComment(user, board);
        return new CommentForm(comment);
    }

    private Comment getComment(User user, Lost board) {
        return Comment.builder()
                .user(user)
                .board(board)
                .content("댓글내용")
                .build();
    }

    private List<ReadImageForm> getReadImageForms() {
        ReadImageForm imageForm = getReadImageForm();
        List<ReadImageForm> images = new ArrayList<>();
        images.add(imageForm);
        return images;
    }

    private ReadImageForm getReadImageForm() {
        UploadFile image = getUploadFile();
        return new ReadImageForm(image);
    }

    private  UploadFile getUploadFile() {
        return UploadFile.builder()
                .storeFileName("storeFileName")
                .uploadFileName("uploadFileName")
                .build();
    }

    private Lost getLost() {
        return Lost.builder()
                .user(getUser())
                .title("제목")
                .content("내용")
                .region("부산")
                .breed("말티즈")
                .lostDate(LocalDate.now())
                .lostPlace("실종장소")
                .gratuity(10000)
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