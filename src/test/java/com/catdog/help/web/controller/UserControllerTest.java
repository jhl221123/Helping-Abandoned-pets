package com.catdog.help.web.controller;

import com.catdog.help.MyConst;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.service.*;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.EditUserForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class UserControllerTest {
    private static final String EMAIL = "emailId";
    private static final String PASSWORD = "password";
    private static final String NICKNAME = "nickname";
    private static final String NAME = "name";
    private static final String AGE = "age";
    private static final String GENDER = "gender";
    private static final String BEFORE_PASSWORD = "beforePassword";
    private static final String AFTER_PASSWORD = "afterPassword";
    private static final String CHECK_PASSWORD = "checkPassword";
    private static final String PAGE = "page";

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private LostService lostService;

    @Mock
    private BulletinService bulletinService;

    @Mock
    private ItemService itemService;

    @Mock
    private InquiryService inquiryService;

    private MockMvc mockMvc;


    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }


    @Test
    @DisplayName("회원가입 양식 요청")
    void joinForm() throws Exception {
        //expected
        mockMvc.perform(get("/users/new").contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(view().name("users/joinForm"));
    }

    @Test
    @DisplayName("회원가입 성공")
    void join() throws Exception {
        //given
        doReturn(1L).when(userService)
                .join(any(SaveUserForm.class));

        //expected
        mockMvc.perform(post("/users/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(EMAIL, "test@test.test")
                        .param(PASSWORD, "12345678")
                        .param(NICKNAME, "닉네임")
                        .param(NAME, "이름")
                        .param(AGE, String.valueOf(20))
                        .param(GENDER, String.valueOf(Gender.MAN))
                )
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("검증으로 인한 회원가입 실패")
    void failJoinByValidate() throws Exception {
        //expected
        mockMvc.perform(post("/users/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(EMAIL, "")
                        .param(PASSWORD, "")
                        .param(NICKNAME, "")
                        .param(NAME, "")
                        .param(AGE, "")
                        .param(GENDER, "")
                )
                .andExpect(model().attributeHasErrors("saveForm"))
                .andExpect(view().name("users/joinForm"));
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패")
    void failJoinByEmailDuplicate() throws Exception {
        //given
        doReturn(true).when(userService)
                .isEmailDuplication("test@test.test");

        //expected
        mockMvc.perform(post("/users/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(EMAIL, "test@test.test")
                        .param(PASSWORD, "12345678")
                        .param(NICKNAME, "닉네임")
                        .param(NAME, "이름")
                        .param(AGE, String.valueOf(20))
                        .param(GENDER, String.valueOf(Gender.MAN))
                )
                .andExpect(model().attributeHasFieldErrors("saveForm", "emailId"))
                .andExpect(view().name("users/joinForm"));
    }

    @Test
    @DisplayName("닉네임 중복으로 회원가입 실패")
    void failJoinByNicknameDuplicate() throws Exception {
        //given
        doReturn(true).when(userService)
                .isNicknameDuplication("중복닉네임");

        //expected
        mockMvc.perform(post("/users/new")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(EMAIL, "test@test.test")
                        .param(PASSWORD, "12345678")
                        .param(NICKNAME, "중복닉네임")
                        .param(NAME, "이름")
                        .param(AGE, String.valueOf(20))
                        .param(GENDER, String.valueOf(Gender.MAN))

                )
                .andExpect(model().attributeHasFieldErrors("saveForm", "nickname"))
                .andExpect(view().name("users/joinForm"));
    }

    @Test
    @DisplayName("로그인 양식 요청")
    void loginForm() throws Exception {
        //expected
        mockMvc.perform(get("/users/login")
                        .contentType(APPLICATION_FORM_URLENCODED)
                )
                .andExpect(view().name("users/loginForm"));
    }

    @Test
    @DisplayName("로그인 성공")
    void login() throws Exception {
        //given
        doReturn("닉네임").when(userService)
                .login("test@test.test", "12345678");

        //expected
        mockMvc.perform(post("/users/login")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(EMAIL, "test@test.test")
                        .param(PASSWORD, "12345678")
                )
                .andExpect(request().sessionAttribute(SessionConst.LOGIN_USER, "닉네임"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("검증으로 인한 로그인 실패")
    void failLoginByEmail() throws Exception {
        //expected
        mockMvc.perform(post("/users/login")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(EMAIL, "")
                        .param(PASSWORD, "")
                )
                .andExpect(model().attributeHasErrors("loginForm"))
                .andExpect(view().name("users/loginForm"));
    }

    @Test
    @DisplayName("존재하지 않는 아이디 또는 비밀번호 불일치로 로그인 실패")
    void failLoginByNonexistentEmailOrWrongPassword() throws Exception {
        //given
        doReturn(MyConst.FAIL_LOGIN).when(userService)
                .login("nonexistent@test.test", "12345678");

        //expected
        mockMvc.perform(post("/users/login")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(EMAIL, "nonexistent@test.test")
                        .param(PASSWORD, "12345678")
                )
                .andExpect(model().attributeHasErrors("loginForm"))
                .andExpect(view().name("users/loginForm"));
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        //expected
        mockMvc.perform(get("/users/logout")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(request().sessionAttributeDoesNotExist(SessionConst.LOGIN_USER))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("내 정보 조회")
    void detail() throws Exception {
        //given
        User user = User.builder().build();
        ReadUserForm readForm = new ReadUserForm(user);

        doReturn(readForm).when(userService)
                .readByNickname("닉네임");

        //expected
        mockMvc.perform(get("/users/detail")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(model().attributeExists("readForm"))
                .andExpect(view().name("users/detail"));
    }

    @Test
    @DisplayName("로그인한 사용자가 작성한 실종글 모두 조회")
    void getMyLostPage() throws Exception {
        //given
        Page page = Mockito.mock(Page.class);
        doReturn(page).when(lostService)
                .getPageByNickname(eq("닉네임"), any(Pageable.class));

        //expected
        mockMvc.perform(get("/users/detail/lost")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(PAGE, String.valueOf(0))
                )
                .andExpect(view().name("users/lostList"));
    }

    @Test
    @DisplayName("로그인한 사용자가 작성한 게시글 모두 조회")
    void getMyBulletinPage() throws Exception {
        //given
        Page page = Mockito.mock(Page.class);
        doReturn(page).when(bulletinService)
                .getPageByNickname(eq("닉네임"), any(Pageable.class));

        //expected
        mockMvc.perform(get("/users/detail/bulletins")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(PAGE, String.valueOf(0))
                )
                .andExpect(view().name("users/bulletinList"));
    }

    @Test
    @DisplayName("로그인한 사용자가 작성한 나눔글 모두 조회")
    void getMyItemPage() throws Exception {
        //given
        Page page = Mockito.mock(Page.class);
        doReturn(page).when(itemService)
                .getPageByNickname(eq("닉네임"), any(Pageable.class));

        //expected
        mockMvc.perform(get("/users/detail/items")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(PAGE, String.valueOf(0))
                )
                .andExpect(view().name("users/itemList"));
    }


    @Test
    @DisplayName("로그인한 사용자가 작성한 문의글 모두 조회")
    void getMyInquiryPage() throws Exception {
        //given
        Page page = Mockito.mock(Page.class);
        doReturn(page).when(inquiryService)
                .getPageByNickname(eq("닉네임"), any(Pageable.class));

        //expected
        mockMvc.perform(get("/users/detail/inquiries")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(PAGE, String.valueOf(0))
                )
                .andExpect(view().name("users/inquiryList"));
    }

    @Test
    @DisplayName("로그인한 사용자가 좋아하는 게시글 모두 조회")
    void getLikeBulletinPage() throws Exception {
        //given
        Page page = Mockito.mock(Page.class);

        doReturn(page).when(bulletinService)
                .getLikeBulletins(eq("닉네임"), any(Pageable.class));

        //expected
        mockMvc.perform(get("/users/detail/likes/bulletins")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(PAGE, String.valueOf(0))
                )
                .andExpect(view().name("users/likeBulletinList"));
    }

    @Test
    @DisplayName("로그인한 사용자가 좋아하는 나눔글 모두 조회")
    void getLikeItemPage() throws  Exception {
        //given
        Page page = Mockito.mock(Page.class);

        doReturn(page).when(itemService)
                .getLikeItems(eq("닉네임"), any(Pageable.class));

        //expected
        mockMvc.perform(get("/users/detail/likes/items")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                        .param(PAGE, String.valueOf(0))
                )
                .andExpect(view().name("users/likeItemList"));
    }

    @Test
    @DisplayName("개인정보 수정 양식 호출")
    void editForm() throws Exception {
        //given
        doReturn(new EditUserForm()).when(userService)
                .getUpdateForm("닉네임");

        //expected
        mockMvc.perform(get("/users/detail/edit")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(model().attributeExists("updateForm"))
                .andExpect(view().name("users/edit"));
    }

    @Test
    @DisplayName("개인정보 수정 성공")
    void edit() throws Exception {
        //given
        doReturn(1L).when(userService)
                .updateUserInfo(any(EditUserForm.class));

        //expected
        mockMvc.perform(post("/users/detail/edit")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(NICKNAME, "닉네임")
                        .param(NAME, "이름변경")
                        .param(AGE, String.valueOf(30))
                        .param(GENDER, String.valueOf(Gender.WOMAN))
                )
                .andExpect(redirectedUrl("/users/detail"));
    }

    @Test
    @DisplayName("검증으로 인한 개인정보 수정 실패")
    void failEditByName() throws Exception {
        //expected
        mockMvc.perform(post("/users/detail/edit")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(NICKNAME, "닉네임")
                        .param(NAME, "")
                        .param(AGE, "")
                        .param(GENDER, "")
                )
                .andExpect(model().attributeHasErrors("updateForm"))
                .andExpect(view().name("users/edit"));
    }

    @Test
    @DisplayName("비밀번호 변경 양식 호출")
    void changePasswordForm() throws Exception {
        //expected
        mockMvc.perform(get("/users/detail/edit/password")
                        .contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(model().attributeExists("changePasswordForm"))
                .andExpect(view().name("users/editPassword"));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword() throws Exception {
        //given
        doReturn(true).when(userService)
                .isSamePassword("before123", "닉네임");

        doReturn(1L).when(userService)
                .changePassword("after123", "닉네임");

        //expected
        mockMvc.perform(post("/users/detail/edit/password")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(BEFORE_PASSWORD, "before123")
                        .param(AFTER_PASSWORD, "after123")
                        .param(CHECK_PASSWORD, "after123")
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/users/detail"));
    }

    @Test
    @DisplayName("검증으로 인한 비밀번호 변경 실패")
    void failChangePasswordByValidate() throws Exception {
        //expected
        mockMvc.perform(post("/users/detail/edit/password")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(BEFORE_PASSWORD, "")
                        .param(AFTER_PASSWORD, "")
                        .param(CHECK_PASSWORD, "")
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(model().attributeHasErrors("changePasswordForm"))
                .andExpect(view().name("users/editPassword"));
    }

    @Test
    @DisplayName("변경 비밀번호의 불일치로 비밀번호 변경 실패")
    void failChangePasswordByAfterPassword() throws Exception {
        //expected
        mockMvc.perform(post("/users/detail/edit/password")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(BEFORE_PASSWORD, "before123")
                        .param(AFTER_PASSWORD, "after123")
                        .param(CHECK_PASSWORD, "mismatchPw")
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(model().attributeHasErrors("changePasswordForm"))
                .andExpect(view().name("users/editPassword"));
    }

    @Test
    @DisplayName("기존 비밀번호 불일치로 비밀번호 변경 실패")
    void failChangePasswordByMismatchPassword() throws Exception {
        //given
        doReturn(false).when(userService)
                .isSamePassword("before123", "닉네임");

        //expected
        mockMvc.perform(post("/users/detail/edit/password")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .param(BEFORE_PASSWORD, "before123")
                        .param(AFTER_PASSWORD, "after123")
                        .param(CHECK_PASSWORD, "after123")
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(model().attributeHasErrors("changePasswordForm"))
                .andExpect(view().name("users/editPassword"));
    }

    @Test
    @DisplayName("회원 탈퇴")
    void delete() throws Exception {
        //expected
        mockMvc.perform(get("/users/detail/delete")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .sessionAttr(SessionConst.LOGIN_USER, "닉네임")
                )
                .andExpect(redirectedUrl("/users/logout"));
    }
}