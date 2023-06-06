package com.catdog.help.web.api.request.bulletin;

import com.catdog.help.web.api.Base64Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class EditBulletinRequest {

    @NotNull
    private Long id;

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickname;

    @NotBlank
    @Length(max = 40)
    private String title;

    @NotBlank @Lob
    @Length(max = 1000)
    private String content;

    @NotBlank
    private String region;

    @Size(max = 5)
    private List<Base64Image> base64Images = new ArrayList<>();

    private List<Long> deleteImageIds = new ArrayList<>();


    @Builder
    public EditBulletinRequest(Long id, String nickname, String title, String content, String region, List<Base64Image> base64Images, List<Long> deleteImageIds) {
        this.id = id;
        this.nickname = nickname;
        this.title = title;
        this.content = content;
        this.region = region;
        this.base64Images = base64Images;
        this.deleteImageIds = deleteImageIds;
    }
}
