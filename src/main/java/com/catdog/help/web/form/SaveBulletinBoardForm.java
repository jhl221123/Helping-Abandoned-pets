package com.catdog.help.web.form;

import com.catdog.help.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Getter @Setter
public class SaveBulletinBoardForm {

    @NotBlank
    private String region;

    @NotBlank
    @Length(max = 25)
    private String title;

    @NotBlank
    @Length(max = 3000)
    private String content;

    private String image; //Blob으로 교체!
}
