package com.catdog.help.web.form.bulletinboard;

import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private List<MultipartFile> images = new ArrayList<>();
    private LocalDateTime writeDate;
    private int score;
}
