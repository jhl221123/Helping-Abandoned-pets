package com.catdog.help.web.form.inquiry;

import com.catdog.help.domain.board.SecretStatus;
import com.catdog.help.web.api.request.inquiry.SaveInquiryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
public class SaveInquiryForm {

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private SecretStatus secret;


    public SaveInquiryForm(SaveInquiryRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.secret = request.getSecret();
    }
}
