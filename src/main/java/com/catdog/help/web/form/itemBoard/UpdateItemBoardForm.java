package com.catdog.help.web.form.itemBoard;

import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class UpdateItemBoardForm {

    @NotNull
    private Long id;

    private User user;

    @NotBlank
    @Length(max = 25)
    private String title;

    @NotBlank
    @Length(max = 250)
    private String content;

    @NotBlank
    @Length(max = 15)
    private String itemName;

    @NotNull
    @Max(10000)
    private int price;

    private UploadFile oldLeadImage;

    private MultipartFile newLeadImage;

    private List<UploadFile> oldImages = new ArrayList<>();

    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Integer> deleteImageIds = new ArrayList<>();
}
