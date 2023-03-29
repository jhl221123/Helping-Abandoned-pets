package com.catdog.help.web.controller;

import com.catdog.help.service.UserService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.LoginForm;
import com.catdog.help.web.form.user.ChangePasswordForm;
import com.catdog.help.web.form.user.SaveUserForm;
import com.catdog.help.web.form.user.UpdateUserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.catdog.help.web.SessionConst.*;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/new")
    public String joinForm(Model model) {
        model.addAttribute("saveUserForm", new SaveUserForm());
        return "users/joinForm";
    }

    @PostMapping("/new")
    public String join(@Validated @ModelAttribute("saveUserForm") SaveUserForm saveUserForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "users/joinForm";
        }

        if (userService.checkEmailDuplication(saveUserForm.getEmailId())) {
            bindingResult.rejectValue("emailId", "duplicate", "이미 가입된 이메일 아이디입니다.");
            return "users/joinForm";
        }

        if (userService.checkNickNameDuplication(saveUserForm.getNickName())) {
            bindingResult.rejectValue("nickName", "duplicate", "이미 존재하는 닉네임입니다.");
            return "users/joinForm";
        }

        userService.join(saveUserForm);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "users/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            bindingResult.reject("failLogin", "아이디와 비밀번호를 확인해주세요.");
            return "users/loginForm";
        }

        UserDto loginUserDto = userService.login(loginForm.getEmailId(), loginForm.getPassword());
        if (loginUserDto == null) {
            bindingResult.reject("failLogin", "아이디와 비밀번호를 확인해주세요.");
            return "users/loginForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, loginUserDto.getNickName());
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

    @GetMapping("/detail")
    public String detail(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String loginUserNickName = (String) session.getAttribute(LOGIN_USER);
        UserDto userDto = userService.getUserDtoByNickName(loginUserNickName);
        model.addAttribute("userDto", userDto);
        return "users/detail";
    }

    @GetMapping("/detail/edit")
    public String editForm(@SessionAttribute(name = LOGIN_USER) String nickName, Model model) {
        UpdateUserForm updateUserForm = userService.getUpdateForm(nickName);
        model.addAttribute("updateUserForm", updateUserForm);
        return "users/edit";
    }

    @PostMapping("/detail/edit")
    public String edit(@Validated @ModelAttribute("updateUserForm") UpdateUserForm updateUserForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/edit";
        }
        userService.updateUserInfo(updateUserForm);
        return "redirect:/users/detail";
    }

//    @GetMapping("/detail/edit/check")
//    public String checkPasswordForm(@ModelAttribute("loginForm") LoginForm loginForm) {
//        return "users/checkPassword";
//    }
//
//    @PostMapping("/detail/edit/check")
//    public String checkPassword(@Validated @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult,
//                                @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName, Model model) {
//        if (bindingResult.hasErrors()) {
//            bindingResult.reject("failCheck", "아이디와 비밀번호를 확인해주세요.");
//            return "users/checkPassword";
//        }
//
//        UserDto findUser = userService.login(loginForm.getEmailId(), loginForm.getPassword());
//        if (findUser == null || !findUser.getNickName().equals(nickName)) {
//            bindingResult.reject("failCheck", "아이디 혹은 비밀번호가 일치하지 않습니다.");
//            return "users/checkPassword";
//        }
//
//        model.addAttribute("changePasswordForm", new ChangePasswordForm());
//        return "users/editPassword";
//    }

    @GetMapping("/detail/edit/password")
    public String changePasswordForm(@ModelAttribute ChangePasswordForm changeForm) {
        return "users/editPassword";
    }

    @PostMapping("/detail/edit/password")
    public String changePassword(@Validated @ModelAttribute ChangePasswordForm changeForm, BindingResult bindingResult,
                                 @SessionAttribute(name = LOGIN_USER) String nickName) {
        if (bindingResult.hasErrors()) {
            return "users/editPassword";
        }

        String findPassword = userService.getUserDtoByNickName(nickName).getPassword();
        if (!findPassword.equals(changeForm.getBeforePassword())) {
            bindingResult.rejectValue("beforePassword", "inaccurate", "기존 비밀번호와 일치하지 않습니다.");
            return "users/editPassword";
        }

        if (!changeForm.getAfterPassword().equals(changeForm.getCheckPassword())) {
            bindingResult.reject("inaccurate", "새 비밀번호는 동일한 값을 가져야 합니다.");
            return "users/editPassword";
        }

        //변경 로직
        userService.changePassword(changeForm, nickName);
        return "redirect:/users/detail";
    }


    @GetMapping("/detail/delete")
    public String deleteForm(@SessionAttribute(name = LOGIN_USER) String nickName, Model model) {
        model.addAttribute("nickName", nickName);
        return "users/delete";
    }

    @PostMapping("/detail/delete")
    public String delete(@SessionAttribute(name = LOGIN_USER) String nickName) {
        userService.deleteUser(nickName);
        return "redirect:/users/logout";
    }
}