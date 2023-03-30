package com.catdog.help.web.form.bulletinboard;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @Length(max = 250)
    private String content;

    private List<UploadFile> oldImages = new ArrayList<>();
    private List<MultipartFile> newImages = new ArrayList<>();
    private List<Integer> deleteImageIds = new ArrayList<>();

    private Dates dates;
}
