package com.catdog.help.web.dto;

import com.catdog.help.domain.Gender;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter @Setter
public class UserDto {

    @NotNull
    private Long id;

    @NotBlank
    private String emailId;

    @NotBlank
    @Length(min = 8, max = 16)
    private String password;

    @NotBlank
    private String name;

    @NotNull
    private int age;

    @NotNull
    private Gender gender;

    @NotNull
    private int reliability;

    @NotNull
    private LocalDateTime joinDate; // TODO: 2023-03-05 날짜 모음 값타입으로 수정
}
