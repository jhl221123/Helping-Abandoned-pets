package com.catdog.help.web.form.lost;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
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
@NoArgsConstructor
public class EditLostForm {

    @NotNull
    private Long id;

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
    private LocalDate lostDate;

    @NotBlank
    @Length(max = 30)
    private String lostPlace;

    @NotNull
    @PositiveOrZero
    private int gratuity;

    private List<ReadImageForm> oldImages = new ArrayList<>();

    @Size(max = 5)
    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Long> deleteImageIds = new ArrayList<>();


    public EditLostForm(Lost lost, List<ReadImageForm> oldImages) {
        this.id = lost.getId();;
        this.title = lost.getTitle();
        this.content = lost.getContent();
        this.oldImages = oldImages;
        this.region = lost.getRegion();
        this.breed = lost.getBreed();
        this.lostDate = lost.getLostDate();
        this.lostPlace = lost.getLostPlace();
        this.gratuity = lost.getGratuity();
    }
}
