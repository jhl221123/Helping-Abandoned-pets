package com.catdog.help.web.api.response.user;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.web.form.user.ReadUserForm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReadUserResponse {

    private Long id;

    private String emailId;

    private String nickname;

    private String name;

    private int age;

    private Gender gender;

    private LocalDateTime createdDate;

    private Long lostSize;

    private Long bulletinSize;

    private Long itemSize;

    private Long inquirySize;

    private Long likeBulletinSize;

    private Long likeItemSize;


    @Builder
    public ReadUserResponse(ReadUserForm form) {
        this.id = form.getId();
        this.emailId = form.getEmailId();
        this.nickname = form.getNickname();
        this.name = form.getName();
        this.age = form.getAge();
        this.gender = form.getGender();
        this.createdDate = form.getCreatedDate();
        this.lostSize = form.getLostSize();
        this.bulletinSize = form.getBulletinSize();
        this.itemSize = form.getItemSize();
        this.inquirySize = form.getInquirySize();
        this.likeBulletinSize = form.getLikeBulletinSize();
        this.likeItemSize = form.getLikeItemSize();
    }
}
