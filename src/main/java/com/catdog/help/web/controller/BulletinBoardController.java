package com.catdog.help.web.controller;

import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.service.BulletinBoardService;
import com.catdog.help.web.UpdateBoardForm;
import com.catdog.help.web.SaveBoardForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BulletinBoardController {

    private final BulletinBoardService bulletinBoardService;
    private final BulletinBoardRepository bulletinBoardRepository;


    @GetMapping("/boards/new")
    public String createBulletinBoardForm(Model model) {
        model.addAttribute("updateBoardForm", new UpdateBoardForm());
        return "bulletinBoard/create";
    }

    @PostMapping("/boards/new")
    public String createBulletinBoard(@ModelAttribute("saveBoardForm") SaveBoardForm saveBoardForm) {
        bulletinBoardService.createBoard(saveBoardForm);
        return "redirect:/boards";
    }

    @GetMapping("/boards")
    public String BulletinBoardList(Model model) {
        List<UpdateBoardForm> updateBoardForms = bulletinBoardService.readAll();
        model.addAttribute("updateBoardForms", updateBoardForms);
        return "bulletinBoard/list";
    }

    @GetMapping("/boards/{boardNo}")
    public String readBulletinBoard(@PathVariable("boardNo") Long boardNo, Model model) {
        UpdateBoardForm updateBoardForm = bulletinBoardService.readBoard(boardNo);
        model.addAttribute("updateBoardForm", updateBoardForm);
        return "bulletinBoard/detail";
    }

    @GetMapping("/boards/{boardNo}/edit")
    public String updateBulletinBoardForm(@PathVariable("boardNo") Long boardNo, Model model) {
        UpdateBoardForm updateBoardForm = bulletinBoardService.readBoard(boardNo);
        model.addAttribute("updateBoardForm", updateBoardForm);
        return "bulletinBoard/update";
    }

    @PostMapping("/boards/{boardNo}/edit")
    public String updateBulletinBoard(@ModelAttribute("updateBoardForm") UpdateBoardForm updateBoardForm) {
        bulletinBoardService.updateBoard(updateBoardForm);
        return "redirect:/boards/{boardNo}";
    }
}
