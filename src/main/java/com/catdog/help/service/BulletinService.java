package com.catdog.help.service;

import com.catdog.help.domain.board.Bulletin;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.repository.BulletinRepository;
import com.catdog.help.repository.LikeRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.bulletin.EditBulletinForm;
import com.catdog.help.web.form.bulletin.PageBulletinForm;
import com.catdog.help.web.form.bulletin.ReadBulletinForm;
import com.catdog.help.web.form.bulletin.SaveBulletinForm;
import com.catdog.help.web.form.image.ReadImageForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BulletinService {

    private final BulletinRepository bulletinRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;


    @Transactional
    public Long save(SaveBulletinForm form, String nickname) {
        Bulletin board = getBulletin(nickname, form);
        return bulletinRepository.save(board).getId();
    }

    public ReadBulletinForm read(Long id) {
        Bulletin findBoard = bulletinRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);

        List<ReadImageForm> imageForms = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));
        int likeSize = Math.toIntExact(likeRepository.countByBoardId(id));

        return ReadBulletinForm.builder()
                .board(findBoard)
                .imageForms(imageForms)
                .likeSize(likeSize)
                .build();
    }

    public Page<PageBulletinForm> getPage(Pageable pageable) {
        return bulletinRepository.findPageBy(pageable)
                .map(PageBulletinForm::new);
//        return getPageBulletinBoardForms(bulletinRepository.findPageBy(age(offset, limit));
    }
//
//    public int countPages() {
//        int total = (int) bulletinRepository.count();
//        if (total <= 10) {
//            return 1;
//        } else if (total % 10 == 0) {
//            return total / 10;
//        } else {
//            return total / 10 + 1;
//        }
//    }

    public EditBulletinForm getEditForm(Long id) {
        Bulletin findBoard = bulletinRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> oldImages = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));

        return new EditBulletinForm(findBoard, oldImages);
    }

    @Transactional
    public void update(EditBulletinForm form) {
        Bulletin findBoard = bulletinRepository.findById(form.getId())
                .orElseThrow(BoardNotFoundException::new);
        updateBulletin(findBoard, form);
    }

    @Transactional
    public void delete(Long boardId) {
        Bulletin findBoard = bulletinRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        bulletinRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private Bulletin getBulletin(String nickname, SaveBulletinForm form) {
        Optional<User> findUser = userRepository.findByNickname(nickname);
        Bulletin board = Bulletin.builder()
                .user(findUser.get())
                .title(form.getTitle())
                .content(form.getContent())
                .region(form.getRegion())
                .build();

        imageService.addImage(board, form.getImages());
        return board;
    }

    private List<PageBulletinForm> getPageBulletinForms(List<Bulletin> boards) {
        return boards.stream()
                .map(PageBulletinForm::new)
                .collect(Collectors.toList());
    }

    private void updateBulletin(Bulletin findBoard, EditBulletinForm form) {
        findBoard.updateBoard(form.getTitle(), form.getContent(), form.getRegion());
        imageService.updateImage(findBoard, form.getDeleteImageIds(), form.getNewImages());
    }

    private List<ReadImageForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(ReadImageForm::new)
                .collect(Collectors.toList());
    }
}
