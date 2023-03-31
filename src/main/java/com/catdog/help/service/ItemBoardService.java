package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.repository.LikeBoardRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.web.form.itemBoard.SaveItemBoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemBoardService {

    private final JpaItemBoardRepository itemBoardRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final FileStore fileStore;
    private final LikeBoardRepository likeBoardRepository;

    @Transactional
    public void createBoard(SaveItemBoardForm saveForm) {
        ItemBoard itemBoard = getItemBoard(saveForm);
        itemBoardRepository.save(itemBoard);
    }

    private ItemBoard getItemBoard(SaveItemBoardForm saveForm) {
        ItemBoard board = new ItemBoard();
        board.setItemName(saveForm.getItemName());
        board.setPrice(saveForm.getPrice());
        board.setTitle(saveForm.getTitle());
        board.setContent(saveForm.getContent());
        if (!saveForm.getImages().isEmpty()) {
            List<UploadFile> uploadFiles = fileStore.storeFiles(saveForm.getImages());
            for (UploadFile uploadFile : uploadFiles) {
                board.addImage(uploadFile);
                uploadFileRepository.save(uploadFile);
            }
        }
        Dates dates = new Dates(LocalDateTime.now(), null, null);
        board.setDates(dates);
        return board;
    }

}
