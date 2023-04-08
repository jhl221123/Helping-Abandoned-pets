package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.repository.jpa.LikeBoardRepository;
import com.catdog.help.web.form.itemboard.PageItemBoardForm;
import com.catdog.help.web.form.itemboard.ReadItemBoardForm;
import com.catdog.help.web.form.itemboard.SaveItemBoardForm;
import com.catdog.help.web.form.itemboard.UpdateItemBoardForm;
import com.catdog.help.web.form.uploadfile.ReadUploadFileForm;
import com.catdog.help.web.form.user.ReadUserForm;
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
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final FileStore fileStore;
    private final LikeBoardRepository likeBoardRepository;

    @Transactional
    public void createBoard(SaveItemBoardForm saveForm, String nickName) {
        ItemBoard itemBoard = getItemBoard(saveForm);
        User findUser = userRepository.findByNickName(nickName);
        itemBoard.setUser(findUser);
        itemBoardRepository.save(itemBoard);
    }

    public ReadItemBoardForm readBoard(Long itemBoardId) {
        ItemBoard findBoard = itemBoardRepository.findById(itemBoardId);
        List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(itemBoardId);
        return getReadForm(findBoard, uploadFiles);
    }

    public List<PageItemBoardForm> readPage(int page) {
        int offset = 0 + (page -1) * 6;
        int limit = 6;

        List<ItemBoard> boards = itemBoardRepository.findPage(offset, limit);

        List<PageItemBoardForm> pageForms = new ArrayList<>();
        for (ItemBoard board : boards) {
            PageItemBoardForm form = getPageForm(board);
            pageForms.add(form);
        }

        return pageForms;
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
        return getUpdateItemBoardForm(id, findBoard);
    }

    @Transactional
    public void updateBoard(Long id, UpdateItemBoardForm updateForm) {
        ItemBoard findBoard = itemBoardRepository.findById(id);
        updateItemBoard(updateForm, findBoard);
    }

    @Transactional
    public void addViews(Long boardId) {
        ItemBoard findBoard = itemBoardRepository.findById(boardId);
        findBoard.addViews();
        // TODO: 2023-03-29 조회수만 업데이트 하는데 findOne(fetch join) 쿼리가 불편. 리팩토링 필요
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

    private PageItemBoardForm getPageForm(ItemBoard board) {
        PageItemBoardForm form = new PageItemBoardForm();
        form.setId(board.getId());
        form.setItemName(board.getItemName());
        form.setPrice(board.getPrice());
        form.setStatus(board.getStatus());
        form.setLeadImage(getReadUploadFileForms(uploadFileRepository.findUploadFiles(board.getId())).get(0));
        return form;
    }

    private ReadItemBoardForm getReadForm(ItemBoard findBoard, List<UploadFile> uploadFiles) {
        ReadItemBoardForm form = new ReadItemBoardForm();
        form.setId(findBoard.getId());
        form.setUserForm(getReadUserForm(findBoard.getUser()));
        form.setTitle(findBoard.getTitle());
        form.setContent(findBoard.getContent());
        form.setDates(findBoard.getDates());
        form.setItemName(findBoard.getItemName());
        form.setPrice(findBoard.getPrice());
        form.setStatus(findBoard.getStatus());
        form.setImages(getReadUploadFileForms(uploadFiles));
        form.setViews(findBoard.getViews());
        form.setLikeSize((int) likeBoardRepository.countByBoardId(findBoard.getId()));
        return form;
    }

    private UpdateItemBoardForm getUpdateItemBoardForm(Long id, ItemBoard findBoard) {
        UpdateItemBoardForm updateForm = new UpdateItemBoardForm();
        updateForm.setId(findBoard.getId());
        updateForm.setTitle(findBoard.getTitle());
        updateForm.setContent(findBoard.getContent());
        updateForm.setItemName(findBoard.getItemName());
        updateForm.setPrice(findBoard.getPrice());

        List<UploadFile> uploadFiles = uploadFileRepository.findUploadFiles(id);
        if (!uploadFiles.isEmpty()) {
            List<ReadUploadFileForm> readForms = getReadUploadFileForms(uploadFiles);
            //대표이미지
            updateForm.setOldLeadImage(readForms.get(0));
            for (int i = 1; i < readForms.size(); i++) {
                //대표이미지 제외
                updateForm.getOldImages().add(readForms.get(i));
            }
        }
        return updateForm;
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

    private ReadUserForm getReadUserForm(User user) {
        ReadUserForm readForm = new ReadUserForm();
        readForm.setId(user.getId());
        readForm.setEmailId(user.getEmailId());
        readForm.setPassword(user.getPassword());
        readForm.setNickName(user.getNickName());
        readForm.setName(user.getName());
        readForm.setAge(user.getAge());
        readForm.setGender(user.getGender());
        readForm.setDates(user.getDates());
        return readForm;
    }

    private List<ReadUploadFileForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(u -> {
                    ReadUploadFileForm readForm = new ReadUploadFileForm();
                    readForm.setId(u.getId());
                    readForm.setStoreFileName(u.getStoreFileName());
                    readForm.setUploadFileName(u.getUploadFileName());
                    return readForm;
                }).collect(Collectors.toList());
    }
}