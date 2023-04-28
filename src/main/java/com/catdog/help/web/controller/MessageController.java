package com.catdog.help.web.controller;

import com.catdog.help.service.MsgRoomService;
import com.catdog.help.service.MessageService;
import com.catdog.help.web.form.message.PageMsgRoomForm;
import com.catdog.help.web.form.message.ReadMsgRoomForm;
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

    private final MsgRoomService msgRoomService;
    private final MessageService messageService;


    @GetMapping("/messages/new")
    public String checkAndSaveRoom(@SessionAttribute(name = LOGIN_USER) String senderNick,
                                   @RequestParam("boardId") Long boardId, RedirectAttributes redirectAttributes,
                                   @RequestParam("recipientNick") String recipientNick) {

        Long roomId = msgRoomService.checkRoom(boardId, senderNick);

        if (roomId == -1L) { // message room 생성
            Long newRoomId = msgRoomService.save(boardId, senderNick, recipientNick);
            redirectAttributes.addAttribute("newRoomId", newRoomId);
            return "redirect:/messages/{newRoomId}";
        } else { // 기존 message room 입장
            redirectAttributes.addAttribute("roomId", roomId);
            return "redirect:/messages/{roomId}";
        }
    }

    @GetMapping("/messages/{roomId}")
    public String getRoom(@PathVariable("roomId") Long roomId, Model model,
                          @SessionAttribute(name = LOGIN_USER) String senderNick) {

        // TODO: 2023-04-27 접근제한

        ReadMsgRoomForm readForm = msgRoomService.read(roomId);
        model.addAttribute("readForm", readForm);

        SaveMessageForm saveForm = new SaveMessageForm();
        model.addAttribute("saveForm", saveForm);

        model.addAttribute("sender", senderNick);
        return "messages/room";
    }

    @PostMapping("/messages/{roomId}")
    public String sendMessage(@PathVariable("roomId") Long roomId, @SessionAttribute(name = LOGIN_USER) String senderNick,
                              @Validated @ModelAttribute("saveForm") SaveMessageForm saveForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // TODO: 2023-04-16 검증
        }
        messageService.save(roomId, senderNick, saveForm);
        return "redirect:/messages/{roomId}";
    }

    @GetMapping("/messages")
    public String getRooms(@SessionAttribute(name = LOGIN_USER) String senderNick,
                           @RequestParam("page") int page, Model model) {
        List<PageMsgRoomForm> pageForms = msgRoomService.getPage(senderNick, page);

        int lastPage = msgRoomService.countPages(senderNick);
        model.addAttribute("pageForms", pageForms);
        model.addAttribute("lastPage", lastPage);
        return "messages/list";
    }
}
