package com.catdog.help.web.controller;

import com.catdog.help.repository.UserRepository;
import com.catdog.help.service.UserService;
import com.catdog.help.web.form.SaveUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/users/new")
    public String joinUser(Model model) {
        model.addAttribute("saveUserForm", new SaveUserForm());
        return "users/joinForm";
    }

    @PostMapping("/users/new")
    public String join(@Validated @ModelAttribute("saveUserForm") SaveUserForm saveUserForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "users/joinForm";
        }

        userService.join(saveUserForm);
        return "redirect:/";
    }
}
