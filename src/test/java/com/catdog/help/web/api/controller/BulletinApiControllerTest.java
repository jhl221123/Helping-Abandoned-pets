package com.catdog.help.web.api.controller;

import com.catdog.help.service.BulletinService;
import com.catdog.help.web.api.request.bulletin.SaveBulletinRequest;
import com.catdog.help.web.api.response.bulletin.PageBulletinResponse;
import com.catdog.help.web.api.response.bulletin.SaveBulletinResponse;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class BulletinApiControllerTest {

    @InjectMocks
    private BulletinApiController bulletinApiController;

    @Mock
    private BulletinService bulletinService;

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
                .save(any(SaveBulletinForm.class), eq(request.getNickname()));

        //expected
        mockMvc.perform(post("/api/bulletins/new")
                        .contentType(APPLICATION_JSON)
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

        PageBulletinResponse response = getPageBulletinResponse(pageBulletinForms);
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


    private PageBulletinResponse getPageBulletinResponse(Page<PageBulletinForm> pageBulletinForms) {
        return PageBulletinResponse.builder()
                .content(pageBulletinForms.getContent())
                .page(pageBulletinForms.getPageable().getPageNumber())
                .size(pageBulletinForms.getPageable().getPageSize())
                .totalElements(pageBulletinForms.getTotalElements())
                .totalPages(pageBulletinForms.getTotalPages())
                .build();
    }

    private SaveBulletinResponse getSaveBulletinResponse() {
        return new SaveBulletinResponse(2L);
    }

    private SaveBulletinRequest getSaveBulletinRequest() {
        return SaveBulletinRequest.builder()
                .nickname("닉네임")
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }
}