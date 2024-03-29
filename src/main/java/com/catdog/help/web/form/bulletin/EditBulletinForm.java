package com.catdog.help.web.form.bulletin;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.web.api.request.bulletin.EditBulletinRequest;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class EditBulletinForm {

    @NotNull
    private Long id;

    @NotBlank
    @Length(max = 40)
    private String title;

    @NotBlank @Lob
    @Length(max = 1000)
    private String content;

    @NotBlank
    private String region;

    private List<ReadImageForm> oldImages = new ArrayList<>();

    @Size(max = 5)
    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Long> deleteImageIds = new ArrayList<>();


    public EditBulletinForm(Bulletin board, List<ReadImageForm> oldImages) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.oldImages = oldImages;
        this.region = board.getRegion();
    }

    public EditBulletinForm(EditBulletinRequest request) {
        this.id = request.getId();
        this.title = request.getTitle();
        this.content = request.getContent();
        this.region = request.getRegion();
        this.deleteImageIds = request.getDeleteImageIds();
    }

    public void addOldImages(List<ReadImageForm> oldImages) {
        this.oldImages = oldImages;
    }

    public void addNewImages(List<MultipartFile> newImages) {
        this.newImages = newImages;
    }
}
