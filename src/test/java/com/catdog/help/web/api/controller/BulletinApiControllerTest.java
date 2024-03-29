package com.catdog.help.web.api.controller;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.api.Base64Image;
import com.catdog.help.web.api.request.bulletin.EditBulletinRequest;
import com.catdog.help.web.api.request.bulletin.SaveBulletinRequest;
import com.catdog.help.web.api.response.bulletin.PageBulletinResponse;
import com.catdog.help.web.api.response.bulletin.ReadBulletinResponse;
import com.catdog.help.web.api.response.bulletin.SaveBulletinResponse;
import com.catdog.help.web.controller.ViewUpdater;
import com.catdog.help.web.form.bulletin.EditBulletinForm;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.search.BulletinSearch;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BulletinApiControllerTest {

    @InjectMocks
    private BulletinApiController bulletinApiController;

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

    private ObjectMapper objectMapper;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(bulletinApiController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("게시글 저장 후 id 반환 성공")
    void saveBulletinAndReturnId() throws Exception {
        //given
        SaveBulletinRequest request = getSaveBulletinRequest();
        String json = objectMapper.writeValueAsString(request);

        SaveBulletinResponse response = getSaveBulletinResponse();
        String result = objectMapper.writeValueAsString(response);

        doReturn(2L).when(bulletinService)
                .save(any(SaveBulletinForm.class), eq("닉네임"));

        //expected
        mockMvc.perform(post("/api/bulletins/new")
                        .contentType(APPLICATION_JSON)
                        .sessionAttr(LOGIN_USER, "닉네임")
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.content().json(result));
    }

    @Test
    @DisplayName("검색 조건에 맞는 게시글 페이지 조회")
    void getPage() throws Exception {
        //given
        BulletinSearch search = BulletinSearch.builder()
                .region("부산")
                .title("검색제목")
                .build();
        String request = objectMapper.writeValueAsString(search);

        List<PageBulletinForm> forms = new ArrayList<>();
        Page<PageBulletinForm> pageBulletinForms = new PageImpl<>(forms, PageRequest.of(0, 10), 0);

        Page<PageBulletinResponse> response = getPageBulletinResponse(pageBulletinForms);
        String result = objectMapper.writeValueAsString(response);

        doReturn(pageBulletinForms).when(bulletinService)
                .search(any(BulletinSearch.class), any(Pageable.class));

        //expected
        mockMvc.perform(get("/api/bulletins")
                        .contentType(APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(content().json(result));
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

        ReadBulletinResponse response = ReadBulletinResponse.builder()
                .form(form)
                .checkLike(false)
                .commentForms(forms)
                .build();
        String result = objectMapper.writeValueAsString(response);

        //expected
        mockMvc.perform(get("/api/bulletins/{id}", 2L)
                        .contentType(APPLICATION_JSON)
                        .sessionAttr(LOGIN_USER, "닉네임")
                )
                .andExpect(content().json(result)).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("게시글의 좋아요 버튼 클릭")
    void clickLikeButton() throws Exception {
        //given
        doNothing().when(likeService)
                .clickLike(2L, "닉네임");

        //expected
        mockMvc.perform(post("/api/bulletins/{id}/like", 2L)
                        .contentType(APPLICATION_JSON)
                        .sessionAttr(LOGIN_USER, "닉네임")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void edit() throws Exception {
        //given
        EditBulletinRequest editRequest = getEditBulletinRequest();
        String request = objectMapper.writeValueAsString(editRequest);

        doReturn("닉네임").when(boardService)
                .getWriter(2L);

        doNothing().when(bulletinService)
                .update(any(EditBulletinForm.class));

        //expected
        mockMvc.perform(post("/api/bulletins/{id}/edit", 2L)
                        .contentType(APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk());
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
        mockMvc.perform(post("/api/bulletins/{id}/delete", 2L)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(status().isOk());
    }


    private Page<PageBulletinResponse> getPageBulletinResponse(Page<PageBulletinForm> pageBulletinForms) {
        return pageBulletinForms.map(PageBulletinResponse::new);
    }

    private SaveBulletinResponse getSaveBulletinResponse() {
        return new SaveBulletinResponse(2L);
    }

    private ReadBulletinForm getReadBulletinForm() {
        return ReadBulletinForm.builder()
                .board(getBulletin("제목"))
                .imageForms(List.of())
                .likeSize(1)
                .build();
    }

    private EditBulletinRequest getEditBulletinRequest() {

        return EditBulletinRequest.builder()
                .id(2L)
                .nickname("닉네임")
                .title("제목")
                .content("내용")
                .region("부산")
                .base64Images(getBase64Images())
                .deleteImageIds(List.of())
                .build();
    }

    private List<Base64Image> getBase64Images() {
        Base64Image base64Image = Base64Image.builder()
                .originalName("uploadFileName")
                .base64File("base64File")
                .build();
        List<Base64Image> newImages = new ArrayList<>();
        newImages.add(base64Image);
        return newImages;
    }

    private SaveBulletinRequest getSaveBulletinRequest() {
        return SaveBulletinRequest.builder()
                .title("제목")
                .content("내용")
                .region("지역")
                .base64Images(getBase64Images())
                .build();
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
}