package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.ItemService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.form.item.EditItemForm;
import com.catdog.help.web.form.item.PageItemForm;
import com.catdog.help.web.form.item.ReadItemForm;
import com.catdog.help.web.form.item.SaveItemForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final BoardService boardService;
    private final LikeService likeService;
    private final ViewUpdater viewUpdater;


    /***  create  ***/
    @GetMapping("/items/new")
    public String getSaveForm(@SessionAttribute(name = LOGIN_USER) String nickname, Model model) {
        SaveItemForm saveForm = new SaveItemForm();
        model.addAttribute("saveForm", saveForm);
        model.addAttribute("nickname", nickname);
        return "items/create";
    }

    @PostMapping("/items/new")
    public String saveBoard(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                            @Validated @ModelAttribute("saveForm") SaveItemForm saveForm, BindingResult bindingResult) {
//        model.addAttribute("nickname", nickname); test 에서 redirect 경로에 파라미터로 붙어서 일단 주석해둠. 이거 없이도 닉 보이도록 해보자.
        if (bindingResult.hasErrors()) {
            return "items/create";
        }

        for (MultipartFile image : saveForm.getImages()) {
            if (image.isEmpty()) {
                bindingResult.rejectValue("images", "empty", "반드시 하나 이상의 이미지를 업로드 해야합니다.");
                return "items/create";
            }
        }

        itemService.save(saveForm, nickname);
        return "redirect:/items?page=0";
    }


    /***  read  ***/
    @GetMapping("/items")
    public String getPage(Pageable pageable, Model model) {
        Page<PageItemForm> pageForms = itemService.getPage(pageable);
        model.addAttribute("pageForms", pageForms);

        int totalPages = pageForms.getTotalPages();
        model.addAttribute("lastPage", totalPages);
        return "items/list";
    }

    @GetMapping("/items/{id}")
    public String readBoard(@PathVariable("id") Long id, Model model,
                            @SessionAttribute(name = LOGIN_USER) String nickname,
                            HttpServletRequest request, HttpServletResponse response) {
        //조회수 증가
        viewUpdater.addView(id, request, response);

        model.addAttribute("nickname", nickname); // 수정버튼 본인확인

        ReadItemForm findReadForm = itemService.read(id);
        model.addAttribute("readForm", findReadForm);
        model.addAttribute("firstImage", findReadForm.getImages().get(0));
        model.addAttribute("imageSize", findReadForm.getImages().size());

        boolean checkLike = likeService.isLike(id, nickname);
        model.addAttribute("checkLike", checkLike);

        return "items/detail";
    }


    /***  update  ***/
    @GetMapping("/items/{id}/like")
    public String clickLike(@PathVariable("id") Long id,
                            @SessionAttribute(name = LOGIN_USER) String nickname) {
        likeService.clickLike(id, nickname);
        return "redirect:/items/{id}";
    }

    @GetMapping("/items/{id}/edit")
    public String getEditForm(@PathVariable("id") Long id, Model model,
                              @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        EditItemForm updateForm = itemService.getEditForm(id);
        model.addAttribute("updateForm", updateForm);
        model.addAttribute("nickname", nickname);
        return "items/edit";
    }

    @PostMapping("/items/{id}/edit")
    public String editBoard(@PathVariable("id") Long id, @SessionAttribute(name = LOGIN_USER) String nickname,
                            @Validated @ModelAttribute("updateForm") EditItemForm updateForm, BindingResult bindingResult) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "items/edit";
        }

        itemService.update(updateForm);
        return "redirect:/items/{id}";
    }

    @GetMapping("/items/{id}/status")
    public String changeItemStatus(@PathVariable("id") Long id,
                                   @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        itemService.changeStatus(id);
        return "redirect:/items/{id}";
    }

    /***  delete  ***/
    @GetMapping("/items/{id}/delete")
    public String getDeleteForm(@PathVariable("id") Long id, Model model,
                                @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        ReadItemForm readForm = itemService.read(id);
        model.addAttribute("nickname", nickname);
        model.addAttribute("readForm", readForm);
        return "items/delete";
    }

    @PostMapping("/items/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id,
                              @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        itemService.delete(id);
        return "redirect:/items?page=0";
    }


    private Boolean isWriter(Long id, String nickname) {
        String writer = boardService.getWriter(id);
        return writer.equals(nickname) ? true : false;
    }
}
