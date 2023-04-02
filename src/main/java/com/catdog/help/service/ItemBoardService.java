package com.catdog.help.service;

import com.catdog.help.FileStore;
import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.ItemBoard;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.LikeBoardRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.repository.jpa.JpaItemBoardRepository;
import com.catdog.help.web.form.itemBoard.PageItemBoardForm;
import com.catdog.help.web.form.itemBoard.ReadItemBoardForm;
import com.catdog.help.web.form.itemBoard.SaveItemBoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        int start = 0 + (page -1) * 9;
        int total = 9;

        List<ItemBoard> boards = itemBoardRepository.findPage(start, total);

        List<PageItemBoardForm> pageForms = new ArrayList<>();
        for (ItemBoard board : boards) {
            PageItemBoardForm form = getPageForm(board);
            pageForms.add(form);
        }

        return pageForms;
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
        form.setLeadImage(uploadFileRepository.findUploadFiles(board.getId()).get(0));
        return form;
    }

    private static ReadItemBoardForm getReadForm(ItemBoard findBoard, List<UploadFile> uploadFiles) {
        ReadItemBoardForm form = new ReadItemBoardForm();
        form.setId(findBoard.getId());
        form.setUser(findBoard.getUser());
        form.setTitle(findBoard.getTitle());
        form.setContent(findBoard.getContent());
        form.setDates(findBoard.getDates());
        form.setItemName(findBoard.getItemName());
        form.setPrice(findBoard.getPrice());
        form.setStatus(findBoard.getStatus());
        form.setImages(uploadFiles);
        return form;
    }
}
