package com.catdog.help.web.api.controller;

import com.catdog.help.exception.NotAuthorizedException;
import com.catdog.help.service.BoardService;
import com.catdog.help.service.BulletinService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LikeService;
import com.catdog.help.web.api.Base64Image;
import com.catdog.help.web.api.request.bulletin.EditBulletinRequest;
import com.catdog.help.web.api.request.bulletin.SaveBulletinRequest;
import com.catdog.help.web.api.response.bulletin.PageBulletinResponse;
import com.catdog.help.web.api.response.bulletin.ReadBulletinResponse;
import com.catdog.help.web.api.response.bulletin.SaveBulletinResponse;
import com.catdog.help.web.controller.ViewUpdater;
import com.catdog.help.web.form.bulletin.EditBulletinForm;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.search.BulletinSearch;
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
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bulletins")
public class BulletinApiController {

    private final BulletinService bulletinService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final ViewUpdater viewUpdater;
    private final BoardService boardService;


    /**    create    **/
    @PostMapping("/new")
    public SaveBulletinResponse save(@RequestBody @Validated SaveBulletinRequest request) {
        Long boardId = bulletinService.save(new SaveBulletinForm(request), request.getNickname());
        return new SaveBulletinResponse(boardId);
    }

    /***  read  ***/
    @GetMapping
    public PageBulletinResponse getPage(@RequestBody BulletinSearch search,
                                        @PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        Page<PageBulletinForm> pageForms = bulletinService.search(search, pageable);
        return PageBulletinResponse.builder()
                .content(pageForms.getContent())
                .page(pageForms.getPageable().getPageNumber())
                .size(pageForms.getPageable().getPageSize())
                .totalElements(pageForms.getTotalElements())
                .totalPages(pageForms.getTotalPages())
                .build();
    }

    @GetMapping("/{id}")
    public ReadBulletinResponse readBoard(@PathVariable("id") Long id, @SessionAttribute(name = LOGIN_USER) String nickname,
                                          HttpServletRequest request, HttpServletResponse response) {
        //조회수 증가
        viewUpdater.addView(id, request, response);

        ReadBulletinForm readForm = bulletinService.read(id);
        boolean checkLike = likeService.isLike(id, nickname);
        List<CommentForm> commentForms = commentService.readByBoardId(id);
        return ReadBulletinResponse.builder()
                .form(readForm)
                .checkLike(checkLike)
                .commentForms(commentForms)
                .build();
    }

    /***  update  ***/
    @PostMapping("/{id}/like")
    public void clickLike(@PathVariable("id") Long id, @SessionAttribute(name = LOGIN_USER) String nickname) {
        likeService.clickLike(id, nickname);
    }

    @PostMapping("/{id}/edit")
    public void editBoard(@Validated @RequestBody EditBulletinRequest request) {
        if (!isWriter(request.getId(), request.getNickname())) {
            throw new NotAuthorizedException(BULLETIN);
        } //작성자 본인만 수정 가능
        EditBulletinForm form = new EditBulletinForm(request);
        List<MultipartFile> newImages = getMultipartFiles(request.getBase64Images());
        form.addNewImages(newImages);
        bulletinService.update(form);
    }

    /***  delete  ***/
    @PostMapping("/{id}/delete")
    public void deleteBoard(@PathVariable("id") Long id,
                            @SessionAttribute(name = LOGIN_USER) String nickname) {
        if (!isWriter(id, nickname)) {
            throw new NotAuthorizedException(BULLETIN);
        }//작성자 본인만 삭제 가능
        bulletinService.delete(id);
    }


    private List<MultipartFile> getMultipartFiles(List<Base64Image> base64Images) {
        List<MultipartFile> newImages = new ArrayList<>();
        for (Base64Image base64Image : base64Images) {
            String fileName = base64Image.getOriginalName();
            String base64File = base64Image.getBase64File();

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodedBytes = decoder.decode(base64File.getBytes());

            MockMultipartFile mockMultipartFile = new MockMultipartFile("image", fileName, "image/png", decodedBytes);
            newImages.add(mockMultipartFile);
        }
        return newImages;
    }

    private Boolean isWriter(Long id, String nickname) {
        String writer = boardService.getWriter(id);
        return writer.equals(nickname) ? true : false;
    }
}
