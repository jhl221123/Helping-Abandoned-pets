package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.JpaLikeBoardRepository;
import com.catdog.help.repository.jpa.JpaUploadFileRepository;
import com.catdog.help.repository.jpa.JpaUserRepository;
import com.catdog.help.web.form.itemboard.PageItemBoardForm;
import com.catdog.help.web.form.itemboard.ReadItemBoardForm;
import com.catdog.help.web.form.itemboard.SaveItemBoardForm;
import com.catdog.help.web.form.itemboard.UpdateItemBoardForm;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemBoardService {

    private final JpaItemBoardRepository itemBoardRepository;
    private final JpaUserRepository userRepository;
    private final JpaUploadFileRepository uploadFileRepository;
    private final FileStore fileStore;
    private final JpaLikeBoardRepository jpaLikeBoardRepository;

    @Transactional
    public void createBoard(SaveItemBoardForm saveForm, String nickName) {
        ItemBoard itemBoard = getItemBoard(saveForm);
        User findUser = userRepository.findByNickname(nickName);
        itemBoard.setUser(findUser);
        itemBoardRepository.save(itemBoard);
    }

    public ReadItemBoardForm readBoard(Long itemBoardId) {
        ItemBoard findBoard = itemBoardRepository.findById(itemBoardId);
        List<ReadUploadFileForm> readUploadFileForms = getReadUploadFileForms(uploadFileRepository.findUploadFiles(itemBoardId));
        int likeSize = (int)jpaLikeBoardRepository.countByBoardId(findBoard.getId());

        return new ReadItemBoardForm(findBoard, readUploadFileForms, likeSize);
    }

    public List<PageItemBoardForm> readPage(int page) {
        int offset = 0 + (page -1) * 6;
        int limit = 6;

        List<ItemBoard> boards = itemBoardRepository.findPage(offset, limit);

        return getPageItemBoardForms(boards);
    }

    public int countPage() {
        int total = (int)itemBoardRepository.countAll();
        if (total <= 6) {
            return 1;
        } else if (total % 6 == 0) {
            return total / 6;
        } else {
            return total / 6 + 1;
        }
    }

    public UpdateItemBoardForm getUpdateForm(Long id) {
        ItemBoard findBoard = itemBoardRepository.findById(id);
        List<ReadUploadFileForm> readUploadFileForms = getReadUploadFileForms(uploadFileRepository.findUploadFiles(id));
        return new UpdateItemBoardForm(findBoard, readUploadFileForms);
    }

    @Transactional
    public void updateBoard(Long id, UpdateItemBoardForm updateForm) {
        ItemBoard findBoard = itemBoardRepository.findById(id);
        updateItemBoard(updateForm, findBoard);
    }

    @Transactional
    public void changeStatus(Long boardId) {
        ItemBoard findBoard = itemBoardRepository.findById(boardId);
        if (findBoard.getStatus() == ItemStatus.STILL) {
            findBoard.setStatus(ItemStatus.COMP);
        } else {
            findBoard.setStatus(ItemStatus.STILL);
        }
    }

    @Transactional
    public void deleteBoard(Long id) {
        ItemBoard findBoard = itemBoardRepository.findById(id);
        itemBoardRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private ItemBoard getItemBoard(SaveItemBoardForm saveForm) {
        ItemBoard board = new ItemBoard();
        board.setTitle(saveForm.getTitle());
        board.setContent(saveForm.getContent());
        board.setItemName(saveForm.getItemName());
        board.setPrice(saveForm.getPrice());
        board.setStatus(ItemStatus.STILL);
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

    private List<PageItemBoardForm> getPageItemBoardForms(List<ItemBoard> boards) {
        return boards.stream().map(b -> {
            ReadUploadFileForm leadImage = getReadUploadFileForms(uploadFileRepository.findUploadFiles(b.getId())).get(0); // TODO: 2023-04-14 페이지 당 6번씩 쿼리나감..
            PageItemBoardForm form = new PageItemBoardForm(b, leadImage);
            return form;
        }).collect(Collectors.toList());
    }

    private void updateItemBoard(UpdateItemBoardForm updateForm, ItemBoard findBoard) {
        findBoard.setTitle(updateForm.getTitle());
        findBoard.setContent(updateForm.getContent());
        findBoard.setItemName(updateForm.getItemName());
        findBoard.setPrice(updateForm.getPrice());

        //대표이미지 변경
        if (!updateForm.getNewLeadImage().getResource().getFilename().isEmpty()) { //지금은 이름으로 검증하는게 최선;;
            UploadFile newLeadImage = fileStore.storeFile(updateForm.getNewLeadImage());
            List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(findBoard.getId());
            uploadFiles.get(0).setUploadFileName(newLeadImage.getUploadFileName());
            uploadFiles.get(0).setStoreFileName(newLeadImage.getStoreFileName());
            // TODO: 2023-04-02 이름 각각 덮어서 수정했는데 다른 방법도 강구해보자.
        }

        //선택 이미지 삭제
        if (!updateForm.getDeleteImageIds().isEmpty()) {
            for (Integer id : updateForm.getDeleteImageIds()) {
                UploadFile target = uploadFileRepository.findById(Long.valueOf(id));
                uploadFileRepository.delete(target);
            }
        }
        // TODO: 2023-04-02 file 경로에 있는 이미지 삭제 -> storeName으로

        //새 이미지 추가
        if (!updateForm.getNewImages().isEmpty()) {
            List<UploadFile> uploadFiles = fileStore.storeFiles(updateForm.getNewImages());
            for (UploadFile uploadFile : uploadFiles) {
                findBoard.addImage(uploadFile);
                uploadFileRepository.save(uploadFile);
            }
        }
        findBoard.setDates(new Dates(findBoard.getDates().getCreateDate(), LocalDateTime.now(), null));
    }

    private List<ReadUploadFileForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(u -> {
                    ReadUploadFileForm readForm = new ReadUploadFileForm(u);
                    return readForm;
                }).collect(Collectors.toList());
    }
}