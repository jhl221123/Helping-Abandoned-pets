package com.catdog.web.controller;

import com.catdog.web.domain.BulletinBoard;
import com.catdog.web.repository.BulletinBoardRepository;
import com.catdog.web.service.BulletinBoardService;
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
        model.addAttribute("boardForm", new BoardForm());
        return "bulletinBoard/createForm";
    }

    @PostMapping("/boards/new")
    public String createBulletinBoard(@ModelAttribute("boardForm") BoardForm boardForm) {
        bulletinBoardService.createBoard(boardForm);
        return "redirect:/boards";
    }

    @GetMapping("/boards")
    public String BulletinBoardList(Model model) {
        List<BoardForm> boardForms = bulletinBoardService.readAll();
        model.addAttribute("boardForms", boardForms); //엔티티로 접근, 수정 필요
        return "bulletinBoard/listForm";
    }

    @GetMapping("/boards/{boardNo}")
    public String readBulletinBoard(@PathVariable("boardNo") Long boardNo, Model model) {
        BoardForm boardForm = bulletinBoardService.readBoard(boardNo);
        model.addAttribute("boardForm", boardForm);
        return "bulletinBoard/readForm";
    }

    @GetMapping("/boards/{boardNo}/edit")
    public String updateBulletinBoardForm(@PathVariable("boardNo") Long boardNo, Model model) {
        BoardForm boardForm = bulletinBoardService.updateBoard(boardNo);
        model.addAttribute("boardForm", boardForm);
        return "bulletinBoard/readForm";
    }
}
