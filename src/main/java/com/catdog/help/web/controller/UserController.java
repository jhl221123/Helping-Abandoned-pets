package com.catdog.help.web.controller;

import com.catdog.help.domain.User;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.service.UserService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.LoginForm;
import com.catdog.help.web.form.SaveUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
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

        userService.join(saveUserForm);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "users/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult,
                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "users/loginForm";
        }

        UserDto loginUserDto = userService.login(loginForm.getEmailId(), loginForm.getPassword());
        if (loginUserDto == null) {
            bindingResult.reject("failLogin", "아이디와 비밀번호를 확인해주세요.");
            return "users/loginForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, loginUserDto.getNickName());

        return "redirect:/";
    }

}
