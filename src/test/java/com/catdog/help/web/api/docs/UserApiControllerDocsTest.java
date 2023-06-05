package com.catdog.help.web.api.docs;

import com.catdog.help.domain.board.*;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.*;
import com.catdog.help.web.api.request.user.LoginRequest;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.bulletin.PageBulletinResponse;
import com.catdog.help.web.api.response.inquiry.PageInquiryResponse;
import com.catdog.help.web.api.response.item.PageItemResponse;
import com.catdog.help.web.api.response.lost.PageLostResponse;
import com.catdog.help.web.api.response.user.ReadUserResponse;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.item.PageItemForm;
import com.catdog.help.web.form.lost.PageLostForm;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.catdog.help.MyConst.*;
import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
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

    @Autowired
    private LostRepository lostRepository;

    @Autowired
    private BulletinRepository bulletinRepository;
    
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UploadFileRepository uploadFileRepository;

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
    void readLoginUserInfo() throws Exception {
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
                        requestHeaders(headerWithName("Set-Cookie").description("사용자 인증 쿠키(JSESSIONID={발급된 키})")),
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

    @Test
    @DisplayName("Docs 내가 작성한 실종글 목록 조회")
    void readMyLostPage() throws Exception {
        //given
        User user = getUser();
        userRepository.save(user);

        Lost board = getLost(user);
        lostRepository.save(board);

        UploadFile uploadFile = new UploadFile("uploadFileName", "storeFileName");
        uploadFile.addBoard(board);
        uploadFileRepository.save(uploadFile);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_USER, "닉네임");

        PageLostResponse response = getPageLostResponse(board, uploadFile);
        String result = objectMapper
                .registerModule(new JavaTimeModule()) //LocalDateTime 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(response);

        //expected
        mockMvc.perform(get("/api/users/detail/lost")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Set-Cookie", session.getId())
                        .session(session)
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(content().json(result))
                .andDo(document("users-lost",
                        requestHeaders(headerWithName("Set-Cookie").description("사용자 인증 쿠키(JSESSIONID={발급된 키})")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("조회 건수")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").description("실종글 식별자"),
                                fieldWithPath("content[].leadImage.id").description("대표이미지 식별자"),
                                fieldWithPath("content[].leadImage.uploadFileName").description("대표이미지 업로드 이름"),
                                fieldWithPath("content[].leadImage.storeFileName").description("대표이미지 저장 이름"),
                                fieldWithPath("content[].region").description("지역"),
                                fieldWithPath("content[].breed").description("품종"),
                                fieldWithPath("content[].lostDate").description("실종날짜"),
                                fieldWithPath("content[].lostPlace").description("실종장소"),
                                fieldWithPath("content[].gratuity").description("사례금"),

                                fieldWithPath("page").description("현재 페이지"),
                                fieldWithPath("size").description("조회 건수"),
                                fieldWithPath("totalElements").description("전체 조회 건수"),
                                fieldWithPath("totalPages").description("전체 페이지 수")
                        )
                ));
    }

    @Test
    @DisplayName("Docs 내가 작성한 게시글 목록 조회")
    void readMyBulletinPage() throws Exception {
        //given
        User user = getUser();
        userRepository.save(user);

        Bulletin board = Bulletin.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
        bulletinRepository.save(board);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_USER, "닉네임");

        PageBulletinResponse response = getPageBulletinResponse(board);
        String result = objectMapper
                .registerModule(new JavaTimeModule()) //LocalDateTime 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(response);

        //expected
        mockMvc.perform(get("/api/users/detail/bulletins")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Set-Cookie", session.getId())
                        .session(session)
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(content().json(result))
                .andDo(document("users-bulletins",
                        requestHeaders(headerWithName("Set-Cookie").description("사용자 인증 쿠키(JSESSIONID={발급된 키})")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("조회 건수")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").description("게시글 식별자"),
                                fieldWithPath("content[].title").description("제목"),
                                fieldWithPath("content[].nickname").description("작성자 닉네임"),
                                fieldWithPath("content[].createdDate").description("작성날짜"),
                                fieldWithPath("content[].views").description("조회수"),
                                fieldWithPath("content[].region").description("지역"),

                                fieldWithPath("page").description("현재 페이지"),
                                fieldWithPath("size").description("조회 건수"),
                                fieldWithPath("totalElements").description("전체 조회 건수"),
                                fieldWithPath("totalPages").description("전체 페이지 수")
                        )
                ));
    }

    @Test
    @DisplayName("Docs 내가 작성한 나눔글 목록 조회")
    void readMyItemPage() throws Exception {
        //given
        User user = getUser();
        userRepository.save(user);

        Item board = Item.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .itemName("제품명")
                .price(1000)
                .build();
        itemRepository.save(board);

        UploadFile uploadFile = new UploadFile("uploadFileName", "storeFileName");
        uploadFile.addBoard(board);
        uploadFileRepository.save(uploadFile);

        PageItemResponse response = getPageItemResponse(board, uploadFile);
        String result = objectMapper.writeValueAsString(response);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_USER, "닉네임");

        //expected
        mockMvc.perform(get("/api/users/detail/items")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Set-Cookie", session.getId())
                        .session(session)
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(content().json(result))
                .andDo(document("users-items",
                        requestHeaders(headerWithName("Set-Cookie").description("사용자 인증 쿠키(JSESSIONID={발급된 키})")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("조회 건수")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").description("나눔글 식별자"),
                                fieldWithPath("content[].itemName").description("제품명"),
                                fieldWithPath("content[].price").description("제품가격"),
                                fieldWithPath("content[].region").description("지역"),
                                fieldWithPath("content[].status").description("거래상태 STILL(거래중), COMP(거래완료)"),
                                fieldWithPath("content[].leadImage.id").description("대표이미지 식별자"),
                                fieldWithPath("content[].leadImage.uploadFileName").description("대표이미지 업로드 이름"),
                                fieldWithPath("content[].leadImage.storeFileName").description("대표이미지 저장 이름"),
                                fieldWithPath("content[].views").description("조회수"),
                                fieldWithPath("content[].likeSize").description("좋아요 수"),
                                fieldWithPath("content[].roomSize").description("쪽지 수"),

                                fieldWithPath("page").description("현재 페이지"),
                                fieldWithPath("size").description("조회 건수"),
                                fieldWithPath("totalElements").description("전체 조회 건수"),
                                fieldWithPath("totalPages").description("전체 페이지 수")
                        )
                ));
    }

    @Test
    @DisplayName("Docs 내가 작성한 문의글 목록 조회")
    void readMyInquiryPage() throws Exception {
        //given
        User user = getUser();
        userRepository.save(user);

        Inquiry board = Inquiry.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .secret(false)
                .build();
        inquiryRepository.save(board);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_USER, "닉네임");

        PageInquiryResponse response = getPageInquiryResponse(board);
        String result = objectMapper
                .registerModule(new JavaTimeModule()) //LocalDateTime 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(response);

        //expected
        mockMvc.perform(get("/api/users/detail/inquiries")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Set-Cookie", session.getId())
                        .session(session)
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(content().json(result))
                .andDo(document("users-inquiries",
                        requestHeaders(headerWithName("Set-Cookie").description("사용자 인증 쿠키(JSESSIONID={발급된 키})")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("조회 건수")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").description("문의글 식별자"),
                                fieldWithPath("content[].title").description("제목"),
                                fieldWithPath("content[].nickname").description("작성자 닉네임"),
                                fieldWithPath("content[].createdDate").description("작성날짜"),
                                fieldWithPath("content[].secret").description("비밀여부 false(공개글), true(비밀글)"),

                                fieldWithPath("page").description("현재 페이지"),
                                fieldWithPath("size").description("조회 건수"),
                                fieldWithPath("totalElements").description("전체 조회 건수"),
                                fieldWithPath("totalPages").description("전체 페이지 수")
                        )
                ));
    }

    @Test
    @DisplayName("Docs 내가 좋아요 누른 게시글 목록 조회")
    void readLikeBulletinPage() throws Exception {
        //given
        User user = getUser();
        userRepository.save(user);

        Bulletin board = Bulletin.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .region("지역")
                .build();
        bulletinRepository.save(board);

        Like like = Like.builder()
                .user(user)
                .board(board)
                .build();
        likeRepository.save(like);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LOGIN_USER, "닉네임");

        PageBulletinResponse response = getPageBulletinResponse(board);
        String result = objectMapper
                .registerModule(new JavaTimeModule()) //LocalDateTime 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(response);

        //expected
        mockMvc.perform(get("/api/users/detail/likes/bulletins")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Set-Cookie", session.getId())
                        .session(session)
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(content().json(result))
                .andDo(document("users-like-bulletins",
                        requestHeaders(headerWithName("Set-Cookie").description("사용자 인증 쿠키(JSESSIONID={발급된 키})")),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("조회 건수")
                        ),
                        responseFields(
                                fieldWithPath("content[].id").description("게시글 식별자"),
                                fieldWithPath("content[].title").description("제목"),
                                fieldWithPath("content[].nickname").description("작성자 닉네임"),
                                fieldWithPath("content[].createdDate").description("작성날짜"),
                                fieldWithPath("content[].views").description("조회수"),
                                fieldWithPath("content[].region").description("지역"),

                                fieldWithPath("page").description("현재 페이지"),
                                fieldWithPath("size").description("조회 건수"),
                                fieldWithPath("totalElements").description("전체 조회 건수"),
                                fieldWithPath("totalPages").description("전체 페이지 수")
                        )
                ));
    }


    private PageInquiryResponse getPageInquiryResponse(Inquiry board) {
        PageInquiryForm pageInquiryForm = new PageInquiryForm(board);
        List<PageInquiryForm> forms = new ArrayList<>();
        forms.add(pageInquiryForm);
        return PageInquiryResponse.builder()
                .content(forms)
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
    }

    private PageItemResponse getPageItemResponse(Item board, UploadFile uploadFile) {
        PageItemForm pageItemForm = new PageItemForm(board, new ReadImageForm(uploadFile));
        List<PageItemForm> forms = new ArrayList<>();
        forms.add(pageItemForm);
        return PageItemResponse.builder()
                .content(forms)
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
    }

    private PageBulletinResponse getPageBulletinResponse(Bulletin board) {
        PageBulletinForm pageBulletinForm = new PageBulletinForm(board);
        List<PageBulletinForm> forms = new ArrayList<>();
        forms.add(pageBulletinForm);
        return PageBulletinResponse.builder()
                .content(forms)
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
    }

    private PageLostResponse getPageLostResponse(Lost board, UploadFile uploadFile) {
        PageLostForm pageLostForm = new PageLostForm(board, new ReadImageForm(uploadFile));
        List<PageLostForm> forms = new ArrayList<>();
        forms.add(pageLostForm);
        return PageLostResponse.builder()
                .content(forms)
                .page(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
    }

    private Lost getLost(User user) {
        return Lost.builder()
                .user(user)
                .title("실종글 제목")
                .content("실종글 내용")
                .breed("품종")
                .lostDate(LocalDate.now())
                .lostPlace("실종된 장소")
                .gratuity(100000)
                .build();
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
