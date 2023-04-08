package com.catdog.help.web.form.itemboard;

import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class UpdateItemBoardForm {

    @NotNull
    private Long id;

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

    private ReadUploadFileForm oldLeadImage;

    private MultipartFile newLeadImage;

    private List<ReadUploadFileForm> oldImages = new ArrayList<>();

    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Integer> deleteImageIds = new ArrayList<>();
}
