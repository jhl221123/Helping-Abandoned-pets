package com.catdog.help.service;

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
    private final ImageService imageService;
    private final JpaLikeBoardRepository jpaLikeBoardRepository;

    @Transactional
    public void createBoard(SaveItemBoardForm form, String nickname) {
        ItemBoard itemBoard = getItemBoard(nickname, form);
        itemBoardRepository.save(itemBoard);
    }

    public ReadItemBoardForm readBoard(Long id) {
        ItemBoard findBoard = itemBoardRepository.findById(id);
        List<ReadUploadFileForm> readUploadFileForms = getReadUploadFileForms(uploadFileRepository.findUploadFiles(id));
        int likeSize = (int)jpaLikeBoardRepository.countByBoardId(findBoard.getId());

        return new ReadItemBoardForm(findBoard, readUploadFileForms, likeSize);
    }

    public List<PageItemBoardForm> readPage(int page) {
        int offset = 0 + (page -1) * 6;
        int limit = 6;

        return getPageItemBoardForms(itemBoardRepository.findPage(offset, limit));
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
    public void updateBoard(Long id, UpdateItemBoardForm form) {
        ItemBoard findBoard = itemBoardRepository.findById(id);
        updateItemBoard(form, findBoard);
    }

    @Transactional
    public void changeStatus(Long id) {
        ItemBoard findBoard = itemBoardRepository.findById(id);
        findBoard.changeStatus(findBoard.getStatus() == ItemStatus.STILL ? ItemStatus.COMP : ItemStatus.STILL);
    }

    @Transactional
    public void deleteBoard(Long id) {
        ItemBoard findBoard = itemBoardRepository.findById(id);
        itemBoardRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private ItemBoard getItemBoard(String nickname, SaveItemBoardForm form) {
        User findUser = userRepository.findByNickname(nickname);
        ItemBoard board = ItemBoard.builder()
                .user(findUser)
                .form(form)
                .build();

        imageService.addImage(board, form.getImages());
        return board;
    }

    private List<PageItemBoardForm> getPageItemBoardForms(List<ItemBoard> boards) {
        return boards.stream()
                .map(b -> {
                    ReadUploadFileForm leadImage = getReadUploadFileForms(uploadFileRepository.findUploadFiles(b.getId())).get(0); // TODO: 2023-04-14 페이지 당 6번씩 쿼리나감..
                    return new PageItemBoardForm(b, leadImage);
                }).collect(Collectors.toList());
    }

    private void updateItemBoard(UpdateItemBoardForm form, ItemBoard board) {
        board.updateBoard(form);
        imageService.updateLeadImage(form.getNewLeadImage(), board.getId());
        imageService.updateImage(board, form.getDeleteImageIds(), form.getNewImages());
    }

    private List<ReadUploadFileForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(ReadUploadFileForm::new)
                .collect(Collectors.toList());
    }
}