package com.catdog.help.web.form.lost;

import com.catdog.help.domain.board.Lost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PageLostForm {

    private Long id;
    private String region;
    private String breed;
    private LocalDateTime lostDate;
    private String lostPlace;
    private int gratuity;


    public PageLostForm(Lost board) {
        this.id = board.getId();
        this.region = board.getRegion();
        this.breed = board.getBreed();
        this.lostDate = board.getLostDate();
        this.lostPlace = board.getLostPlace();
        this.gratuity = board.getGratuity();
    }
}
