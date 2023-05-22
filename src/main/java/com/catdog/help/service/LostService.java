package com.catdog.help.service;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.LikeRepository;
import com.catdog.help.repository.LostRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LostService {

    private final LostRepository lostRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final ImageService imageService;
    private final LikeRepository likeRepository;


    @Transactional
    public Long save(SaveLostForm form, String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        Lost board = getLost(form, user);
        imageService.addImage(board, form.getImages());

        return lostRepository.save(board).getId();
    }

    /** **/
    public ReadLostForm read(Long id) {
        Lost findBoard = lostRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> imageForms = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));
        return new ReadLostForm(findBoard, imageForms);
    }
//
//    public Map<String, Long> getCountByRegion() {
//        List<Bulletin> boards = bulletinRepository.findAll();
//        List<String> regions = getRegions();
//        return getCountMap(boards, regions);
//    }
//
//    public Long countByNickname(String nickname) {
//        return bulletinRepository.findAll().stream()
//                .filter(b -> b.getUser().getNickname().equals(nickname))
//                .count();
//    }
//
//    public Page<PageBulletinForm> getPageByNickname(String nickname, Pageable pageable) {
//        return bulletinRepository.findPageByNickname(nickname, pageable)
//                .map(PageBulletinForm::new);
//    }
//
//    public Long countLikeBulletin(String nickname) {
//        return bulletinRepository.findAll().stream()
//                .filter(b -> b.getLikes().stream().anyMatch(like -> like.getUser().getNickname().equals(nickname))).count();
//    }
//
//    public Page<PageBulletinForm> getLikeBulletins(String nickname, Pageable pageable) {
//        User user = userRepository.findByNickname(nickname)
//                .orElseThrow(UserNotFoundException::new);
//
//        return bulletinRepository.findLikeBulletins(user.getId(), pageable)
//                .map(PageBulletinForm::new);
//    }
//
//    public Page<PageBulletinForm> search(BulletinSearch search, Pageable pageable) {
//        return searchQueryRepository.searchBulletin(search.getTitle(), search.getRegion(), pageable)
//                .map(PageBulletinForm::new);
//    }
//
//    public EditBulletinForm getEditForm(Long id) {
//        Bulletin findBoard = bulletinRepository.findById(id)
//                .orElseThrow(BoardNotFoundException::new);
//        List<ReadImageForm> oldImages = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));
//
//        return new EditBulletinForm(findBoard, oldImages);
//    }
//
//    @Transactional
//    public void update(EditBulletinForm form) {
//        Bulletin findBoard = bulletinRepository.findById(form.getId())
//                .orElseThrow(BoardNotFoundException::new);
//        updateBulletin(findBoard, form);
//    }
//
//    @Transactional
//    public void delete(Long boardId) {
//        Bulletin findBoard = bulletinRepository.findById(boardId)
//                .orElseThrow(BoardNotFoundException::new);
//        if (!findBoard.getImages().isEmpty()) {
//            imageService.deleteImage(findBoard.getImages());
//        }
//        bulletinRepository.delete(findBoard);
//    }
    /** **/

    private List<ReadImageForm> getReadUploadFileForms(List<UploadFile> uploadFiles) {
        return uploadFiles.stream()
                .map(ReadImageForm::new)
                .collect(Collectors.toList());
    }

    private Lost getLost(SaveLostForm form, User user) {
        return Lost.builder()
                .user(user)
                .title(form.getTitle())
                .content(form.getContent())
                .region(form.getRegion())
                .breed(form.getBreed())
                .lostDate(form.getLostDate())
                .lostPlace(form.getLostPlace())
                .gratuity(form.getGratuity())
                .build();
    }
}
