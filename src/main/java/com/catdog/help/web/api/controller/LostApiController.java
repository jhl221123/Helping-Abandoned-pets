package com.catdog.help.web.api.controller;

import com.catdog.help.service.BoardService;
import com.catdog.help.service.CommentService;
import com.catdog.help.service.LostService;
import com.catdog.help.web.api.Base64Image;
import com.catdog.help.web.api.request.lost.SaveLostRequest;
import com.catdog.help.web.api.response.comment.CommentResponse;
import com.catdog.help.web.api.response.lost.ReadLostResponse;
import com.catdog.help.web.api.response.lost.SaveLostResponse;
import com.catdog.help.web.controller.ViewUpdater;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;
import static java.util.stream.Collectors.toList;

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
}
