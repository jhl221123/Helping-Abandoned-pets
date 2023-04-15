package com.catdog.help.web.controller;

import com.catdog.help.service.ItemBoardService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.form.itemboard.PageItemBoardForm;
import com.catdog.help.web.form.itemboard.ReadItemBoardForm;
import com.catdog.help.web.form.itemboard.SaveItemBoardForm;
import com.catdog.help.web.form.itemboard.UpdateItemBoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemBoardController {

    private final ItemBoardService itemBoardService;
    private final LikeService likeService;
    private final ViewUpdater viewUpdater;


    /***  create  ***/
    @GetMapping("/items/new")
    public String createItemBoardForm(@SessionAttribute(name = LOGIN_USER) String nickname, Model model) {
        SaveItemBoardForm saveForm = new SaveItemBoardForm();
        model.addAttribute("saveForm", saveForm);
        model.addAttribute("nickname", nickname);
        return "items/create";
    }

    @PostMapping("/items/new")
    public String createItemBoard(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                                  @Validated @ModelAttribute("saveForm") SaveItemBoardForm saveForm, BindingResult bindingResult) {
        model.addAttribute("nickname", nickname);
        if (bindingResult.hasErrors()) {
            return "items/create";
        }

        for (MultipartFile image : saveForm.getImages()) {
            if (image.isEmpty()) {
                bindingResult.rejectValue("images", "empty", "반드시 하나 이상의 이미지를 업로드 해야합니다.");
                return "items/create";
            }
        }

        itemBoardService.createBoard(saveForm, nickname);
        return "redirect:/items?page=1";
    }


    /***  read  ***/
    @GetMapping("/items")
    public String itemBoardList(@RequestParam("page") int page, Model model) {
        List<PageItemBoardForm> pageForms = itemBoardService.readPage(page);
        model.addAttribute("pageForms", pageForms);

        int lastPage = itemBoardService.countPage();
        model.addAttribute("lastPage", lastPage); // TODO: 2023-03-31 count query
        return "items/list";
    }

    @GetMapping("/items/{id}")
    public String readItemBoard(@PathVariable("id") Long id, Model model,
                                @SessionAttribute(name = LOGIN_USER) String nickname,
                                HttpServletRequest request, HttpServletResponse response) {
        //조회수 증가
        viewUpdater.addView(id, request, response);

        model.addAttribute("nickname", nickname); // 수정버튼 본인확인

        ReadItemBoardForm findReadForm = itemBoardService.readBoard(id);
        model.addAttribute("readForm", findReadForm);
        model.addAttribute("firstImage", findReadForm.getImages().get(0));
        model.addAttribute("imageSize", findReadForm.getImages().size());

        boolean checkLike = likeService.checkLike(id, nickname);
        model.addAttribute("checkLike", checkLike);

        return "items/detail";
    }


    /***  update  ***/
    @GetMapping("/items/{id}/edit")
    public String updateForm(@PathVariable("id") Long id, Model model,
                             @SessionAttribute(name = LOGIN_USER) String nickname) {
        UpdateItemBoardForm updateForm = itemBoardService.getUpdateForm(id);
        model.addAttribute("updateForm", updateForm);
        model.addAttribute("nickname", nickname);
        return "items/update";
    }

    @PostMapping("/items/{id}/edit")
    public String update(@PathVariable("id") Long id,
                         @Validated @ModelAttribute("updateForm") UpdateItemBoardForm updateForm, BindingResult bindingResult) {
        itemBoardService.updateBoard(id, updateForm);
        return "redirect:/items/{id}";
    }

    @GetMapping("/items/{id}/like")
    public String clickLike(@PathVariable("id") Long id,
                            @SessionAttribute(name = LOGIN_USER) String nickname) {
        likeService.clickLike(id, nickname);
        return "redirect:/items/{id}";
    }

    @GetMapping("/items/{id}/status")
    public String changeItemStatus(@PathVariable("id") Long id,
                                   @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        ReadItemBoardForm readForm = itemBoardService.readBoard(id);
        if (!readForm.getNickname().equals(nickname)) {
            return "redirect:/items/{id}";
        }

        itemBoardService.changeStatus(id);
        return "redirect:/items/{id}";
    }

    /***  delete  ***/
    @GetMapping("/items/{id}/delete")
    public String deleteForm(@PathVariable("id") Long id, Model model,
                             @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 삭제 가능
        ReadItemBoardForm readForm = itemBoardService.readBoard(id);
        if (!readForm.getNickname().equals(nickname)) {
            return "redirect:/items/{id}";
        }

        model.addAttribute("nickname", nickname);
        model.addAttribute("readForm", readForm);
        return "items/delete";
    }

    @PostMapping("/items/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 삭제 가능
        ReadItemBoardForm readForm = itemBoardService.readBoard(id);
        if (!readForm.getNickname().equals(nickname)) {
            return "redirect:/items/{id}";
        }
        itemBoardService.deleteBoard(id);
        return "redirect:/items?page=1";
    }
}
