package com.catdog.help.web.form.uploadfile;

import com.catdog.help.domain.board.UploadFile;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReadUploadFileForm {

    private Long id;

    private String uploadFileName;

    private String storeFileName;

    public ReadUploadFileForm(UploadFile uploadFile) {
        this.id = uploadFile.getId();
        this.uploadFileName = uploadFile.getUploadFileName();
        this.storeFileName = uploadFile.getStoreFileName();
    }
}
