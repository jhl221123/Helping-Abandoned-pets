package com.catdog.help.web.controller;

import com.catdog.help.service.BulletinBoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.form.bulletinboard.PageBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.ReadBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.SaveBulletinBoardForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinBoardForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.UpdateCommentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BulletinBoardController {

    private final BulletinBoardService bulletinBoardService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final ViewUpdater viewUpdater;


    /***  create  ***/
    @GetMapping("/boards/new")
    public String createBulletinBoardForm(@SessionAttribute(name = LOGIN_USER) String nickName, Model model) {
        SaveBulletinBoardForm saveBoardForm = new SaveBulletinBoardForm();
        model.addAttribute("nickName", nickName);
        model.addAttribute("saveBoardForm", saveBoardForm);
        return "bulletinBoard/create";
    }

    @PostMapping("/boards/new")
    public String createBulletinBoard(@SessionAttribute(name = LOGIN_USER) String nickName, Model model,
                                      @Validated @ModelAttribute("saveBoardForm") SaveBulletinBoardForm saveBoardForm,
                                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        model.addAttribute("nickName", nickName);
        if (bindingResult.hasErrors()) {
            return "bulletinBoard/create";
        }

        Long boardId = bulletinBoardService.createBoard(saveBoardForm, nickName);
        redirectAttributes.addAttribute("id", boardId);
        return "redirect:/boards/{id}";
    }


    /***  read  ***/
    @GetMapping("/boards")
    public String BulletinBoardList(@RequestParam(value = "page") int page, Model model) {
        List<PageBulletinBoardForm> pageBoardForms = bulletinBoardService.readPage(page);
        model.addAttribute("pageBoardForms", pageBoardForms);

        int lastPage = bulletinBoardService.countPages();
        model.addAttribute("lastPage", lastPage);
        return "bulletinBoard/list";
    }

    @GetMapping("/boards/{id}")
    public String readBulletinBoard(@PathVariable("id") Long id, Model model,
                                    @SessionAttribute(name = LOGIN_USER) String nickName,
                                    @ModelAttribute("updateCommentForm") UpdateCommentForm updateCommentForm,
                                    @RequestParam(value = "clickReply", required = false) Long parentCommentId,
                                    HttpServletRequest request, HttpServletResponse response) {
        //조회수 증가
        viewUpdater.addView(id, request, response);

        model.addAttribute("nickName", nickName); // 수정버튼 본인확인

        ReadBulletinBoardForm readForm = bulletinBoardService.readBoard(id);
        model.addAttribute("readForm", readForm);

        if (!readForm.getImages().isEmpty()) {
            model.addAttribute("firstImage", readForm.getImages().get(0));
            model.addAttribute("imageSize", readForm.getImages().size());
        }

        boolean checkLike = likeService.checkLike(id, nickName);
        model.addAttribute("checkLike", checkLike);

        List<CommentForm> commentForms = commentService.readComments(id);
        if (commentForms != null) {
            model.addAttribute("commentForms", commentForms);
        }
        // TODO: 2023-03-28 게시글 조회 시 한 번에 가져와서 boardDto 에서 다 해결하도록 수정하기! 그리고 이 부분은 자세히 기록해서 포트폴리오 소스로 활용하자. 이 부분 말고도 성능 개선해야할 부분 많네

        //댓글
        CommentForm commentForm = new CommentForm();
        model.addAttribute("commentForm", commentForm);

        //답글
        model.addAttribute("clickReply", parentCommentId);

        return "bulletinBoard/detail";
    }


    /***  update  ***/
    @GetMapping("/boards/{id}/like")
    public String clickLike(@PathVariable("id") Long id,
                              @SessionAttribute(name = LOGIN_USER) String nickName) {
        likeService.clickLike(id, nickName);
        return "redirect:/boards/{id}";
    }

    @GetMapping("/boards/{id}/edit")
    public String updateBulletinBoardForm(@PathVariable("id") Long id, Model model,
                                          @SessionAttribute(name = LOGIN_USER) String nickName) {
        //작성자 본인만 수정 가능
        ReadBulletinBoardForm readForm = bulletinBoardService.readBoard(id);
        if (!readForm.getReadUserForm().getNickName().equals(nickName)) {
            return "redirect:/boards/{id}";
        }
        UpdateBulletinBoardForm updateBulletinBoardForm = bulletinBoardService.getUpdateForm(id);
        model.addAttribute("updateForm", updateBulletinBoardForm);
        model.addAttribute("nickName", nickName);
        return "bulletinBoard/update";
    }

    @PostMapping("/boards/{id}/edit")
    public String updateBulletinBoard(@Validated @ModelAttribute("updateForm") UpdateBulletinBoardForm updateForm,
                                      BindingResult bindingResult, @SessionAttribute(name = LOGIN_USER) String nickName) {
        //작성자 본인만 수정 가능
        ReadBulletinBoardForm readForm = bulletinBoardService.readBoard(updateForm.getId());
        if (!readForm.getReadUserForm().getNickName().equals(nickName)) {
            return "redirect:/boards/{id}";
        }

        if (bindingResult.hasErrors()) {
            return "bulletinBoard/update";
        }

        bulletinBoardService.updateBoard(updateForm);
        return "redirect:/boards/{id}";
    }


    /***  delete  ***/
    @GetMapping("/boards/{id}/delete")
    public String deleteBulletinBoardForm(@PathVariable("id") Long id, Model model,
                                          @SessionAttribute(name = LOGIN_USER) String nickName) {
        //작성자 본인만 접근 가능
        ReadBulletinBoardForm readForm = bulletinBoardService.readBoard(id);
        if (!readForm.getReadUserForm().getNickName().equals(nickName)) {
            return "redirect:/boards/{id}";
        }
        String boardTitle = readForm.getTitle();
        model.addAttribute("boardId", id);
        model.addAttribute("boardTitle", boardTitle);
        model.addAttribute("nickName", nickName);
        return "bulletinBoard/delete";
    }

    @PostMapping("/boards/{id}/delete")
    public String deleteBulletinBoard(@PathVariable("id") Long id,
                                      @SessionAttribute(name = LOGIN_USER) String nickName) {
        //작성자 본인만 삭제 가능
        ReadBulletinBoardForm readForm = bulletinBoardService.readBoard(id);
        if (!readForm.getReadUserForm().getNickName().equals(nickName)) {
            return "redirect:/boards/{id}";
        }
        bulletinBoardService.deleteBoard(id);
        return "redirect:/boards?page=1";
    }


}
