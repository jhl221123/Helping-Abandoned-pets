package com.catdog.help.web.api.controller;

import com.catdog.help.exception.NotAuthorizedException;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LostService;
import com.catdog.help.web.api.Base64Image;
import com.catdog.help.web.api.request.lost.EditLostRequest;
import com.catdog.help.web.api.request.lost.SaveLostRequest;
import com.catdog.help.web.api.response.comment.CommentResponse;
import com.catdog.help.web.api.response.lost.PageLostResponse;
import com.catdog.help.web.api.response.lost.ReadLostResponse;
import com.catdog.help.web.api.response.lost.SaveLostResponse;
import com.catdog.help.web.controller.ViewUpdater;
import com.catdog.help.web.form.lost.EditLostForm;
import com.catdog.help.web.form.lost.PageLostForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import com.catdog.help.web.form.search.LostSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.catdog.help.MyConst.BULLETIN;
import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;
@Slf4j
@RestController
@RequestMapping("/api/lost")
@RequiredArgsConstructor
public class LostApiController {

    private final LostService lostService;
    private final CommentService commentService;
    private final ViewUpdater viewUpdater;
    private final BoardService boardService;


    /**    create    **/
    @PostMapping("/new")
    public SaveLostResponse save(@SessionAttribute(LOGIN_USER) String nickname,
                                 @RequestBody @Validated SaveLostRequest request) {
        SaveLostForm form = new SaveLostForm(request);
        form.addImages(getMultipartFiles(request.getImages()));
        Long boardId = lostService.save(form, nickname);

        return new SaveLostResponse(boardId);
    }

    /***  read  ***/
    @GetMapping
    public Page<PageLostResponse> getPage(@ModelAttribute LostSearch search,
                                          @PageableDefault(size = 6, sort = "id", direction = DESC) Pageable pageable) {
        Page<PageLostForm> pageForms = lostService.search(search, pageable);
        return pageForms.map(PageLostResponse::new);
    }

    @GetMapping("/{id}")
    public ReadLostResponse readBoard(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        //조회수 증가
        viewUpdater.addView(id, request, response);

        ReadLostForm readForm = lostService.read(id);
        List<CommentResponse> commentResponses = commentService.readByBoardId(id).stream()
                .map(CommentResponse::new)
                .collect(toList());

        return ReadLostResponse.builder()
                .form(readForm)
                .commentResponses(commentResponses)
                .build();
    }

    /***  update  ***/
    @PostMapping("/{id}/edit")
    public void editBoard(@SessionAttribute(value = LOGIN_USER) String nickname,
                          @Validated @RequestBody(required = false) EditLostRequest request) {
        if (!isWriter(request.getId(), nickname)) {
            throw new NotAuthorizedException(BULLETIN);
        } //작성자 본인만 수정 가능

        EditLostForm form = new EditLostForm(request);

        MockMultipartFile newLeadImage = getMultipartFile(request.getNewLeadImage());
        form.addNewLeadImage(newLeadImage);

        List<MultipartFile> newImages = getMultipartFiles(request.getNewImages());
        form.addNewImages(newImages);

        lostService.update(form);
    }


    private List<MultipartFile> getMultipartFiles(List<Base64Image> base64Images) {
        List<MultipartFile> newImages = new ArrayList<>();
        for (Base64Image base64Image : base64Images) {
            newImages.add(getMultipartFile(base64Image));
        }
        return newImages;
    }

    private MockMultipartFile getMultipartFile(Base64Image base64Image) {
        String fileName = base64Image.getOriginalName();
        String base64File = base64Image.getBase64File();
        if (base64File == null) {
            return new MockMultipartFile("name", (byte[]) null);
        }

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(base64File.getBytes());

        return new MockMultipartFile("image", fileName, "image/png", decodedBytes);
    }

    private Boolean isWriter(Long id, String nickname) {
        String writer = boardService.getWriter(id);
        return writer.equals(nickname) ? true : false;
    }
}
