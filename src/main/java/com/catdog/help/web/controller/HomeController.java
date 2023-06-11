package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.web.form.BoardByRegion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller @Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;

    @GetMapping("/")
    public String home(Model model) {
        List<BoardByRegion> lostByRegion = boardService.getLostByRegion();
        List<BoardByRegion> bulletinByRegion = boardService.getBulletinByRegion();
        List<BoardByRegion> itemByRegion = boardService.getItemByRegion();
        model.addAttribute("lostByRegion", lostByRegion);
        model.addAttribute("bulletinByRegion", bulletinByRegion);
        model.addAttribute("itemByRegion", itemByRegion);
        return "home";
    }
}
