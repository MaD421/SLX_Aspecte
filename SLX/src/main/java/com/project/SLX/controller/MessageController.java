package com.project.SLX.controller;

import com.project.SLX.model.InboxMessage;
import com.project.SLX.model.Message;
import com.project.SLX.model.User;
import com.project.SLX.model.dto.InboxMessageDTO;
import com.project.SLX.model.dto.MessageDTO;
import com.project.SLX.model.exception.MessageException;
import com.project.SLX.model.exception.UserException;
import com.project.SLX.service.CustomUserDetailsService;
import com.project.SLX.service.InboxMessageService;
import com.project.SLX.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;
    private final InboxMessageService inboxMessageService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public MessageController(MessageService messageService, InboxMessageService inboxMessageService, CustomUserDetailsService customUserDetailsService) {
        this.messageService = messageService;
        this.inboxMessageService = inboxMessageService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/send/{sendTo}")
    @ResponseStatus(HttpStatus.CREATED)
    public String sendMessage(@RequestBody String body, @PathVariable Long sendTo) {
        if (body == null || body.isEmpty()) {
            throw new MessageException("Invalid body");
        }

        User toUser = customUserDetailsService.getUserById(sendTo).orElse(null);
        if (toUser == null) {
            throw new UserException("User not found!");
        }

        User sendBy = customUserDetailsService.getCurrentUser();

        Message createdMessage = messageService.createMessage(body, toUser, sendBy);
        inboxMessageService.add(createdMessage);

        return "ok";
    }

    // ex: /getByUser/32?page=5
    @GetMapping("/getByUser/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageDTO> getAllMessagesByUser(@PageableDefault(value = MessageViewController.messagesPerPage, page = MessageViewController.initialPage) Pageable page, @PathVariable Long userId) {
        User user = customUserDetailsService.getCurrentUser();
        List<Message> messages = messageService.findAllToUserAndByUser(userId, user.getUserId(), page);
        List<MessageDTO> messageDTOS = new ArrayList<>();
        messages.forEach(m -> {
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setBody(m.getBody());
            messageDTO.setCreatedAt(m.getCreatedAt());
            messageDTO.setOwn(m.getSentBy().getUserId().equals(user.getUserId()));
            messageDTOS.add(messageDTO);
        });
        return messageDTOS;
    }

    @GetMapping("/inbox")
    @ResponseStatus(HttpStatus.OK)
    public List<InboxMessageDTO> getInbox(@PageableDefault(value = MessageViewController.inboxMessagesPerPage, page = MessageViewController.initialPage) Pageable page) {
        User user = customUserDetailsService.getCurrentUser();
        List<InboxMessage> inboxMessages = inboxMessageService.findInbox(user.getUserId(), page);
        List<InboxMessageDTO> inboxMessageDTOS = new ArrayList<>();

        inboxMessages.forEach(m -> {
            Long partnerId;
            String username;
            InboxMessageDTO inboxMessageDTO = new InboxMessageDTO();

            if (user.getUserId().equals(m.getFirstUser().getUserId())) {
                partnerId = m.getSecondUser().getUserId();
                username = m.getSecondUser().getUsername();
            } else {
                partnerId = m.getFirstUser().getUserId();
                username = m.getFirstUser().getUsername();
            }

            inboxMessageDTO.setPartnerUsername(username);
            inboxMessageDTO.setPartnerId(partnerId);
            inboxMessageDTO.setOwn(m.getFirstUser().getUserId().equals(user.getUserId()));
            inboxMessageDTO.setBody(m.getBody());
            inboxMessageDTO.setCreatedAt(m.getCreatedAt());
            inboxMessageDTOS.add(inboxMessageDTO);
        });

        return inboxMessageDTOS;
    }
}
