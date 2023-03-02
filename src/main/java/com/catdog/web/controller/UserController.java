package com.catdog.web.controller;

import com.catdog.web.repository.UserRepositoryImpl;
import com.catdog.web.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepositoryImpl userRepositoryImpl;
    private final UserService userService;

    @GetMapping("/users/new")
    public String joinUser(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/joinForm";
    }

    @PostMapping("/users/new")
    public String join(@ModelAttribute("userForm") UserForm userForm) {
        userService.join(userForm);
        return "redirect:/";
    }
}
