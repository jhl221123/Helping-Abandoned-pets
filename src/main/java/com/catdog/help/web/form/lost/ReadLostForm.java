package com.catdog.help.web.form.lost;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReadLostForm {
    private Long id;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private String region;
    private String breed;
    private LocalDateTime lostDate;
    private String lostPlace;
    private int gratuity;
    private List<ReadImageForm> images;
    private int views;


    public ReadLostForm(Lost board, List<ReadImageForm> imageForms) {
        this.id = board.getId();
        this.nickname = board.getUser().getNickname();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdDate = board.getCreatedDate();
        this.views = board.getViews();
        this.region = board.getRegion();
        this.breed = board.getBreed();
        this.lostDate = board.getLostDate();
        this.lostPlace = board.getLostPlace();
        this.gratuity = board.getGratuity();
        this.images = imageForms;
    }
}
