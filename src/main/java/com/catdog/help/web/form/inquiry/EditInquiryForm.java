package com.catdog.help.web.form.inquiry;

import com.catdog.help.domain.board.Inquiry;
import com.catdog.help.domain.board.SecretStatus;
import com.catdog.help.web.api.request.inquiry.EditInquiryRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
public class EditInquiryForm {

    @NotNull
    private Long id;

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private SecretStatus secret;


    public EditInquiryForm(Inquiry inquiry) {
        this.id = inquiry.getId();
        this.title = inquiry.getTitle();
        this.content = inquiry.getContent();
        this.secret = inquiry.getSecret();
    }

    public EditInquiryForm(EditInquiryRequest request) {
        this.id = request.getId();
        this.title = request.getTitle();
        this.content = request.getContent();
        this.secret = request.getSecret();
    }
}
