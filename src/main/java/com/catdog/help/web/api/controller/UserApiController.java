package com.catdog.help.web.api.controller;

import com.catdog.help.service.BulletinService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.user.ReadUserResponse;
import com.catdog.help.web.api.response.user.SaveUserResponse;
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
        // TODO: 2023-05-18 nickname, email 중복확인 후 예외 공통처리 만들기
        Long userId = userService.join(new SaveUserForm(request));
        return new SaveUserResponse(userId);
    }

    @GetMapping("/detail")
    public ReadUserResponse read(@RequestParam String nickname) {
        ReadUserForm form = userService.readByNickname(nickname);
        return new ReadUserResponse(form);
    }
}
