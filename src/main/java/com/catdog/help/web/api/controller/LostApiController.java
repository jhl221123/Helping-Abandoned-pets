package com.catdog.help.web.api.controller;

import com.catdog.help.service.LostService;
import com.catdog.help.web.api.Base64Image;
import com.catdog.help.web.api.request.lost.SaveLostRequest;
import com.catdog.help.web.api.response.lost.SaveLostResponse;
import com.catdog.help.web.form.lost.SaveLostForm;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;

@RestController
@RequestMapping("/api/lost")
@RequiredArgsConstructor
public class LostApiController {

    private final LostService lostService;


    /**    create    **/
    @PostMapping("/new")
    public SaveLostResponse save(@SessionAttribute(LOGIN_USER) String nickname,
                                 @RequestBody @Validated SaveLostRequest request) {
        SaveLostForm form = new SaveLostForm(request);
        form.addImages(getMultipartFiles(request.getImages()));
        Long boardId = lostService.save(form, nickname);

        return new SaveLostResponse(boardId);
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
