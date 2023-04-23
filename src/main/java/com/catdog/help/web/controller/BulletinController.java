package com.catdog.help.web.controller;

import com.catdog.help.service.BulletinService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.form.bulletinboard.PageBulletinForm;
import com.catdog.help.web.form.bulletinboard.ReadBulletinForm;
import com.catdog.help.web.form.bulletinboard.SaveBulletinForm;
import com.catdog.help.web.form.bulletinboard.UpdateBulletinForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.UpdateCommentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BulletinBoardController {

    private final BulletinService bulletinService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final ViewUpdater viewUpdater;


    /***  create  ***/
    @GetMapping("/boards/new")
    public String createBulletinBoardForm(@SessionAttribute(name = LOGIN_USER) String nickname, Model model) {
        SaveBulletinForm saveBoardForm = new SaveBulletinForm();
        model.addAttribute("nickname", nickname);
        model.addAttribute("saveBoardForm", saveBoardForm);
        return "bulletinBoard/create";
    }

    @PostMapping("/boards/new")
    public String createBulletinBoard(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                                      @Validated @ModelAttribute("saveBoardForm") SaveBulletinForm saveBoardForm,
                                      BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        model.addAttribute("nickname", nickname);
        if (bindingResult.hasErrors()) {
            return "bulletinBoard/create";
        }

        Long boardId = bulletinService.save(saveBoardForm, nickname);
        redirectAttributes.addAttribute("id", boardId);
        return "redirect:/boards/{id}";
    }


    /***  read  ***/
    @GetMapping("/boards")
    public String BulletinBoardList(@RequestParam(value = "page") int page, Model model) {
        Page<PageBulletinForm> pageBoardForms = bulletinService.getPage(page);
        model.addAttribute("pageBoardForms", pageBoardForms.getContent()); // TODO: 2023-04-23 일단 getContent 함. 서비스 테스트 끝나고 Page에 맞게 수정

        int lastPage = bulletinService.countPages();
        model.addAttribute("lastPage", lastPage);
        return "bulletinBoard/list";
    }

    @GetMapping("/boards/{id}")
    public String readBulletinBoard(@PathVariable("id") Long id, Model model,
                                    @SessionAttribute(name = LOGIN_USER) String nickname,
                                    @ModelAttribute("updateCommentForm") UpdateCommentForm updateCommentForm,
                                    @RequestParam(value = "clickReply", required = false) Long parentCommentId,
                                    HttpServletRequest request, HttpServletResponse response) {
        //조회수 증가
        viewUpdater.addView(id, request, response);

        model.addAttribute("nickname", nickname); // 수정버튼 본인확인

        ReadBulletinForm readForm = bulletinService.read(id);
        model.addAttribute("readForm", readForm);

        if (!readForm.getImages().isEmpty()) {
            model.addAttribute("firstImage", readForm.getImages().get(0));
            model.addAttribute("imageSize", readForm.getImages().size());
        }

        boolean checkLike = likeService.checkLike(id, nickname);
        model.addAttribute("checkLike", checkLike);

        List<CommentForm> commentForms = commentService.readComments(id);
        if (!commentForms.isEmpty()) {
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
                              @SessionAttribute(name = LOGIN_USER) String nickname) {
        likeService.clickLike(id, nickname);
        return "redirect:/boards/{id}";
    }

    @GetMapping("/boards/{id}/edit")
    public String updateBulletinBoardForm(@PathVariable("id") Long id, Model model,
                                          @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        ReadBulletinForm readForm = bulletinService.read(id);
        if (!readForm.getNickname().equals(nickname)) {
            return "redirect:/boards/{id}";
        }
        UpdateBulletinForm updateBulletinForm = bulletinService.getUpdateForm(id);
        model.addAttribute("updateForm", updateBulletinForm);
        model.addAttribute("nickname", nickname);
        return "bulletinBoard/update";
    }

    @PostMapping("/boards/{id}/edit")
    public String updateBulletinBoard(@Validated @ModelAttribute("updateForm") UpdateBulletinForm updateForm,
                                      BindingResult bindingResult, @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        ReadBulletinForm readForm = bulletinService.read(updateForm.getId());
        if (!readForm.getNickname().equals(nickname)) {
            return "redirect:/boards/{id}";
        }

        if (bindingResult.hasErrors()) {
            return "bulletinBoard/update";
        }

        bulletinService.update(updateForm);
        return "redirect:/boards/{id}";
    }


    /***  delete  ***/
    @GetMapping("/boards/{id}/delete")
    public String deleteBulletinBoardForm(@PathVariable("id") Long id, Model model,
                                          @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 접근 가능
        ReadBulletinForm readForm = bulletinService.read(id);
        if (!readForm.getNickname().equals(nickname)) {
            return "redirect:/boards/{id}";
        }
        String boardTitle = readForm.getTitle();
        model.addAttribute("boardId", id);
        model.addAttribute("boardTitle", boardTitle);
        model.addAttribute("nickname", nickname);
        return "bulletinBoard/delete";
    }

    @PostMapping("/boards/{id}/delete")
    public String deleteBulletinBoard(@PathVariable("id") Long id,
                                      @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 삭제 가능
        ReadBulletinForm readForm = bulletinService.read(id);
        if (!readForm.getNickname().equals(nickname)) {
            return "redirect:/boards/{id}";
        }
        bulletinService.delete(id);
        return "redirect:/boards?page=1";
    }


}