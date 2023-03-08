package com.catdog.help.web.dto;

import com.catdog.help.domain.Gender;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter @Setter
public class UserDto {

    @NotNull
    private Long id;

    @NotBlank
    @Email
    private String emailId;

    @NotBlank
    @Length(min = 8, max = 16)
    private String password;

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickName;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private int age;

    @NotNull
    private String gender;

    @NotNull
    private int reliability;

    @NotNull
    private LocalDateTime joinDate; // TODO: 2023-03-05 날짜 모음 값타입으로 수정
}
