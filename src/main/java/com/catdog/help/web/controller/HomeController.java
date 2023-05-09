package com.catdog.help.web.controller;

import com.catdog.help.service.BulletinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BulletinService bulletinService;

    @GetMapping("/")
    public String home(Model model) {
        Map<String, Long> countMap = bulletinService.getCountByRegion();
        model.addAttribute("countMap", countMap);

        return "home";
    }
}
