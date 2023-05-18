package com.catdog.help.web.api.request.bulletin;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveBulletinRequest {

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickname;

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank @Lob
    @Length(max = 250)
    private String content;

    @NotBlank
    private String region; // TODO: 2023-05-18 지역이름 검증 들어가야겠는걸

    @Size(max = 5)
    private List<MultipartFile> images = new ArrayList<>();


    @Builder
    public SaveBulletinRequest(String nickname, String title, String content, String region, List<MultipartFile> images) {
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.region = region;
        this.images = images;
    }
}
