package com.catdog.help.web.api.response.lost;

import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.PageLostForm;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PageLostResponse {

    private Long id;
    private ReadImageForm leadImage;
    private String region;
    private String breed;
    private LocalDate lostDate;
    private String lostPlace;
    private int gratuity;


    public PageLostResponse(PageLostForm form, ReadImageForm leadImage) {
        this.id = form.getId();
        this.leadImage = leadImage;
        this.region = form.getRegion();
        this.breed = form.getBreed();
        this.lostDate = form.getLostDate();
        this.lostPlace = form.getLostPlace();
        this.gratuity = form.getGratuity();
    }
}
