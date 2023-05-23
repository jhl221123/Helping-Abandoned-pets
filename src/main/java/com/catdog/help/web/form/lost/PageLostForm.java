package com.catdog.help.web.form.lost;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PageLostForm {

    private Long id;
    private ReadImageForm leadImage;
    private String region;
    private String breed;
    private LocalDateTime lostDate;
    private String lostPlace;
    private int gratuity;


    public PageLostForm(Lost board, ReadImageForm leadImage) {
        this.id = board.getId();
        this.leadImage = leadImage;
        this.region = board.getRegion();
        this.breed = board.getBreed();
        this.lostDate = board.getLostDate();
        this.lostPlace = board.getLostPlace();
        this.gratuity = board.getGratuity();
    }
}
