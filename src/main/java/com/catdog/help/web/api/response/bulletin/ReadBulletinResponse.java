package com.catdog.help.web.api.response.bulletin;

import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.comment.CommentForm;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReadBulletinResponse {

    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private String region;
    private List<ReadImageForm> images;
    private int views;
    private int likeSize;
    private boolean checkLike;
    private List<CommentForm> commentForms;


    @Builder
    public ReadBulletinResponse(ReadBulletinForm form, boolean checkLike, List<CommentForm> commentForms) {
        this.id = form.getId();
        this.nickname = form.getNickname();
        this.title = form.getTitle();
        this.content = form.getContent();
        this.createdDate = form.getCreatedDate();
        this.region = form.getRegion();
        this.images = form.getImages();
        this.views = form.getViews();
        this.likeSize = form.getLikeSize();
        this.checkLike = checkLike;
        this.commentForms = commentForms;
    }
}
