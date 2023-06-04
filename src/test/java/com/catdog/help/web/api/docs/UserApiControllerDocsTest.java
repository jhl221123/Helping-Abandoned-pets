package com.catdog.help.web.api.docs;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.api.request.user.LoginRequest;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.catdog.help.MyConst.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                                fieldWithPath("emailId").description("이메일")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, EMAIL))),
                                fieldWithPath("password").description("비밀번호")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, PASSWORD))),
                                fieldWithPath("name").description("이름")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, NAME))),
                                fieldWithPath("nickname").description("닉네임")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, NICKNAME))),
                                fieldWithPath("age").description("나이")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, AGE))),
                                fieldWithPath("gender").description("성별")
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
        User user = User.builder()
                .emailId("test@test.test")
                .password("12341234")
                .nickname("닉네임")
                .name("이름")
                .age(22)
                .gender(Gender.WOMAN)
                .build();
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
                .andExpect(status().isOk())
                .andDo(document("users-login",
                        requestFields(
                                fieldWithPath("emailId").description("이메일")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, EMAIL))),
                                fieldWithPath("password").description("비밀번호")
                                        .attributes(key("constraints").value(getConstraintDescription(SaveUserRequest.class, PASSWORD)))
                        ),
                        responseFields(
                                fieldWithPath("redirectURL").description("로그인 하지 않고 접근했던 경로")
                        )
                ));
    }

    private List<String> getConstraintDescription(Class target, String field) {
        ConstraintDescriptions simpleRequestConstraints = new ConstraintDescriptions(target);
        return simpleRequestConstraints.descriptionsForProperty(field);
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
}
