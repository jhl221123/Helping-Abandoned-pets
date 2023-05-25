package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.service.LostService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.lost.SaveLostForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

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
        doReturn(2L).when(lostService)
                .save(any(SaveLostForm.class), eq("닉네임"));

        //expected
        mockMvc.perform(post("/lost/new")
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
    @DisplayName("게시글 삭제 성공")
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
}