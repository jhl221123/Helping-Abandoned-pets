package com.catdog.help.service;

import com.catdog.help.MyConst;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.EditUserForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    private LostService lostService;

    @Mock
    private BulletinService bulletinService;

    @Mock
    private ItemService itemService;

    @Mock
    private InquiryService inquiryService;


    @Test
    @DisplayName("회원가입")
    void join() {
        //given
        SaveUserForm form = getSaveUserForm("test@test.test", "닉네임");
        User user = getUser("test@test.test", "닉네임");

        doReturn(user).when(userRepository)
                .save(any(User.class));

        //when
        Long id = userService.join(form);

        //then
        assertThat(user.getId()).isEqualTo(id);

        //verify
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 아이디 중복 확인")
    void checkEmailDuplicate() {
        //given
        User user = getUser("duplicate@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByEmailId("duplicate@test.test");

        //when
        boolean result = userService.isEmailDuplication("duplicate@test.test");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임 중복 확인")
    void checkNickName() {
        //given
        User user = getUser("test@test.test", "중복닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("중복닉네임");

        //when
        boolean result = userService.isNicknameDuplication("중복닉네임");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("로그인")
    void login() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByEmailId("test@test.test");

        //when
        String nickname = userService.login("test@test.test", "12345678");//정상 로그인

        //then
        assertThat(nickname).isEqualTo("닉네임");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 실패")
    void failLoginByEmail() {
        //given
        doReturn(Optional.empty()).when(userRepository)
                .findByEmailId("nonexistent@test.test");

        //when
        String nickname = userService.login("nonexistent@test.test", "12345678");

        //then
        assertThat(nickname).isEqualTo(MyConst.FAIL_LOGIN);
    }

    @Test
    @DisplayName("비밀번호 불일치로 로그인 실패")
    void failLoginByPassword() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByEmailId("test@test.test");

        //when
        String nickname = userService.login("test@test.test", "12345678999");

        //then
        assertThat(nickname).isEqualTo(MyConst.FAIL_LOGIN);
    }

    @Test
    @DisplayName("관리자인 경우 성공")
    void isManager() {
        //given
        User user = getUser("test@test.test", "닉네임");
        user.makeManager();

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("매니저");

        //when
        Boolean result = userService.isManager("매니저");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("관리자가 아닌 경우 실패")
    void isNotManager() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("일반사용자");

        //when
        Boolean result = userService.isManager("일반사용자");

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("ReadUserForm 조회")
    void readByNickname() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        doReturn(2L).when(lostService)
                .countByNickname(user.getNickname());

        doReturn(2L).when(bulletinService)
                .countByNickname(user.getNickname());

        doReturn(2L).when(itemService)
                .countByNickname(user.getNickname());

        doReturn(2L).when(inquiryService)
                .countByNickname(user.getNickname());

        doReturn(2L).when(bulletinService)
                .countLikeBulletin(user.getNickname());

        doReturn(2L).when(itemService)
                .countLikeItem(user.getNickname());

        //when
        ReadUserForm readForm = userService.readByNickname("닉네임");

        //then
        assertThat(readForm.getEmailId()).isEqualTo(user.getEmailId());
    }

    @Test
    @DisplayName("UpdateUserForm 호출")
    void getUpdateForm() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        //when
        EditUserForm updateForm = userService.getUpdateForm("닉네임");

        //then
        assertThat(updateForm.getName()).isEqualTo("이름");
    }

    @Test
    @DisplayName("개인정보 변경")
    void updateInfo() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        EditUserForm form = new EditUserForm(
                User.builder()
                        .nickname("닉네임")
                        .name("이름변경")
                        .age(20)
                        .gender(Gender.MAN)
                        .build()
        );

        //when
        Long id = userService.updateUserInfo(form);

        //then
        assertThat(user.getName()).isEqualTo("이름변경");
    }

    @Test
    @DisplayName("비밀번호 확인")
    void validPassword() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        //when
        Boolean result = userService.isSamePassword("12345678", "닉네임");

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        //when
        userService.changePassword("87654321", "닉네임");

        //then
        assertThat(user.getPassword()).isEqualTo("87654321");
    }

    @Test
    @DisplayName("회원 탈퇴")
    void delete() {
        //given
        User user = getUser("test@test.test", "닉네임");

        doReturn(Optional.ofNullable(user)).when(userRepository)
                .findByNickname("닉네임");

        //expected
        userService.deleteUser("닉네임");
        verify(userRepository, times(1)).findByNickname("닉네임");
    }

    @Test
    @DisplayName("존재하지 않는 닉네임으로 사용자 조회 시 예외 발생")
    void notFoundUserExceptionByNickname() {
        //given
        doReturn(Optional.empty()).when(userRepository)
                .findByNickname("존재하지않는닉네임");

        //expected
        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.readByNickname("존재하지않는닉네임"));
    }


    private User getUser(String emailId, String nickname) {
        return User.builder()
                .emailId(emailId)
                .password("12345678")
                .nickname(nickname)
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }

    private SaveUserForm getSaveUserForm(String emailId, String nickname) {
        return SaveUserForm.builder()
                .emailId(emailId)
                .password("12345678")
                .nickname(nickname)
                .name("이름")
                .age(20)
                .gender(Gender.MAN)
                .build();
    }
}