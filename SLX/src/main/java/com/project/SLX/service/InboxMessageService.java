package com.project.SLX.service;

import com.project.SLX.model.InboxMessage;
import com.project.SLX.model.Message;
import com.project.SLX.model.User;
import com.project.SLX.repository.InboxMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InboxMessageService {
    private final InboxMessageRepository inboxMessageRepository;

    @Autowired
    public InboxMessageService(InboxMessageRepository inboxMessageRepository) {
        this.inboxMessageRepository = inboxMessageRepository;
    }

    public List<InboxMessage> findInbox(Long userId, Pageable pageable) {
        return inboxMessageRepository.findAllByFirstUser_UserIdOrSecondUser_UserIdOrderByCreatedAtDesc(userId, userId, pageable);
    }

    public InboxMessage add(Message message) {
        User firstUser, secondUser;
        InboxMessage inboxMessage;

        firstUser = message.getSentBy();
        secondUser = message.getToUser();

        try {
            inboxMessage = findByFirstUserAndSecondUser(firstUser, secondUser);
            inboxMessage.setBody(message.getBody());
            inboxMessage.setCreatedAt(message.getCreatedAt());
        } catch (Exception error) {
            inboxMessage = new InboxMessage();
            inboxMessage.setBody(message.getBody());
            inboxMessage.setCreatedAt(message.getCreatedAt());
            inboxMessage.setFirstUser(firstUser);
            inboxMessage.setSecondUser(secondUser);
        }

        inboxMessage = inboxMessageRepository.save(inboxMessage);

        return inboxMessage;
    }

    public InboxMessage findByFirstUserAndSecondUser(User firstUser, User secondUser) {
        String combinedIds = InboxMessage.getCombinedIds(firstUser, secondUser);

        return inboxMessageRepository.findByCombinedIds(combinedIds);
    }
}
