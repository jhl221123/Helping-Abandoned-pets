package com.catdog.help.web.form.lost;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SaveLostForm {

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank @Lob
    @Length(max = 250)
    private String content;

    @NotBlank
    private String region;

    @NotBlank
    @Length(max = 15)
    private String breed;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lostDate;

    @NotBlank
    @Length(max = 30)
    private String lostPlace;

    @NotNull
    @PositiveOrZero
    private int gratuity;

    @Size(max = 5)
    private List<MultipartFile> images = new ArrayList<>();


    @Builder
    public SaveLostForm(String title, String content, String region, String breed, LocalDate lostDate, String lostPlace, int gratuity, List<MultipartFile> images) {
        this.title = title;
        this.content = content;
        this.region = region;
        this.breed = breed;
        this.lostDate = lostDate;
        this.lostPlace = lostPlace;
        this.gratuity = gratuity;
        this.images = images;
    }
}
