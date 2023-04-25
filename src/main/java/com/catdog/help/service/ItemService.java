package com.catdog.help.service;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.ItemRepository;
import com.catdog.help.repository.LikeRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.item.EditItemForm;
import com.catdog.help.web.form.item.PageItemForm;
import com.catdog.help.web.form.item.ReadItemForm;
import com.catdog.help.web.form.item.SaveItemForm;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;

    @Transactional
    public void save(SaveItemForm form, String nickname) {
        Item item = getItem(nickname, form);
        itemRepository.save(item);
    }

    public ReadItemForm read(Long id) {
        Item findBoard = itemRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> readImageForms = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));
        int likeSize = Math.toIntExact(likeRepository.countByBoardId(findBoard.getId()));

        return new ReadItemForm(findBoard, readImageForms, likeSize);
    }

    public Page<PageItemForm> readPage(Pageable pageable) {
        return itemRepository.findPageBy(pageable)
                .map(item -> getPageItemForm(item));
    }
//
//    public int countPage() {
//        int total = (int) itemRepository.count();
//        if (total <= 6) {
//            return 1;
//        } else if (total % 6 == 0) {
//            return total / 6;
//        } else {
//            return total / 6 + 1;
//        }
//    }

    public EditItemForm getEditForm(Long id) {
        Item findBoard = itemRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> readImageForms = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));
        return new EditItemForm(findBoard, readImageForms);
    }

    @Transactional
    public void update(EditItemForm form) {
        Item findBoard = itemRepository.findById(form.getId())
                .orElseThrow(BoardNotFoundException::new);
        updateItem(form, findBoard);
    }

    @Transactional
    public void changeStatus(Long id) {
        Item findBoard = itemRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        findBoard.changeStatus(findBoard.getStatus() == ItemStatus.STILL ? ItemStatus.COMP : ItemStatus.STILL);
    }

    @Transactional
    public void delete(Long id) {
        Item findBoard = itemRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        itemRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private Item getItem(String nickname, SaveItemForm form) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);

        Item board = Item.builder()
                .user(findUser)
                .title(form.getTitle())
                .content(form.getContent())
                .itemName(form.getItemName())
                .price(form.getPrice())
                .build();

        imageService.addImage(board, form.getImages());
        return board;
    }

    private PageItemForm getPageItemForm(Item item) {
        return new PageItemForm(item, new ReadImageForm(item.getImages().get(0))); // TODO: 2023-04-14 페이지 당 6번씩 쿼리나가는지 확인하기..
    }

    private void updateItem(EditItemForm form, Item board) {
        board.updateBoard(form.getTitle(), form.getContent(), form.getItemName(), form.getPrice());
        imageService.updateLeadImage(form.getNewLeadImage(), board.getId());
        imageService.updateImage(board, form.getDeleteImageIds(), form.getNewImages());
    }

    private List<ReadImageForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(ReadImageForm::new)
                .collect(Collectors.toList());
    }
}