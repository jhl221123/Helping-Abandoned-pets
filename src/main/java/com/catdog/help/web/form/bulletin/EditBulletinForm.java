package com.catdog.help.web.form.bulletin;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EditBulletinForm {

    @NotNull
    private Long id;

    @NotBlank
    private String region;

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank
    @Length(max = 250)
    private String content;

    private List<ReadImageForm> oldImages = new ArrayList<>();

    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Long> deleteImageIds = new ArrayList<>();


    public EditBulletinForm() { //컨트롤러 파라미터 바인딩
    }

    public EditBulletinForm(Bulletin board, List<ReadImageForm> oldImages) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.oldImages = oldImages;
        this.region = board.getRegion();
    }
}
