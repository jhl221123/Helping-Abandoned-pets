package com.catdog.help.web.api.request.bulletin;

import com.catdog.help.web.api.Base64Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveBulletinRequest {

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank @Lob
    @Length(max = 250)
    private String content;

    @NotBlank
    private String region; // TODO: 2023-05-18 지역이름 검증 들어가야겠는걸

    @Size(max = 5)
    private List<Base64Image> base64Images = new ArrayList<>();


    @Builder
    public SaveBulletinRequest(String title, String content, String region, List<Base64Image> base64Images) {
        this.title = title;
        this.content = content;
        this.region = region;
        this.base64Images = base64Images;
    }
}
