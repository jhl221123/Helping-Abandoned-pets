package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.repository.jpa.JpaUploadFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final FileStore fileStore;
    private final JpaUploadFileRepository uploadFileRepository;

    public void addImage(Board board, List<MultipartFile> images) {
        if (!images.isEmpty()) {
            fileStore.storeFiles(images)
                    .forEach(uploadFile -> {
                        uploadFile.addBoard(board);
                        uploadFileRepository.save(uploadFile);
                    });
        }
    }

    public void updateImage(Board board, List<Integer> deleteIds, List<MultipartFile> newImages) {
        if (!deleteIds.isEmpty()) {
            deleteIds.stream()
                    .map(id -> uploadFileRepository.findById(Long.valueOf(id)))
                    .forEach(uploadFile -> uploadFileRepository.delete(uploadFile));
        }

        // TODO: 2023-04-02 file 경로에 있는 이미지 삭제 -> storeName으로

        addImage(board, newImages);
    }

    public void updateLeadImage(MultipartFile newLeadImage, Long boardId) {
        //대표이미지 변경
        if (!newLeadImage.getResource().getFilename().isEmpty()) { //지금은 이름으로 검증하는게 최선;;
            UploadFile result = fileStore.storeFile(newLeadImage);
            uploadFileRepository.findUploadFiles(boardId)
                    .get(0)
                    .changeLeadImage(result);
            // TODO: 2023-04-02 이름 각각 덮어서 수정했는데 다른 방법도 강구해보자.
        }
    }
}