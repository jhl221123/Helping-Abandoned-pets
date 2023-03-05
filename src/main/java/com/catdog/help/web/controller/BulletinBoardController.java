package com.catdog.help.web.controller;

import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.service.BulletinBoardService;
import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.UpdateBulletinBoardForm;
import com.catdog.help.web.form.SaveBulletinBoardForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
        model.addAttribute("saveBoardForm", new SaveBulletinBoardForm());
        return "bulletinBoard/create";
    }

    @PostMapping("/boards/new")
    public String createBulletinBoard(@Validated @ModelAttribute("saveBoardForm") SaveBulletinBoardForm saveBoardForm,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "bulletinBoard/create";
        }
        bulletinBoardService.createBoard(saveBoardForm);
        return "redirect:/boards";
    }

    @GetMapping("/boards")
    public String BulletinBoardList(Model model) {
        List<BulletinBoardDto> bulletinBoardDtos = bulletinBoardService.readAll();
        model.addAttribute("bulletinBoardDtos", bulletinBoardDtos);
        return "bulletinBoard/list";
    }

    @GetMapping("/boards/{id}")
    public String readBulletinBoard(@PathVariable("id") Long id, Model model) {
        BulletinBoardDto bulletinBoardDto = bulletinBoardService.readBoard(id);
        model.addAttribute("bulletinBoardDto", bulletinBoardDto);
        return "bulletinBoard/detail";
    }

    @GetMapping("/boards/{id}/edit")
    public String updateBulletinBoardForm(@PathVariable("id") Long id, Model model) {
        UpdateBulletinBoardForm updateBulletinBoardForm = bulletinBoardService.getUpdateForm(id);
        model.addAttribute("updateBoardForm", updateBulletinBoardForm);
        return "bulletinBoard/update";
    }

    @PostMapping("/boards/{id}/edit")
    public String updateBulletinBoard(@Validated @ModelAttribute("updateBoardForm") UpdateBulletinBoardForm updateBulletinBoardForm,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "bulletinBoard/update";
        }
        bulletinBoardService.updateBoard(updateBulletinBoardForm);
        return "redirect:/boards/{id}";
    }
}
