package com.catdog.help.web.controller;

import com.catdog.help.service.BulletinService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.bulletin.EditBulletinForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.comment.UpdateCommentForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/bulletins")
@RequiredArgsConstructor
public class BulletinController {

    private final BulletinService bulletinService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final ViewUpdater viewUpdater;


    /***  create  ***/
    @GetMapping("/new")
    public String getSaveForm(@SessionAttribute(name = LOGIN_USER) String nickname, Model model) {
        SaveBulletinForm saveForm = new SaveBulletinForm();
        model.addAttribute("nickname", nickname);
        model.addAttribute("saveForm", saveForm);
        return "bulletins/create";
    }

    @PostMapping("/new")
    public String saveBoard(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                            @Validated @ModelAttribute("saveForm") SaveBulletinForm saveForm,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        model.addAttribute("nickname", nickname); // TODO: 2023-04-23 이거 없어도 될텐데?
        if (bindingResult.hasErrors()) {
            return "bulletins/create";
        }

        Long boardId = bulletinService.save(saveForm, nickname);
        redirectAttributes.addAttribute("id", boardId);
        return "redirect:/bulletins/{id}";
    }


    /***  read  ***/
    @GetMapping
    public String getPage(Pageable pageable, Model model) {
        Page<PageBulletinForm> pageForm = bulletinService.getPage(pageable);
        model.addAttribute("pageForm", pageForm.getContent()); // TODO: 2023-04-23 일단 getContent 함. 서비스 테스트 끝나고 Page에 맞게 수정

        int totalPages = pageForm.getTotalPages();
        model.addAttribute("lastPage", totalPages); //
        return "bulletins/list";
    }

    @GetMapping("/{id}")
    public String readBoard(@PathVariable("id") Long id, Model model,
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

        boolean checkLike = likeService.isLike(id, nickname);
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

        return "bulletins/detail";
    }


    /***  update  ***/
    @GetMapping("/{id}/like")
    public String clickLike(@SessionAttribute(name = LOGIN_USER) String nickname,
                            @PathVariable("id") Long id) {
        likeService.clickLike(id, nickname);
        return "redirect:/bulletins/{id}";
    }

    @GetMapping("/{id}/edit")
    public String getEditForm(@SessionAttribute(name = LOGIN_USER) String nickname,
                              @PathVariable("id") Long id, Model model) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }
        EditBulletinForm editForm = bulletinService.getEditForm(id);
        model.addAttribute("updateForm", editForm);
        model.addAttribute("nickname", nickname);
        return "bulletins/edit";
    }

    @PostMapping("/{id}/edit")
    public String editBoard(@SessionAttribute(name = LOGIN_USER) String nickname,
                            @Validated @ModelAttribute("editForm") EditBulletinForm editForm, BindingResult bindingResult) {
        //작성자 본인만 수정 가능
        if (!isWriter(editForm.getId(), nickname)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "bulletins/edit";
        }

        bulletinService.update(editForm);
        return "redirect:/bulletins/{id}";
    }


    /***  delete  ***/
    @GetMapping("/{id}/delete")
    public String getDeleteForm(@PathVariable("id") Long id, Model model,
                                @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 접근 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        ReadBulletinForm form = bulletinService.read(id);
        String boardTitle = form.getTitle();
        model.addAttribute("boardId", id); // TODO: 2023-04-24 한 번에 처리할 수 있도록 해보자.
        model.addAttribute("boardTitle", boardTitle);
        model.addAttribute("nickname", nickname);
        return "bulletins/delete";
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id,
                              @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 삭제 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        bulletinService.delete(id);
        return "redirect:/bulletins?page=0";
    }


    private Boolean isWriter(Long id, String nickname) {
        String writer = bulletinService.getWriter(id);
        return writer.equals(nickname) ? true : false;
    }
}
