package com.catdog.help.web.api.controller;

import com.catdog.help.service.LostService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.api.Base64Image;
import com.catdog.help.web.api.request.lost.SaveLostRequest;
import com.catdog.help.web.api.response.lost.SaveLostResponse;
import com.catdog.help.web.form.lost.SaveLostForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class LostApiControllerTest {

    @InjectMocks
    private LostApiController lostApiController;

    @Mock
    private LostService lostService;

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
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(request);

        String result = objectMapper.writeValueAsString(response);

        doReturn(2L).when(lostService)
                .save(any(SaveLostForm.class), eq("닉네임"));

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post("/api/lost/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.content().json(result));
    }
}