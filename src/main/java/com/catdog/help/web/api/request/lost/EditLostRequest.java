package com.catdog.help.web.api.request.lost;

import com.catdog.help.web.api.Base64Image;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class EditLostRequest {

    @NotNull
    private Long id;

    @NotBlank
    @Length(max = 40)
    private String title;

    @NotBlank @Lob
    @Length(max = 1000)
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

    private Base64Image newLeadImage;

    @Size(max = 5)
    private List<Base64Image> newImages;

    private List<Long> deleteImageIds;


    @Builder
    public EditLostRequest(Long id, String title, String content, String region, String breed, LocalDate lostDate, String lostPlace,
                           int gratuity, Base64Image newLeadImage, List<Base64Image> newImages, List<Long> deleteImageIds) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.region = region;
        this.breed = breed;
        this.lostDate = lostDate;
        this.lostPlace = lostPlace;
        this.gratuity = gratuity;
        this.newLeadImage = newLeadImage;
        this.newImages = newImages;
        this.deleteImageIds = deleteImageIds;
    }
}
