package com.catdog.help.service;

import com.catdog.help.domain.board.Item;
import com.catdog.help.domain.board.ItemStatus;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.*;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.item.EditItemForm;
import com.catdog.help.web.form.item.PageItemForm;
import com.catdog.help.web.form.item.ReadItemForm;
import com.catdog.help.web.form.item.SaveItemForm;
import com.catdog.help.web.form.search.ItemSearch;
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
    private final SearchQueryRepository searchQueryRepository;

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

    public Long countByNickname(String nickname) {
        return itemRepository.countByNickname(nickname);
    }

    public Page<PageItemForm> getPageByNickname(String nickname, Pageable pageable) {
        return itemRepository.findPageByNickname(nickname, pageable)
                .map(item -> getPageItemForm(item));
    }

    public Long countLikeItem(String nickname) {
        return itemRepository.findAll().stream()
                .filter(b -> b.getLikes().stream().anyMatch(like -> like.getUser().getNickname().equals(nickname))).count();
    }

    public Page<PageItemForm> getLikeItems(String nickname, Pageable pageable) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);

        return itemRepository.findLikeItems(user.getId(), pageable)
                .map(item -> getPageItemForm(item));
    }

    public Page<PageItemForm> search(ItemSearch search, Pageable pageable) {
        return searchQueryRepository.searchItem(search.getTitle(), search.getItemName(), pageable)
                .map(item -> getPageItemForm(item));
    }

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
        if (!findBoard.getImages().isEmpty()) {
            imageService.deleteImage(findBoard.getImages());
        }
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
                .region(form.getRegion())
                .build();

        imageService.addImage(board, form.getImages());
        return board;
    }

    private PageItemForm getPageItemForm(Item item) {
        return new PageItemForm(item, new ReadImageForm(item.getImages().get(0))); // TODO: 2023-04-14 페이지 당 6번씩 쿼리나가는지 확인하기..
    }

    private void updateItem(EditItemForm form, Item board) {
        board.updateBoard(form.getTitle(), form.getContent(), form.getItemName(), form.getPrice(), form.getRegion());
        imageService.updateLeadImage(form.getNewLeadImage(), board.getId());
        imageService.updateImage(board, form.getDeleteImageIds(), form.getNewImages());
    }

    private List<ReadImageForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(ReadImageForm::new)
                .collect(Collectors.toList());
    }
}