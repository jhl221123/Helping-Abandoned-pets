package com.catdog.help.web.controller;

import com.catdog.help.domain.User;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.service.BulletinBoardService;
import com.catdog.help.service.UserService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.dto.UserDto;
import com.catdog.help.web.form.UpdateBulletinBoardForm;
import com.catdog.help.web.form.SaveBulletinBoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BulletinBoardController {

    private final BulletinBoardService bulletinBoardService;
    private final UserService userService;


    @GetMapping("/boards/new")
    public String createBulletinBoardForm(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickName, Model model) {
        UserDto findUserDto = userService.getUserDtoByNickName(nickName);
        User sessionUser = userService.getUser(findUserDto); // TODO: 2023-03-06 엔티티 반환 안하려고 한건데 더 고민해봐야할 듯. 준영속으로 만드는 것도 찜찜해서..
        SaveBulletinBoardForm saveBoardForm = new SaveBulletinBoardForm();
        saveBoardForm.setUser(sessionUser);
        model.addAttribute("saveBoardForm", saveBoardForm);
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
    public String readBulletinBoard(@PathVariable("id") Long id, Model model,
                                    @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName) {
        BulletinBoardDto bulletinBoardDto = bulletinBoardService.readBoard(id);
        model.addAttribute("bulletinBoardDto", bulletinBoardDto);
        model.addAttribute("nickName", nickName);
        return "bulletinBoard/detail";
    }

    @GetMapping("/boards/{id}/edit")
    public String updateBulletinBoardForm(@PathVariable("id") Long id, Model model,
                                          @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName) {
        //작성자 본인만 수정 가능
        BulletinBoardDto findBoardDto = bulletinBoardService.readBoard(id);
        if (!findBoardDto.getUser().getNickName().equals(nickName)) {
            return "redirect:/boards/{id}";
        }
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
