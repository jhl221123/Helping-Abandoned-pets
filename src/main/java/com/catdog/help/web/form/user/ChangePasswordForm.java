package com.catdog.help.web.form.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor
public class ChangePasswordForm {

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
    public ChangePasswordForm(String beforePassword, String afterPassword, String checkPassword) {
        this.beforePassword = beforePassword;
        this.afterPassword = afterPassword;
        this.checkPassword = checkPassword;
    }
}
