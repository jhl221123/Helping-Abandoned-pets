package com.catdog.help.web.api.controller;

import com.catdog.help.exception.EmailDuplicateException;
import com.catdog.help.exception.NicknameDuplicateException;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.api.request.user.EditUserRequest;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.user.ReadUserResponse;
import com.catdog.help.web.api.response.user.SaveUserResponse;
import com.catdog.help.web.form.user.EditUserForm;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/detail")
    public ReadUserResponse read(@RequestParam String nickname) {
        ReadUserForm form = userService.readByNickname(nickname);
        return new ReadUserResponse(form);
    }

    @PostMapping("/edit")
    public void edit(@RequestBody @Validated EditUserRequest request) {
        userService.updateUserInfo(new EditUserForm(request));
    }
}
