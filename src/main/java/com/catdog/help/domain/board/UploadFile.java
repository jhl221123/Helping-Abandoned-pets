package com.catdog.help.domain.board;

import lombok.*;

import javax.persistence.*;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile {

    @Id @GeneratedValue
    @Column(name = "upload_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "board_id")
    private Board board;

    private String uploadFileName;

    private String storeFileName;


    @Builder
    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    public void changeLeadImage(UploadFile newLeadImage) {
        this.uploadFileName = newLeadImage.getUploadFileName();
        this.storeFileName = newLeadImage.getStoreFileName();
    }

    public void addBoard(Board board) {
        this.board = board;
        board.getImages().add(this);
    }
}
