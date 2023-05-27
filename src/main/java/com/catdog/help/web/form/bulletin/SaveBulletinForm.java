package com.catdog.help.web.form.bulletin;

import com.catdog.help.web.api.request.bulletin.SaveBulletinRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SaveBulletinForm {

    @NotBlank
    @Length(max = 40)
    private String title;

    @NotBlank @Lob
    @Length(max = 1000)
    private String content;

    @NotBlank
    private String region;

    @Size(max = 5)
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

    public SaveBulletinForm(SaveBulletinRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.region = request.getRegion();
        this.images = request.getImages();
    }
}
