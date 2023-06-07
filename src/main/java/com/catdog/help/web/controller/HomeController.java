package com.catdog.help.web.controller;

import com.catdog.help.service.BulletinService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.LostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final LostService lostService;
    private final BulletinService bulletinService;
    private final ItemService itemService;

    @GetMapping("/")
    public String home(Model model) {
        Map<String, Long> lostMap = lostService.getCountByRegion();
        model.addAttribute("lostMap", lostMap);

        Map<String, Long> bulletinMap = bulletinService.getCountByRegion();
        model.addAttribute("bulletinMap", bulletinMap);

        Map<String, Long> itemMap = itemService.getCountByRegion();
        model.addAttribute("itemMap", itemMap);

        return "home";
    }
}
