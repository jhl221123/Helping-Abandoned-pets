package com.catdog.help.web.api.request.inquiry;

import com.catdog.help.domain.board.SecretStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class SaveInquiryRequest {

    @NotBlank
    @Length(max = 30)
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private SecretStatus secret;


    @Builder
    public SaveInquiryRequest(String title, String content, SecretStatus secret) {
        this.title = title;
        this.content = content;
        this.secret = secret;
    }
}
