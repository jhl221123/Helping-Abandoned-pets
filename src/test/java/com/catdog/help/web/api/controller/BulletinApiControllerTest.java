package com.catdog.help.web.api.controller;

import com.catdog.help.service.BulletinService;
import com.catdog.help.web.api.response.SaveBulletinResponse;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
        mockMvc = MockMvcBuilders.standaloneSetup(bulletinApiController).build();
        objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("게시글 저장 후 id 반환 성공")
    void saveBulletinAndReturnId() throws Exception {
        //given
        SaveBulletinForm form = getSaveBulletinForm();
        String json = objectMapper.writeValueAsString(form);

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

    private SaveBulletinResponse getSaveBulletinResponse() {
        return SaveBulletinResponse.builder()
                .id(2L)
                .build();
    }

    private SaveBulletinForm getSaveBulletinForm() {
        return SaveBulletinForm.builder()
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
    }
}