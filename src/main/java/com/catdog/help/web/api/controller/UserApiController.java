package com.catdog.help.web.api.controller;

import com.catdog.help.exception.*;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.api.request.user.ChangePasswordRequest;
import com.catdog.help.web.api.request.user.EditUserRequest;
import com.catdog.help.web.api.request.user.LoginRequest;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.user.LoginResponse;
import com.catdog.help.web.api.response.user.ReadUserResponse;
import com.catdog.help.web.api.response.user.SaveUserResponse;
import com.catdog.help.web.form.user.EditUserForm;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.catdog.help.MyConst.FAIL_LOGIN;
import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserApiController {

    private final UserService userService;
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
        session.setAttribute(LOGIN_USER, nickname); // TODO: 2023-05-29 프론트에 뭘 줘야할 지.. 세션처리 어떻게 하지
        return new LoginResponse(redirectURL);
    }

    @GetMapping("/detail")
    public ReadUserResponse read(@RequestParam String nickname) {
        ReadUserForm form = userService.readByNickname(nickname);
        return new ReadUserResponse(form);
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
}
