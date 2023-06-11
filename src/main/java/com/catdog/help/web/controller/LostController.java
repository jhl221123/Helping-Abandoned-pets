package com.catdog.help.web.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.service.LostService;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.lost.EditLostForm;
import com.catdog.help.web.form.lost.PageLostForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import com.catdog.help.web.form.search.LostSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static com.catdog.help.domain.board.RegionConst.*;
import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Slf4j
@Controller
@RequestMapping("/lost")
@RequiredArgsConstructor
public class LostController {

    private final LostService lostService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final ViewUpdater viewUpdater;
    private final BoardService boardService;


    /***  create  ***/
    @GetMapping("/new")
    public String getSaveForm(Model model) {
        SaveLostForm saveForm = SaveLostForm.builder().build();
        model.addAttribute("saveForm", saveForm);

        List<String> regions = getRegions();
        model.addAttribute("regions", regions);
        return "lost/create";
    }

    @PostMapping("/new")
    public String saveBoard(@SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                            @Validated @ModelAttribute("saveForm") SaveLostForm saveForm,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            List<String> regions = getRegions();
            model.addAttribute("regions", regions);
            return "lost/create";
        }

        MultipartFile uploadImage = saveForm.getImages().get(0);
        if (uploadImage.isEmpty()) {
            List<String> regions = getRegions();
            model.addAttribute("regions", regions);
            bindingResult.rejectValue("images", "empty", "하나 이상의 이미지를 업로드 해야합니다.");
            return "lost/create";
        }

        Long boardId = lostService.save(saveForm, nickname);
        redirectAttributes.addAttribute("id", boardId);
        return "redirect:/lost/{id}";
    }


    /***  read  ***/
    @GetMapping
    public String getPage(@PageableDefault(size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                          @ModelAttribute("lostSearch") LostSearch search, Model model) {
        Page<PageLostForm> pageForms = lostService.search(search, pageable);

        model.addAttribute("pageForms", pageForms.getContent());

        int offset = pageable.getPageNumber() / 5 * 5;
        model.addAttribute("offset", offset);

        int limit = offset + 4;
        int endPage = Math.max(pageForms.getTotalPages() - 1, 0);
        int lastPage = getLastPage(limit, endPage);
        model.addAttribute("lastPage", lastPage);

        boolean isEnd = false;
        if (lastPage == endPage) {
            isEnd = true;
        }
        model.addAttribute("isEnd", isEnd);

        return "lost/list";
    }

    @GetMapping("/{id}")
    public String readBoard(@PathVariable("id") Long id, Model model,
                            @SessionAttribute(name = LOGIN_USER) String nickname,
                            HttpServletRequest request, HttpServletResponse response) {
        // TODO: 2023-04-26 bindingResult 이용해서 뷰템플릿에 오류 보이도록 만들자.

        //조회수 증가
        viewUpdater.addView(id, request, response);

        ReadLostForm readForm = lostService.read(id);
        model.addAttribute("readForm", readForm);

        if (!readForm.getImages().isEmpty()) {
            model.addAttribute("firstImage", readForm.getImages().get(0));
            model.addAttribute("imageSize", readForm.getImages().size());
        }

        List<CommentForm> commentForms = commentService.readByBoardId(id);
        if (!commentForms.isEmpty()) {
            model.addAttribute("commentForms", commentForms);
        }
        // TODO: 2023-03-28 게시글 조회 시 한 번에 가져와서 boardDto 에서 다 해결하도록 수정하기! 그리고 이 부분은 자세히 기록해서 포트폴리오 소스로 활용하자. 이 부분 말고도 성능 개선해야할 부분 많네

        //댓글
        CommentForm commentForm = new CommentForm();
        model.addAttribute("commentForm", commentForm);

        return "lost/detail";
    }


    /***  update  ***/

    @GetMapping("/{id}/edit")
    public String getEditForm(@PathVariable("id") Long id, Model model,
                              @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }
        EditLostForm editForm = lostService.getEditForm(id);
        model.addAttribute("editForm", editForm);

        List<String> regions = getRegions();
        model.addAttribute("regions", regions);
        return "lost/edit";
    }

    @PostMapping("/{id}/edit")
    public String editBoard(@PathVariable("id") Long id, @SessionAttribute(name = LOGIN_USER) String nickname, Model model,
                            @Validated @ModelAttribute("editForm") EditLostForm editForm, BindingResult bindingResult) {
        //작성자 본인만 수정 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            EditLostForm form = lostService.getEditForm(id);
            editForm.addOldLeadImage(form.getOldLeadImage());
            editForm.addOldImages(form.getOldImages());

            List<String> regions = getRegions();
            model.addAttribute("regions", regions);
            return "lost/edit";
        }

        lostService.update(editForm);
        return "redirect:/lost/{id}";
    }


    /***  delete  ***/
    @GetMapping("/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id,
                              @SessionAttribute(name = LOGIN_USER) String nickname) {
        //작성자 본인만 삭제 가능
        if (!isWriter(id, nickname)) {
            return "redirect:/";
        }

        lostService.delete(id);
        return "redirect:/lost?page=0";
    }


    private List<String> getRegions() {
        return Arrays.asList(SEOUL, BUSAN, INCHEON, DAEJEON, DAEGU, ULSAN, GWANGJU, SEJONG,
                GYEONGGI, GANGWON, CHUNGBUK, CHUNGNAM, JEONBUK, JEONNAM, GYEONGBUK, GYEONGNAM, JEJU);
    }

    private int getLastPage(int limit, int endPage) {
        int lastPage = Math.min(endPage, limit);
        if(lastPage<0) lastPage = 0;
        return lastPage;
    }

    private Boolean isWriter(Long id, String nickname) {
        String writer = boardService.getWriter(id);
        return writer.equals(nickname) ? true : false;
    }
}

