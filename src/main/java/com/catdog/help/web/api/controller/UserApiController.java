package com.catdog.help.web.api.controller;

import com.catdog.help.exception.*;
import com.catdog.help.service.*;
import com.catdog.help.web.api.request.user.ChangePasswordRequest;
import com.catdog.help.web.api.request.user.EditUserRequest;
import com.catdog.help.web.api.request.user.LoginRequest;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.bulletin.PageBulletinResponse;
import com.catdog.help.web.api.response.inquiry.PageInquiryResponse;
import com.catdog.help.web.api.response.item.PageItemResponse;
import com.catdog.help.web.api.response.lost.PageLostResponse;
import com.catdog.help.web.api.response.user.LoginResponse;
import com.catdog.help.web.api.response.user.ReadUserResponse;
import com.catdog.help.web.api.response.user.SaveUserResponse;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.item.PageItemForm;
import com.catdog.help.web.form.lost.PageLostForm;
import com.catdog.help.web.form.user.EditUserForm;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.catdog.help.MyConst.FAIL_LOGIN;
import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserApiController {

    private final UserService userService;
    private final LostService lostService;
    private final BulletinService bulletinService;
    private final ItemService itemService;
    private final InquiryService inquiryService;

    /***  create  ***/

    @PostMapping("/new")
    public SaveUserResponse join(@RequestBody @Validated SaveUserRequest request) {
        // TODO: 2023-04-20 각 프로퍼티에 특수기호 사용불가 검증 특히 _
        if (userService.isEmailDuplication(request.getEmailId())){
            throw new EmailDuplicateException();
        }

        if (userService.isNicknameDuplication(request.getNickname())) {
            throw new NicknameDuplicateException();
        }
        Long userId = userService.join(new SaveUserForm(request));
        return new SaveUserResponse(userId);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest, HttpServletRequest request,
                               @RequestParam(defaultValue = "/") String redirectURL) {
        String nickname = userService.login(loginRequest.getEmailId(), loginRequest.getPassword());

        if (nickname.equals(FAIL_LOGIN)) {
            throw new LoginFailureException();
        }

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, nickname);
        return new LoginResponse(redirectURL);
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @GetMapping("/detail")
    public ReadUserResponse read(@SessionAttribute(name = LOGIN_USER) String nickname) {
        ReadUserForm form = userService.readByNickname(nickname);

        return ReadUserResponse.builder()
                .form(form)
                .build();
    }

    @GetMapping("/detail/lost")
    public Page<PageLostResponse> getMyLostBoardPage(@SessionAttribute(name = LOGIN_USER) String nickname,
                                                     @PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        Page<PageLostForm> pageForms = lostService.getPageByNickname(nickname, pageable);
        return pageForms.map(PageLostResponse::new);
    }

    @GetMapping("/detail/bulletins")
    public Page<PageBulletinResponse> getMyBulletinPage(@SessionAttribute(name = LOGIN_USER) String nickname,
                                                        @PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        Page<PageBulletinForm> pageForms = bulletinService.getPageByNickname(nickname, pageable);
        return pageForms.map(PageBulletinResponse::new);
    }

    @GetMapping("/detail/items")
    public Page<PageItemResponse> getMyItemPage(@SessionAttribute(name = LOGIN_USER) String nickname,
                                                @PageableDefault(size = 12, sort = "id", direction = DESC) Pageable pageable) {
        Page<PageItemForm> pageForms = itemService.getPageByNickname(nickname, pageable);
        return pageForms.map(PageItemResponse::new);
    }

    @GetMapping("/detail/inquiries")
    public Page<PageInquiryResponse> getMyInquiryPage(@SessionAttribute(name = LOGIN_USER) String nickname,
                                                @PageableDefault(size = 5, sort = "id", direction = DESC) Pageable pageable) {
        Page<PageInquiryForm> pageForms = inquiryService.getPageByNickname(nickname, pageable);
        return pageForms.map(PageInquiryResponse::new);
    }

    @GetMapping("/detail/likes/bulletins")
    public Page<PageBulletinResponse> getLikeBulletinPage(@SessionAttribute(name = LOGIN_USER) String nickname,
                                                          @PageableDefault(sort = "board_id", direction = DESC) Pageable pageable) {
        Page<PageBulletinForm> pageForms = bulletinService.getLikeBulletins(nickname, pageable);
        return pageForms.map(PageBulletinResponse::new);
    }

    @GetMapping("/detail/likes/items")
    public Page<PageItemResponse> getLikeItemPage(@SessionAttribute(name = LOGIN_USER) String nickname,
                                            @PageableDefault(size = 12, sort = "board_id", direction = DESC) Pageable pageable) {
        Page<PageItemForm> pageForms = itemService.getLikeItems(nickname, pageable);
        return pageForms.map(PageItemResponse::new);
    }

    @PostMapping("/edit")
    public void edit(@RequestBody @Validated EditUserRequest request) {
        userService.updateUserInfo(new EditUserForm(request));
    }

    @PostMapping("/edit/password")
    public void editPassword(@RequestBody @Validated ChangePasswordRequest request) {
        Boolean isCorrect = userService.isSamePassword(request.getBeforePassword(), request.getNickname());
        if (!isCorrect) {
            //기존 비밀번호 불일치
            throw new PasswordIncorrectException();
        }
        if (!request.getAfterPassword().equals(request.getCheckPassword())) {
            //새 비밀번호 불일치
            throw new PasswordNotSameException();
        }
        userService.changePassword(request.getAfterPassword(), request.getNickname());
    }

    @PostMapping("/detail/delete")
    public void delete(@SessionAttribute(name = LOGIN_USER) String nickname) {
        userService.deleteUser(nickname);
    }
}
