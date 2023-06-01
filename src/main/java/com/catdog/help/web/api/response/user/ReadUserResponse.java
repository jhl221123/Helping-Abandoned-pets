package com.catdog.help.web.api.response.user;

import com.catdog.help.domain.user.Gender;
import com.catdog.help.web.form.user.ReadUserForm;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserResponse {

    private Long id;

    private String emailId;

    private String nickname;

    private String name;

    private int age;

    private Gender gender;

    private Long lostSize;

    private Long bulletinSize;

    private Long itemSize;

    private Long inquirySize;

    private Long likeBulletinSize;

    private Long likeItemSize;


    @Builder
    public ReadUserResponse(ReadUserForm form, Long lostSize, Long bulletinSize, Long itemSize, Long inquirySize, Long likeBulletinSize, Long likeItemSize) {
        this.id = form.getId();
        this.emailId = form.getEmailId();
        this.nickname = form.getNickname();
        this.name = form.getName();
        this.age = form.getAge();
        this.gender = form.getGender();
        this.lostSize = lostSize;
        this.bulletinSize = bulletinSize;
        this.itemSize = itemSize;
        this.inquirySize = inquirySize;
        this.likeBulletinSize = likeBulletinSize;
        this.likeItemSize = likeItemSize;
    }
}
