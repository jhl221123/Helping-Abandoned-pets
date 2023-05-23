package com.catdog.help.web.controller;

import com.catdog.help.service.*;
import com.catdog.help.web.SessionConst;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class LostControllerTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String REGION = "region";
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
}