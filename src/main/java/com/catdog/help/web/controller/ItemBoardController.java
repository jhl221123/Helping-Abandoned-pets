package com.catdog.help.web.controller;

import com.catdog.help.service.ItemBoardService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.form.itemBoard.PageItemBoardForm;
import com.catdog.help.web.form.itemBoard.ReadItemBoardForm;
import com.catdog.help.web.form.itemBoard.SaveItemBoardForm;
import com.catdog.help.web.form.itemBoard.UpdateItemBoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
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
        model.addAttribute("nickName", nickName);
        if (bindingResult.hasErrors()) {
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

        int lastPage = itemBoardService.countPage();
        model.addAttribute("lastPage", lastPage); // TODO: 2023-03-31 count query
        return "items/list";
    }

    @GetMapping("/items/{id}")
    public String readItemBoard(@PathVariable("id") Long id, Model model,
                                @SessionAttribute(name = LOGIN_USER) String nickName,
                                HttpServletRequest request, HttpServletResponse response) {
        //start views using cookie
        if (request.getCookies() != null) {
            //로그인 사용자
            Cookie viewCookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("view"))
                    .findAny()
                    .orElse(null);
            log.info("cookie ===================================={}", viewCookie);
            if (viewCookie != null) {
                //조회한 게시글 이미 존재, 해당 게시글 조회 여부 확인
                if (!viewCookie.getValue().contains(String.valueOf(id))) {
                    //처음 조회한 게시글
                    itemBoardService.addViews(id);
                    viewCookie.setValue(viewCookie.getValue() + "_" + String.valueOf(id));
                    viewCookie.setMaxAge(60 * 60 * 12);
                    response.addCookie(viewCookie);
                } else {
                    //이미 조회 한 게시글
                }
            } else {
                //조회한 게시글이 없어 view cookie 가 없는 경우
                itemBoardService.addViews(id);
                Cookie newViewCookie = new Cookie("view", String.valueOf(id));
                newViewCookie.setMaxAge(60 * 60 * 12);
                response.addCookie(newViewCookie);
            }
        } else {
            // TODO: 2023-03-18 비회원 사용자
        }
        //end views using cookie

        model.addAttribute("nickName", nickName); // 수정버튼 본인확인

        ReadItemBoardForm findReadForm = itemBoardService.readBoard(id);
        model.addAttribute("readForm", findReadForm);
        log.info("status 뭐냐={}", findReadForm.getStatus());
        model.addAttribute("firstImage", findReadForm.getImages().get(0));
        model.addAttribute("imageSize", findReadForm.getImages().size());

        boolean checkLike = likeService.checkLike(id, nickName);
        model.addAttribute("checkLike", checkLike);



        return "items/detail";
    }


    /***  update  ***/
    @GetMapping("/items/{id}/edit")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        UpdateItemBoardForm updateForm = itemBoardService.getUpdateForm(id);
        model.addAttribute("updateForm", updateForm);
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
                            @SessionAttribute(name = LOGIN_USER) String nickName) {
        likeService.clickLike(id, nickName);
        return "redirect:/items/{id}";
    }

    @GetMapping("/items/{id}/status")
    public String changeItemStatus(@PathVariable("id") Long id,
                                   @SessionAttribute(name = LOGIN_USER) String nickName) {
        //작성자 본인만 수정 가능
        ReadItemBoardForm readForm = itemBoardService.readBoard(id);
        if (!readForm.getUser().getNickName().equals(nickName)) {
            return "redirect:/items/{id}";
        }

        itemBoardService.changeStatus(id);
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
