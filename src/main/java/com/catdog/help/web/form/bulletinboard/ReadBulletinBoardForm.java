package com.catdog.help.web.form.bulletinboard;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import com.catdog.help.web.form.user.ReadUserForm;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ReadBulletinBoardForm {

    private Long id;
    private ReadUserForm readUserForm;
    private String title;
    private String content;
    private Dates dates;
    private String region;
    private List<ReadUploadFileForm> images = new ArrayList<>();
    private int views;
    private int likeSize;
}
