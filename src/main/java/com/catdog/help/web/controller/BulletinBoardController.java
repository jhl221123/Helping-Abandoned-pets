package com.catdog.help.web.controller;

import com.catdog.help.FileStore;
import com.catdog.help.domain.Board.Comment;
import com.catdog.help.service.BulletinBoardService;
import com.catdog.help.web.SessionConst;
import com.catdog.help.web.dto.BulletinBoardDto;
import com.catdog.help.web.form.bulletinboard.PageBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.comment.CommentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BulletinBoardController {

    private final BulletinBoardService bulletinBoardService;
    private final FileStore fileStore;

    /***  create  ***/
    @GetMapping("/boards/new")
    public String createBulletinBoardForm(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickName, Model model) {
        SaveBulletinBoardForm saveBoardForm = new SaveBulletinBoardForm();
        model.addAttribute("nickName", nickName);
        model.addAttribute("saveBoardForm", saveBoardForm);
        return "bulletinBoard/create";
    }

    @PostMapping("/boards/new")
    public String createBulletinBoard(@SessionAttribute(name = SessionConst.LOGIN_USER) String nickName,
                                      @Validated @ModelAttribute("saveBoardForm") SaveBulletinBoardForm saveBoardForm,
                                      BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            return "bulletinBoard/create";
        }
        Long boardId = bulletinBoardService.createBoard(saveBoardForm, nickName);
        redirectAttributes.addAttribute("id", boardId);
        return "redirect:/boards/{id}";
    }

    @PostMapping("/boards/{id}/comments/parent")
    public String createParentComment(@PathVariable("id") Long boardId, @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName,
                                    @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // TODO: 2023-03-17  redirectAttribute 이용해서 검증하기
            return "bulletinBoard/detail";
        }
        commentForm.setBoardId(boardId);
        commentForm.setNickName(nickName);
        bulletinBoardService.createComment(commentForm, -1L);
        return "redirect:/boards/{id}";
    }

    @PostMapping("/boards/{id}/comments/child")
    public String createChildComment(@PathVariable("id") Long boardId, @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName,
                                     @Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult,
                                     @RequestParam("parentId") Long parentId) {
        if (bindingResult.hasErrors()) {
            // TODO: 2023-03-17  redirectAttribute 이용해서 검증하기
            return "bulletinBoard/detail";
        }
        commentForm.setBoardId(boardId);
        commentForm.setNickName(nickName);
        bulletinBoardService.createComment(commentForm, parentId);
        return "redirect:/boards/{id}";
    }

    /***  read  ***/
    @GetMapping("/boards")
    public String BulletinBoardList(Model model) {
        List<PageBulletinBoardForm> pageBoardForms = bulletinBoardService.readAll();
        model.addAttribute("pageBoardForms", pageBoardForms);
        return "bulletinBoard/list";
    }

    @GetMapping("/boards/{id}")
    public String readBulletinBoard(@PathVariable("id") Long id, Model model,
                                    @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName,
                                    @RequestParam(name = "clickChild", required = false) Long clickChild) {
        model.addAttribute("nickName", nickName); // detail 수정버튼

        BulletinBoardDto bulletinBoardDto = bulletinBoardService.readBoard(id);
        model.addAttribute("bulletinBoardDto", bulletinBoardDto);

        boolean checkLike = bulletinBoardService.checkLike(id, nickName);
        model.addAttribute("checkLike", checkLike);

        CommentForm commentForm = new CommentForm();
        model.addAttribute("commentForm", commentForm);

        List<CommentForm> commentForms = bulletinBoardService.readComments(id);
        if (commentForms != null) {
            model.addAttribute("commentForms", commentForms);
        }

        //대댓글 폼 열기
        if (clickChild != null) {
            model.addAttribute("clickChild", clickChild);
        }

        return "bulletinBoard/detail";
    }

    @ResponseBody
    @GetMapping("/images/{fileName}")
    public Resource downloadImage(@PathVariable String fileName) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(fileName));
    }


    /***  update  ***/
    @GetMapping("/boards/{id}/like")
    public String changeScore(@PathVariable("id") Long id,
                              @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName) {
        bulletinBoardService.clickLike(id, nickName);
        return "redirect:/boards/{id}";
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
        model.addAttribute("nickName", nickName);
        return "bulletinBoard/update";
    }

    @PostMapping("/boards/{id}/edit")
    public String updateBulletinBoard(@Validated @ModelAttribute("updateBoardForm") UpdateBulletinBoardForm updateBulletinBoardForm,
                                      BindingResult bindingResult) throws IOException {
        // TODO: 2023-03-12 여기도 작성자 외 접근 못하도록 막아야 할 듯

        if (bindingResult.hasErrors()) {
            return "bulletinBoard/update";
        }
        bulletinBoardService.updateBoard(updateBulletinBoardForm);
        return "redirect:/boards/{id}";
    }


    /***  delete  ***/
    @GetMapping("/boards/{id}/delete")
    public String deleteBulletinBoardForm(@PathVariable("id") Long id, Model model,
                                          @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName) {
        //작성자 본인만 접근 가능
        BulletinBoardDto findBoardDto = bulletinBoardService.readBoard(id);
        if (!findBoardDto.getUser().getNickName().equals(nickName)) {
            return "redirect:/boards/{id}";
        }
        String boardTitle = findBoardDto.getTitle();
        model.addAttribute("boardId", id);
        model.addAttribute("boardTitle", boardTitle);
        model.addAttribute("nickName", nickName);
        return "bulletinBoard/delete";
    }

    @PostMapping("/boards/{id}/delete")
    public String deleteBulletinBoard(@PathVariable("id") Long id, Model model,
                                      @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName) {
        //작성자 본인만 삭제 가능
        BulletinBoardDto findBoardDto = bulletinBoardService.readBoard(id);
        if (!findBoardDto.getUser().getNickName().equals(nickName)) {
            return "redirect:/boards/{id}";
        }
        bulletinBoardService.deleteBoard(id);
        return "redirect:/boards";
    }

    @GetMapping("/boards/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Long commentId,
                                @PathVariable("id") Long id,
                                @SessionAttribute(name = SessionConst.LOGIN_USER) String nickName) {
        //본인 댓글만 삭제 가능
        String commentNickName = bulletinBoardService.readComment(commentId).getNickName();
        if (!commentNickName.equals(nickName)) {
            return "redirect:/boards/{id}";
        }

        bulletinBoardService.deleteComment(commentId);
        return "redirect:/boards/{id}";
    }
}
