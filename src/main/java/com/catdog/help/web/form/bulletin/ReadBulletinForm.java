package com.catdog.help.web.form.bulletin;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReadBulletinForm {

    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private String region;
    private List<ReadUploadFileForm> images;
    private int views;
    private int likeSize;


    @Builder
    public ReadBulletinForm(Bulletin board, List<ReadUploadFileForm> imageForms, int likeSize) {
        this.id = board.getId();
        this.nickname = board.getUser().getNickname();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdDate = board.getCreatedDate();
        this.views = board.getViews();
        this. region = board.getRegion();
        this.images = imageForms;
        this.likeSize = likeSize;
    }
}
