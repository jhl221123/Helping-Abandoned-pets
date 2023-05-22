package com.catdog.help.web.api.request.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickname;

    @NotBlank
    @Length(min = 8, max = 16)
    private String beforePassword;

    @NotBlank
    @Length(min = 8, max = 16)
    private String afterPassword;

    @NotBlank
    @Length(min = 8, max = 16)
    private String checkPassword;


    @Builder
    public ChangePasswordRequest(String nickname, String beforePassword, String afterPassword, String checkPassword) {
        this.nickname = nickname;
        this.beforePassword = beforePassword;
        this.afterPassword = afterPassword;
        this.checkPassword = checkPassword;
    }
}
