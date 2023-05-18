package com.catdog.help.web.api.controller;

import com.catdog.help.service.BulletinService;
import com.catdog.help.service.InquiryService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.api.request.user.SaveUserRequest;
import com.catdog.help.web.api.response.user.SaveUserResponse;
import com.catdog.help.web.form.user.SaveUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Long userId = userService.join(new SaveUserForm(request));
        return new SaveUserResponse(userId);
    }
}
