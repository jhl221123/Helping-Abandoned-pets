package com.catdog.help.web.form.message;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class SaveMessageForm {

    @NotBlank
    private String content;

    public SaveMessageForm() {
    }

    public SaveMessageForm(String content) {
        this.content = content;
    }
}
