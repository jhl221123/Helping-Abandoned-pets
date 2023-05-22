package com.catdog.help.service;

import com.catdog.help.domain.board.Lost;
import com.catdog.help.domain.board.UploadFile;
import com.catdog.help.domain.user.User;
import com.catdog.help.exception.BoardNotFoundException;
import com.catdog.help.exception.UserNotFoundException;
import com.catdog.help.repository.LostRepository;
import com.catdog.help.repository.UploadFileRepository;
import com.catdog.help.repository.UserRepository;
import com.catdog.help.web.form.image.ReadImageForm;
import com.catdog.help.web.form.lost.EditLostForm;
import com.catdog.help.web.form.lost.ReadLostForm;
import com.catdog.help.web.form.lost.SaveLostForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.catdog.help.domain.board.RegionConst.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LostService {

    private final LostRepository lostRepository;
    private final UserRepository userRepository;
    private final UploadFileRepository uploadFileRepository;
    private final ImageService imageService;


    @Transactional
    public Long save(SaveLostForm form, String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        Lost board = getLost(form, user);
        imageService.addImage(board, form.getImages());

        return lostRepository.save(board).getId();
    }

    public ReadLostForm read(Long id) {
        Lost findBoard = lostRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> imageForms = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));
        return new ReadLostForm(findBoard, imageForms);
    }

    public Map<String, Long> getCountByRegion() {
        List<Lost> boards = lostRepository.findAll();
        List<String> regions = getRegions();
        return getCountMap(boards, regions);
    }

    public Long countByNickname(String nickname) {
        return lostRepository.countByNickname(nickname);
    }

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

    public EditLostForm getEditForm(Long id) {
        Lost findBoard = lostRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
        List<ReadImageForm> oldImages = getReadUploadFileForms(uploadFileRepository.findByBoardId(id));

        return new EditLostForm(findBoard, oldImages);
    }

    @Transactional
    public void update(EditLostForm form) {
        Lost findBoard = lostRepository.findById(form.getId())
                .orElseThrow(BoardNotFoundException::new);
        updateBulletin(findBoard, form);
    }

    @Transactional
    public void delete(Long boardId) {
        Lost findBoard = lostRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        if (!findBoard.getImages().isEmpty()) {
            imageService.deleteImage(findBoard.getImages());
        }
        lostRepository.delete(findBoard);
    }

    /**============================= private method ==============================*/

    private void updateBulletin(Lost findBoard, EditLostForm form) {
        findBoard.updateBoard(form.getTitle(), form.getContent(), form.getRegion(), form.getBreed(), form.getLostDate(), form.getLostPlace(), form.getGratuity());
        // TODO: 2023-05-22 lostedInfo 등으로 값타입 생성 고민해보자
        imageService.updateImage(findBoard, form.getDeleteImageIds(), form.getNewImages());
    }

    private Map<String, Long> getCountMap(List<Lost> boards, List<String> regions) {
        Map<String, Long> result = new HashMap<>();
        for (String region : regions) {
            long countByRegion = boards.stream()
                    .filter(b -> b.getRegion().equals(region))
                    .count();
            result.put(region, countByRegion);
        }
        return result;
    }

    private List<String> getRegions() {
        return Arrays.asList(SEOUL, BUSAN, INCHEON, DAEJEON, DAEGU, ULSAN, GWANGJU, SEJONG,
                GYEONGGI, GANGWON, CHUNGBUK, CHUNGNAM, JEONBUK, JEONNAM, GYEONGBUK, GYEONGNAM, JEJU);
    }

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
