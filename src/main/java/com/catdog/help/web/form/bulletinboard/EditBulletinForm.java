package com.catdog.help.web.form.bulletinboard;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class UpdateBulletinForm {

    @NotNull
    private Long id;

    @NotBlank
    private String region;

    @NotBlank
    @Length(max = 25)
    private String title;

    @NotBlank
    @Length(max = 250)
    private String content;

    private List<ReadUploadFileForm> oldImages = new ArrayList<>();

    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Integer> deleteImageIds = new ArrayList<>();


    public UpdateBulletinForm() { //컨트롤러 파라미터 바인딩
    }

    public UpdateBulletinForm(Bulletin board, List<ReadUploadFileForm> oldImages) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.oldImages = oldImages;
        this.region = board.getRegion();
    }
}
