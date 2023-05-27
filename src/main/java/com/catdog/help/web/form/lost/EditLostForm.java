package com.catdog.help.web.form.lost;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

@Getter @Setter
@NoArgsConstructor
public class EditLostForm {

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

    private ReadImageForm oldLeadImage;

    private MultipartFile newLeadImage;

    private List<ReadImageForm> oldImages = new ArrayList<>();

    @Size(max = 5)
    private List<MultipartFile> newImages = new ArrayList<>();

    private List<Long> deleteImageIds = new ArrayList<>();


    public EditLostForm(Lost lost, List<ReadImageForm> readForms) {
        this.id = lost.getId();;
        this.title = lost.getTitle();
        this.content = lost.getContent();
        this.region = lost.getRegion();
        this.breed = lost.getBreed();
        this.lostDate = lost.getLostDate();
        this.lostPlace = lost.getLostPlace();
        this.gratuity = lost.getGratuity();

        //대표이미지
        this.oldLeadImage = readForms.get(0);
        for (int i = 1; i < readForms.size(); i++) {
            //대표이미지 제외
            this.getOldImages().add(readForms.get(i));
        }
    }


    public void addOldImages(List<ReadImageForm> oldImages) {
        this.oldImages = oldImages;
    }
}
