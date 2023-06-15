package com.catdog.help.web.form.user;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
public class ReadUserForm {

    @NotNull
    private Long id;

    @NotBlank
    @Email
    private String emailId;

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickname;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private int age;

    @NotNull
    @Positive
    private Gender gender;

    @NotNull
    @Positive
    private LocalDateTime createdDate;

    @NotNull
    @Positive
    private Long lostSize;

    @NotNull
    @Positive
    private Long bulletinSize;

    @NotNull
    @Positive
    private Long itemSize;

    @NotNull
    @Positive
    private Long inquirySize;

    @NotNull
    @Positive
    private Long likeBulletinSize;

    @NotNull
    private Long likeItemSize;


    @Builder
    public ReadUserForm(User user, Long lostSize, Long bulletinSize, Long itemSize, Long inquirySize, Long likeBulletinSize, Long likeItemSize) {
        this.id = user.getId();
        this.emailId = user.getEmailId();
        this.nickname = user.getNickname();
        this.name = user.getName();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.createdDate = user.getCreatedDate();
        this.lostSize = lostSize;
        this.bulletinSize = bulletinSize;
        this.itemSize = itemSize;
        this.inquirySize = inquirySize;
        this.likeBulletinSize = likeBulletinSize;
        this.likeItemSize = likeItemSize;
    }
}
