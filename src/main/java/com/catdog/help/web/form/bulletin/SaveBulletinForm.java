package com.catdog.help.web.form.bulletin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SaveBulletinForm {

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank
    @Length(max = 250)
    private String content;

    @NotBlank
    private String region;

    private List<MultipartFile> images = new ArrayList<>();


    public SaveBulletinForm() {
    }

    @Builder
    public SaveBulletinForm(String title, String content, String region, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.region = region;
        this.images = images;
    }
}
