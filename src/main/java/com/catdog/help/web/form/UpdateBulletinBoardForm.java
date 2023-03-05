package com.catdog.help.web.form;

import com.catdog.help.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter @Setter
public class UpdateBulletinBoardForm {

    @NotNull
    private Long id;

    private User user;

    @NotBlank
    private String region;

    @NotBlank
    @Length(max = 25)
    private String title;

    @NotBlank
    @Length(max = 3000)
    private String content;

    private String image; //Blob으로 교체!
    private LocalDateTime writeDate;
    private int score;
}
