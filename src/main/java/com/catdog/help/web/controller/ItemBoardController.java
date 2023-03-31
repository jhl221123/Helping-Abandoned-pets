package com.catdog.help.web.controller;

import com.catdog.help.service.ItemBoardService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.form.itemBoard.SaveItemBoardForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;


import static com.catdog.help.web.SessionConst.*;

@Controller
@RequiredArgsConstructor
public class ItemBoardController {

    private final ItemBoardService itemBoardService;

    @GetMapping("/items/new")
    public String createItemBoardForm(@SessionAttribute(name = LOGIN_USER) String nickName, Model model) {
        SaveItemBoardForm saveForm = new SaveItemBoardForm();
        model.addAttribute("saveForm", saveForm);
        model.addAttribute("nickName", nickName);
        return "items/create";
    }

    @PostMapping("/items/new")
    public String createItemBoard(@Validated @ModelAttribute("saveForm") SaveItemBoardForm saveForm,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "items/create";
        }

        itemBoardService.createBoard(saveForm);
        return "redirect:/items";
    }
}
