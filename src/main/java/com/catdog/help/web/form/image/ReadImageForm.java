package com.catdog.help.web.form.image;

import com.catdog.help.domain.board.UploadFile;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReadImageForm {

    private Long id;

    private String uploadFileName;

    private String storeFileName;

    public ReadImageForm(UploadFile uploadFile) {
        this.id = uploadFile.getId();
        this.uploadFileName = uploadFile.getUploadFileName();
        this.storeFileName = uploadFile.getStoreFileName();
    }
}
