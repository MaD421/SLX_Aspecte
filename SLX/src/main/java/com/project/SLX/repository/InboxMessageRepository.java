package com.project.SLX.repository;

import com.project.SLX.model.InboxMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboxMessageRepository extends PagingAndSortingRepository<InboxMessage,Long> {
    List<InboxMessage> findAllByFirstUser_UserIdOrSecondUser_UserIdOrderByCreatedAtDesc(Long id1, Long id2, Pageable pageable);
    InboxMessage findByCombinedIds(String combinedIds);
}