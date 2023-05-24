package com.catdog.help.domain.board;

import com.catdog.help.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Entity @Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("Lost")
public class Lost extends Board {

    private String region;

    private String breed;

    private LocalDate lostDate;

    private String lostPlace;

    private int gratuity;


    @Builder
    public Lost(User user, String title, String content, String region, String breed, LocalDate lostDate, String lostPlace, int gratuity) {
        super(user, title, content);
        this.region = region;
        this.breed = breed;
        this.lostDate = lostDate;
        this.lostPlace = lostPlace;
        this.gratuity = gratuity;
    }


    public void updateBoard(String title, String content, String region, String breed, LocalDate lostDate, String lostPlace, int gratuity) {
        super.updateBoard(title, content);
        this.region = region;
        this.breed = breed;
        this.lostDate = lostDate;
        this.lostPlace = lostPlace;
        this.gratuity = gratuity;
    }
}
