package com.catdog.help.web.controller;

import com.catdog.help.service.ItemBoardService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.itemBoard.PageItemBoardForm;
import com.catdog.help.web.form.itemBoard.ReadItemBoardForm;
import com.catdog.help.web.form.itemBoard.SaveItemBoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

import static com.catdog.help.web.SessionConst.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemBoardController {

    private final ItemBoardService itemBoardService;
    private final LikeService likeService;


    /***  create  ***/
    @GetMapping("/items/new")
    public String createItemBoardForm(@SessionAttribute(name = LOGIN_USER) String nickName, Model model) {
        SaveItemBoardForm saveForm = new SaveItemBoardForm();
        model.addAttribute("saveForm", saveForm);
        model.addAttribute("nickName", nickName);
        return "items/create";
    }

    @PostMapping("/items/new")
    public String createItemBoard(@SessionAttribute(name = LOGIN_USER) String nickName, Model model,
                                  @Validated @ModelAttribute("saveForm") SaveItemBoardForm saveForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("nickName", nickName);
            return "items/create";
        }

        for (MultipartFile image : saveForm.getImages()) {
            if (image.isEmpty()) {
                bindingResult.rejectValue("images", "empty", "반드시 하나 이상의 이미지를 업로드 해야합니다.");
                return "items/create";
            }
        }

        itemBoardService.createBoard(saveForm, nickName);
        return "redirect:/items?page=1";
    }


    /***  read  ***/
    @GetMapping("/items")
    public String itemBoardList(@RequestParam("page") int page, Model model) {
        List<PageItemBoardForm> pageForms = itemBoardService.readPage(page);
        model.addAttribute("pageForms", pageForms);
        model.addAttribute("lastPage", 3); // TODO: 2023-03-31 count query
        return "items/list";
    }

    @GetMapping("/items/{id}")
    public String readItemBoard(@PathVariable("id") Long itemBoardId, Model model,
                                @SessionAttribute(name = LOGIN_USER) String nickName) {

        boolean checkLike = likeService.checkLike(itemBoardId, nickName);
        model.addAttribute("checkLike", checkLike);

        ReadItemBoardForm findReadForm = itemBoardService.readBoard(itemBoardId);
        model.addAttribute("readForm", findReadForm);
        log.info("what status = {}", findReadForm.getStatus());

        model.addAttribute("nickName", nickName);
        return "items/detail";
    }


    /***  update  ***/
    @GetMapping("/items/{id}/like")
    public String clickLike(@PathVariable("id") Long id,
                            @SessionAttribute(name = LOGIN_USER) String nickName) {
        likeService.clickLike(id, nickName);
        return "redirect:/items/{id}";
    }


    /***  delete  ***/
    @GetMapping("/items/{id}/delete")
    public String deleteForm(@PathVariable("id") Long id, Model model,
                             @SessionAttribute(name = LOGIN_USER) String nickName) {
        //작성자 본인만 삭제 가능
        ReadItemBoardForm readForm = itemBoardService.readBoard(id);
        if (!readForm.getUser().getNickName().equals(nickName)) {
            return "redirect:/items/{id}";
        }

        model.addAttribute("nickName", nickName);
        model.addAttribute("readForm", readForm);
        return "items/delete";
    }

    @PostMapping("/items/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         @SessionAttribute(name = LOGIN_USER) String nickName) {
        //작성자 본인만 삭제 가능
        ReadItemBoardForm readForm = itemBoardService.readBoard(id);
        if (!readForm.getUser().getNickName().equals(nickName)) {
            return "redirect:/items/{id}";
        }
        itemBoardService.deleteBoard(id);
        return "redirect:/items?page=1";
    }
}
