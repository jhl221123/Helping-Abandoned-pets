package com.catdog.help.web.form.lost;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Lob;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
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

    @NotBlank
    private LocalDateTime lostDate;

    @NotBlank
    @Length(max = 30)
    private String lostPlace;

    @NotNull
    @PositiveOrZero
    private int gratuity;

    @Size(max = 5)
    private List<MultipartFile> images = new ArrayList<>();


    @Builder
    public SaveLostForm(String title, String content, String region, String breed, LocalDateTime lostDate, String lostPlace, int gratuity, List<MultipartFile> images) {
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
