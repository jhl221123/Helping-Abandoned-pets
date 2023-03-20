package com.catdog.help.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@Getter
public class DateList {

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    public DateList() { // 기본 생성자 필요
    }

    public DateList(LocalDateTime createDate, LocalDateTime lastModifiedDate, LocalDateTime deleteDate) {
        this.createDate = createDate;
        this.lastModifiedDate = lastModifiedDate;
        this.deleteDate = deleteDate;
    }
}
