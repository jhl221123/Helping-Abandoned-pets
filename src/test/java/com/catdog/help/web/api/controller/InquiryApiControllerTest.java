package com.catdog.help.web.api.controller;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.InquiryService;
import com.catdog.help.web.api.request.inquiry.SaveInquiryRequest;
import com.catdog.help.web.api.response.inquiry.ReadInquiryResponse;
import com.catdog.help.web.api.response.inquiry.SaveInquiryResponse;
import com.catdog.help.web.form.inquiry.ReadInquiryForm;
import com.catdog.help.web.form.inquiry.SaveInquiryForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import static com.catdog.help.domain.board.SecretStatus.OPEN;
import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class InquiryApiControllerTest {

    @InjectMocks
    private InquiryApiController inquiryApiController;

    @Mock
    private InquiryService inquiryService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(inquiryApiController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        objectMapper = new ObjectMapper();
    }


    @Test
    @DisplayName("문의글 저장 성공")
    void saveInquiryBoard() throws Exception {
        //given
        SaveInquiryRequest request = getSaveInquiryRequest();
        String json = objectMapper.writeValueAsString(request);

        SaveInquiryResponse response = new SaveInquiryResponse(2L);
        String result = objectMapper.writeValueAsString(response);

        doReturn(2L).when(inquiryService)
                .save(any(SaveInquiryForm.class), eq("닉네임"));

        //when
        mockMvc.perform(post("/api/inquiries/new")
                        .contentType(APPLICATION_JSON)
                        .sessionAttr(LOGIN_USER, "닉네임")
                        .content(json)
                )
                .andExpect(content().json(result));
    }

    @Test
    @DisplayName("문의글 조회 성공")
    void readInquiryBoard() throws Exception {
        //given
        ReadInquiryForm form = getReadInquiryForm();
        ReadInquiryResponse response = new ReadInquiryResponse(form);

        String result = objectMapper
                .registerModule(new JavaTimeModule())
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(response);

        doReturn(form).when(inquiryService)
                .read(2L);

        //expected
        mockMvc.perform(get("/api/inquiries/{id}", 2L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(content().json(result));
    }


    private ReadInquiryForm getReadInquiryForm() {
        Inquiry inquiry = getInquiry();
        return new ReadInquiryForm(inquiry);
    }

    private SaveInquiryRequest getSaveInquiryRequest() {
        return SaveInquiryRequest.builder()
                .title("제목")
                .content("내용")
                .secret(OPEN)
                .build();
    }

    private Inquiry getInquiry() {
        return Inquiry.builder()
                .user(getUser())
                .title("제목")
                .content("내용")
                .secret(OPEN)
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