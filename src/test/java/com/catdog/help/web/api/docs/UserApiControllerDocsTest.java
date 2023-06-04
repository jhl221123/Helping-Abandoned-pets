package com.catdog.help.web.api.docs;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.api.request.user.LoginRequest;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.user.ReadUserResponse;
import com.catdog.help.web.form.user.ReadUserForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.catdog.help.MyConst.*;
import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.catdolog.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class UserApiControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("Docs 회원가입 성공")
    void successJoin() throws Exception {
        //given
        SaveUserRequest request = getSaveUserRequest();
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/api/users/new")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(document("users-new",
                        requestFields(
                                fieldWithPath(EMAIL).description("이메일")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, EMAIL))),
                                fieldWithPath(PASSWORD).description("비밀번호")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, PASSWORD))),
                                fieldWithPath(NAME).description("이름")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, NAME))),
                                fieldWithPath(NICKNAME).description("닉네임")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, NICKNAME))),
                                fieldWithPath(AGE).description("나이")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, AGE))),
                                fieldWithPath(GENDER).description("성별")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, GENDER)))
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 아이디")
                        )
                ));
    }

    @Test
    @DisplayName("Docs 로그인 성공")
    void successLogin() throws Exception {
        //given
        User user = getUser();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .emailId("test@test.test")
                .password("12341234")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/api/users/login")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(request().sessionAttribute(LOGIN_USER, "닉네임"))
                .andDo(document("users-login",
                        requestFields(
                                fieldWithPath(EMAIL).description("이메일")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, EMAIL))),
                                fieldWithPath(PASSWORD).description("비밀번호")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, PASSWORD)))
                        ),
                        responseFields(
                                fieldWithPath("redirectURL").description("로그인 하지 않고 접근했던 경로")
                        )
                ));
    }

    @Test
    @DisplayName("Docs 로그아웃 성공")
    void successLogout() throws Exception {
        //given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_USER, "닉네임");

        //expected
        mockMvc.perform(get("/api/users/logout")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .session(session)
                )
                .andExpect(request().sessionAttributeDoesNotExist(LOGIN_USER))
                .andDo(document("users-logout"));
    }

    @Test
    @DisplayName("Docs 내 정보 조회")
    void readLoginUser() throws Exception {
        //given
        User user = getUser();
        userRepository.save(user);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_USER, "닉네임");

        ReadUserResponse response = getReadUserResponse(user);
        String result = objectMapper
                .registerModule(new JavaTimeModule()) //LocalDateTime 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(response);

        //expected
        mockMvc.perform(get("/api/users/detail")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Set-Cookie", session.getId())
                        .session(session)
                )
                .andExpect(content().json(result))
                .andDo(document("users-Info",
                        requestHeaders(headerWithName("Set-Cookie").description("사용자 인증 쿠키")),
                        responseFields(
                                fieldWithPath(ID).description("사용자 식별자"),
                                fieldWithPath(EMAIL).description("이메일"),
                                fieldWithPath(NAME).description("이름"),
                                fieldWithPath(NICKNAME).description("닉네임"),
                                fieldWithPath(AGE).description("나이"),
                                fieldWithPath(GENDER).description("성별"),
                                fieldWithPath(CREATED_DATE).description("가입날짜"),
                                fieldWithPath(LOST_SIZE).description("작성한 실종글 수"),
                                fieldWithPath(BULLETIN_SIZE).description("작성한 게시글 수"),
                                fieldWithPath(ITEM_SIZE).description("작성한 나눔글 수"),
                                fieldWithPath(INQUIRY_SIZE).description("작성한 문의글 수"),
                                fieldWithPath(LIKE_BULLETIN_SIZE).description("좋아요 누른 게시글 수"),
                                fieldWithPath(LIKE_ITEM_SIZE).description("좋아요 누른 나눔글 수")
                        )
                ));
    }


    private ReadUserResponse getReadUserResponse(User user) {
        return ReadUserResponse.builder()
                .form(new ReadUserForm(user))
                .lostSize(0L)
                .bulletinSize(0L)
                .itemSize(0L)
                .inquirySize(0L)
                .likeBulletinSize(0L)
                .likeItemSize(0L)
                .build();
    }

    private SaveUserRequest getSaveUserRequest() {
        return SaveUserRequest.builder()
                .emailId("test@test.test")
                .password("12341234")
                .name("이름")
                .nickname("닉네임")
                .age(22)
                .gender(Gender.MAN)
                .build();
    }

    private List<String> getConstraintDescription(Class target, String field) {
        ConstraintDescriptions simpleRequestConstraints = new ConstraintDescriptions(target);
        return simpleRequestConstraints.descriptionsForProperty(field);
    }

    private User getUser() {
        return User.builder()
                .emailId("test@test.test")
                .password("12341234")
                .nickname("닉네임")
                .name("이름")
                .age(22)
                .gender(Gender.WOMAN)
                .build();
    }
}
