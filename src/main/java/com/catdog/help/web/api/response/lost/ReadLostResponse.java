package com.catdog.help.web.api.response.lost;

import com.catdog.help.web.api.response.comment.CommentResponse;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReadLostResponse {

    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private String region;
    private String breed;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lostDate;
    private String lostPlace;
    private int gratuity;
    private int views;
    private List<ReadImageForm> images;
    private List<CommentResponse> commentResponses;


    @Builder
    public ReadLostResponse(ReadLostForm form, List<CommentResponse> commentResponses) {
        this.id = form.getId();
        this.nickname = form.getNickname();
        this.title = form.getTitle();
        this.content = form.getContent();
        this.createdDate = form.getCreatedDate();
        this.region = form.getRegion();
        this.breed = form.getBreed();
        this.lostDate = form.getLostDate();
        this.lostPlace = form.getLostPlace();
        this.gratuity = form.getGratuity();
        this.images = form.getImages();
        this.views = form.getViews();
        this.commentResponses = commentResponses;
    }
}
