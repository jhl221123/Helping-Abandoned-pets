package com.catdog.help.web.controller;

import com.catdog.help.service.*;
import com.catdog.help.web.form.LoginForm;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.inquiry.PageInquiryForm;
import com.catdog.help.web.form.item.PageItemForm;
import com.catdog.help.web.form.user.ChangePasswordForm;
import com.catdog.help.web.form.user.ReadUserForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.catdog.help.MyConst.FAIL_LOGIN;
import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BulletinService bulletinService;
    private final ItemService itemService;
    private final InquiryService inquiryService;
    private final LikeService likeService;


    /***  create  ***/
    @GetMapping("/new")
    public String joinForm(Model model) {
        model.addAttribute("saveForm", new SaveUserForm());
        return "users/joinForm";
    }

    @PostMapping("/new")
    public String join(@Validated @ModelAttribute("saveForm") SaveUserForm saveForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/joinForm";
        }
        // TODO: 2023-04-20 각 프로퍼티에 특수기호 사용불가 검증 특히 _
        if (userService.isEmailDuplication(saveForm.getEmailId())){
            bindingResult.rejectValue("emailId", "duplicate", "이미 가입된 이메일 아이디입니다.");
            return "users/joinForm";
        }

        if (userService.isNicknameDuplication(saveForm.getNickname())) {
            bindingResult.rejectValue("nickname", "duplicate", "이미 존재하는 닉네임입니다.");
            return "users/joinForm";
        }

        userService.join(saveForm);
        return "redirect:/";
    }


    /***  login and logout  ***/
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "users/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "users/loginForm";
        }

        String nickname = userService.login(loginForm.getEmailId(), loginForm.getPassword());
        if (nickname.equals(FAIL_LOGIN)) {
            bindingResult.reject("failLogin", "아이디와 비밀번호를 확인해주세요.");
            return "users/loginForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, nickname);
        return "redirect:" + redirectURL;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }


    /***  read  ***/
    @GetMapping("/detail")
    public String detail(@SessionAttribute(name = LOGIN_USER) String nickname, Model model) {
        ReadUserForm readForm = userService.readByNickname(nickname);
        model.addAttribute("readForm", readForm);

        Long bulletinSize = bulletinService.countByNickname(nickname);
        Long itemSize = itemService.countByNickname(nickname);
        Long inquirySize = inquiryService.countByNickname(nickname);

        model.addAttribute("bulletinSize", bulletinSize);
        model.addAttribute("itemSize", itemSize);
        model.addAttribute("inquirySize", inquirySize);

        Long likeBulletinSize = bulletinService.countLikeBulletin(nickname);
        Long likeItemSize = itemService.countLikeItem(nickname);

        model.addAttribute("likeBulletinSize", likeBulletinSize);
        model.addAttribute("likeItemSize", likeItemSize);
        return "users/detail";
    }

    @GetMapping("/detail/bulletins")
    public String getMyBulletins(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                                @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PageBulletinForm> pageForms = bulletinService.getPageByNickname(nickname, pageable);

        model.addAttribute("pageForms", pageForms.getContent());

        int offset = pageable.getPageNumber() / 5 * 5;
        model.addAttribute("offset", offset);

        int limit = offset + 4;
        int endPage = Math.max(pageForms.getTotalPages() - 1, 0);
        int lastPage = getLastPage(limit, endPage);
        model.addAttribute("lastPage", lastPage);

        boolean isEnd = false;
        if (lastPage == endPage) {
            isEnd = true;
        }
        model.addAttribute("isEnd", isEnd);

        return "users/bulletinList";
    }

    @GetMapping("/detail/items")
    public String getMyItems(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                             @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PageItemForm> pageForms = itemService.getPageByNickname(nickname, pageable);
        model.addAttribute("pageForms", pageForms.getContent());

        int offset = pageable.getPageNumber() / 5 * 5;
        model.addAttribute("offset", offset);

        int limit = offset + 4;
        int endPage = Math.max(pageForms.getTotalPages() - 1, 0);
        int lastPage = getLastPage(limit, endPage);
        model.addAttribute("lastPage", lastPage);

        boolean isEnd = false;
        if (lastPage == endPage) {
            isEnd = true;
        }
        model.addAttribute("isEnd", isEnd);

        return "users/itemList";
    }

    @GetMapping("/detail/inquiries")
    public String getMyInquiries(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                                 @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PageInquiryForm> pageForms = inquiryService.getPageByNickname(nickname, pageable);
        model.addAttribute("pageForms", pageForms);

        int offset = pageable.getPageNumber() / 5 * 5;
        model.addAttribute("offset", offset);

        int limit = offset + 4;
        int endPage = Math.max(pageForms.getTotalPages() - 1, 0);
        int lastPage = getLastPage(limit, endPage);
        model.addAttribute("lastPage", lastPage);

        boolean isEnd = false;
        if (lastPage == endPage) {
            isEnd = true;
        }
        model.addAttribute("isEnd", isEnd);

        return "users/inquiryList";
    }

    @GetMapping("/detail/likes/bulletins")
    public String getLikeBulletinPage(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                                      @PageableDefault(sort = "board_id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PageBulletinForm> pageForms = bulletinService.getLikeBulletins(nickname, pageable);

        model.addAttribute("pageForms", pageForms.getContent());

        int offset = pageable.getPageNumber() / 5 * 5;
        model.addAttribute("offset", offset);

        int limit = offset + 4;
        int endPage = Math.max(pageForms.getTotalPages() - 1, 0);
        int lastPage = getLastPage(limit, endPage);
        model.addAttribute("lastPage", lastPage);

        boolean isEnd = false;
        if (lastPage == endPage) {
            isEnd = true;
        }
        model.addAttribute("isEnd", isEnd);

        return "users/likeBulletinList";
    }


    /***  update  ***/
    @GetMapping("/detail/edit")
    public String editForm(@SessionAttribute(name = LOGIN_USER) String nickname, Model model) {
        UpdateUserForm updateForm = userService.getUpdateForm(nickname);
        model.addAttribute("updateForm", updateForm);
        return "users/edit";
    }

    @PostMapping("/detail/edit")
    public String edit(@Validated @ModelAttribute("updateForm") UpdateUserForm updateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        userService.updateUserInfo(updateForm);
        return "redirect:/users/detail";
    }

    @GetMapping("/detail/edit/password")
    public String changePasswordForm(@ModelAttribute ChangePasswordForm changeForm) {
        return "users/editPassword";
    }

    @PostMapping("/detail/edit/password")
    public String changePassword(@Validated @ModelAttribute ChangePasswordForm changeForm, BindingResult bindingResult,
                                 @SessionAttribute(name = LOGIN_USER) String nickname) {
        if (bindingResult.hasErrors()) {
            return "users/editPassword";
        }

        if (!changeForm.getAfterPassword().equals(changeForm.getCheckPassword())) {
            bindingResult.reject("inaccurate", "새 비밀번호는 동일한 값을 가져야 합니다.");
            return "users/editPassword";
        }

        if (!userService.isSamePassword(changeForm.getBeforePassword(), nickname)) {
            bindingResult.rejectValue("beforePassword", "inaccurate", "기존 비밀번호와 일치하지 않습니다.");
            return "users/editPassword";
        }

        //변경 로직
        userService.changePassword(changeForm.getAfterPassword(), nickname);
        return "redirect:/users/detail";
    }


    /***  delete  ***/
    @GetMapping("/detail/delete")
    public String delete(@SessionAttribute(name = LOGIN_USER) String nickname) {
        userService.deleteUser(nickname);
        return "redirect:/users/logout";
    }


    private int getLastPage(int limit, int endPage) {
        int lastPage = Math.min(endPage, limit);
        if(lastPage<0) lastPage = 0;
        return lastPage;
    }
}