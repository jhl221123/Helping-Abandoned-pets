package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.board.Board;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.exception.FileNotFoundException;
import com.catdog.help.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    @Value("${file.dir}")
    private String fileDir;
    private final FileStore fileStore;
    private final UploadFileRepository uploadFileRepository;

    public void addImage(Board board, List<MultipartFile> images) {
        if (!images.isEmpty()) {
            fileStore.storeFiles(images)
                    .forEach(uploadFile -> {
                        uploadFile.addBoard(board);
                        uploadFileRepository.save(uploadFile);
                    });
        }
    }

    public void updateImage(Board board, List<Long> deleteIds, List<MultipartFile> newImages) {
        if (!deleteIds.isEmpty()) {
            deleteIds.stream()
                    .map(id -> uploadFileRepository.findById(Long.valueOf(id)))
                    .forEach(uploadFile -> {
                        File file = new File(fileDir + uploadFile
                                            .orElseThrow(FileNotFoundException::new)
                                            .getStoreFileName());
                        file.delete(); // 폴더에서 이미지 삭제 todo 삭제는 잘 되는데 사용자나 글 삭제 시 이미지도 같이 삭제 되도록 구현필요
                        uploadFileRepository.delete(uploadFile.orElseThrow(FileNotFoundException::new));
                    });
        }

        addImage(board, newImages);
    }

    public void updateLeadImage(MultipartFile newLeadImage, Long boardId) {
        //대표이미지 변경
        if (!newLeadImage.getResource().getFilename().isEmpty()) { //지금은 이름으로 검증하는게 최선;;
            UploadFile result = fileStore.storeFile(newLeadImage);
            uploadFileRepository.findByBoardId(boardId) // TODO: 2023-04-24 findFirst 이용해보자.
                    .get(0)
                    .changeLeadImage(result);
            // TODO: 2023-04-02 이름 각각 덮어서 수정했는데 다른 방법도 강구해보자.
        }
    }

    public void deleteImage(List<UploadFile> images) {
        images.stream()
                .forEach(uploadFile -> {
                    File file = new File(fileDir + uploadFile.getStoreFileName());
                    file.delete();
                    uploadFileRepository.delete(uploadFile);
                });
    }
}