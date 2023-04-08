package com.catdog.help.web.controller;

import com.catdog.help.service.MessageRoomService;
import com.catdog.help.service.MessageService;
import com.catdog.help.web.form.message.ReadMessageRoomForm;
import com.catdog.help.web.form.message.SaveMessageForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.catdog.help.web.SessionConst.LOGIN_USER;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageRoomService messageRoomService;
    private final MessageService messageService;


    @GetMapping("/messages/new")
    public String checkMessageRoom(@SessionAttribute(name = LOGIN_USER) String senderNickName,
                                   @RequestParam("boardId") Long boardId, RedirectAttributes redirectAttributes,
                                   @RequestParam("recipientNickName") String recipientNickName) {

        Long roomId = messageRoomService.checkRoom(boardId, senderNickName);
        
        if (roomId == -1L) { // message room 생성
            Long newRoomId = messageRoomService.createRoom(boardId, senderNickName, recipientNickName);
            redirectAttributes.addAttribute("newRoomId", newRoomId);
            return "redirect:/messages/{newRoomId}";
        } else { // 기존 message room 입장
            redirectAttributes.addAttribute("roomId", roomId);
            return "redirect:/messages/{roomId}";
        }
    }

    @GetMapping("/messages/{roomId}")
    public String getMessageRoom(@SessionAttribute(name = LOGIN_USER) String senderNickName,
                                 @PathVariable("roomId") Long roomId, Model model) {

        ReadMessageRoomForm readForm = messageRoomService.readRoom(roomId);
        model.addAttribute("readForm", readForm);

        SaveMessageForm saveForm = new SaveMessageForm();
        model.addAttribute("saveForm", saveForm);

        model.addAttribute("sender", senderNickName);
        return "messages/room";
    }

    @PostMapping("/messages/{roomId}")
    public String sendMessage(@PathVariable("roomId") Long roomId, @SessionAttribute(name = LOGIN_USER) String senderNickName,
                              @Validated @ModelAttribute("saveForm") SaveMessageForm saveForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //검증
        }
        messageService.createMessage(roomId, senderNickName, saveForm);
        return "redirect:/messages/{roomId}";
    }

    @GetMapping("/messages")
    public String getMessageRooms(@SessionAttribute(name = LOGIN_USER) String senderNickName,
                                  @RequestParam("page") int page, Model model) {
        List<ReadMessageRoomForm> readPage = messageRoomService.readPageOfRooms(senderNickName, page);

        int lastPage = messageRoomService.countPages(senderNickName);
        model.addAttribute("readForms", readPage);
        model.addAttribute("lastPage", lastPage);
        // TODO: 2023-04-06 페이징 처리
        return "messages/list";
    }
}
