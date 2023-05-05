package com.catdog.help.web.controller;

import com.catdog.help.web.SessionConst;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) String nickname, Model model) {

        if (nickname == null) {
            return "home";
        }

        model.addAttribute("nickname", nickname);
        return "home";
    }
}
