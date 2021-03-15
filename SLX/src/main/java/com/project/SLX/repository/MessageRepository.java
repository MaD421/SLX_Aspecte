package com.project.SLX.repository;

import com.project.SLX.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends PagingAndSortingRepository<Message,Long> {
    List<Message> findAllByToUser_UserIdAndSentBy_UserIdOrSentBy_UserIdAndToUser_UserIdOrderByCreatedAtDesc(Long toUser_userId1, Long sentBy_userId1, Long toUser_userId2, Long sentBy_userId2, Pageable pageable);

    @Query("select m from message m where m.toUser.userId = ?1 group by m.sentBy.userId")
    List<Message> findAllByToUser_UserIdLatest(Long id, Pageable pageable);
}
