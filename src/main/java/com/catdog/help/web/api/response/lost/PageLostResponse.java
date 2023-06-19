package com.catdog.help.web.api.response.lost;

import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.PageLostForm;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PageLostResponse {

    private Long id;
    private ReadImageForm leadImage;
    private String region;
    private String breed;

    @JsonFormat(pattern = "yyyy-MM-DD")
    private LocalDate lostDate;
    private String lostPlace;
    private int gratuity;


    @Builder
    public PageLostResponse(PageLostForm form) {
        this.id = form.getId();
        this.leadImage = form.getLeadImage();
        this.region = form.getRegion();
        this.breed = form.getBreed();
        this.lostDate = form.getLostDate();
        this.lostPlace = form.getLostPlace();
        this.gratuity = form.getGratuity();
    }
}
