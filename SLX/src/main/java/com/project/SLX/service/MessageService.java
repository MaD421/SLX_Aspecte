package com.project.SLX.service;

import com.project.SLX.model.Message;
import com.project.SLX.model.User;
import com.project.SLX.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message createMessage(String body, User sendTo, User sendBy) {
        Message message = new Message();
        message.setCreatedAt(LocalDateTime.now());
        message.setBody(body);
        message.setSentBy(sendBy);
        message.setToUser(sendTo);
        return messageRepository.save(message);
    }

    public List<Message> findAllToUserAndByUser(Long toUserId, Long byUserId, Pageable pageable) {
        return messageRepository.findAllByToUser_UserIdAndSentBy_UserIdOrSentBy_UserIdAndToUser_UserIdOrderByCreatedAtDesc(toUserId, byUserId, toUserId, byUserId, pageable);
    }

    public List<Message> findInbox(Long userId, Pageable pageable) {
        return messageRepository.findAllByToUser_UserIdLatest(userId, pageable);
    }
}
