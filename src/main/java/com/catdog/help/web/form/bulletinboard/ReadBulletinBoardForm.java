package com.catdog.help.web.form.bulletinboard;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import com.catdog.help.web.form.user.ReadUserForm;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ReadBulletinBoardForm {

    private Long id;
    private String nickname;
    private String title;
    private String content;
    private Dates dates;
    private String region;
    private List<ReadUploadFileForm> images = new ArrayList<>();
    private int views;
    private int likeSize;

    public ReadBulletinBoardForm(BulletinBoard findBoard, String nickname, List<ReadUploadFileForm> readUploadFileForms, int likeSize) {
        this.id = findBoard.getId();
        this.nickname = nickname;
        this.title = findBoard.getTitle();
        this.content = findBoard.getContent();
        this.dates = findBoard.getDates();
        this.views = findBoard.getViews();
        this. region = findBoard.getRegion();
        this.images = readUploadFileForms;
        this.likeSize = likeSize;
    }
}
