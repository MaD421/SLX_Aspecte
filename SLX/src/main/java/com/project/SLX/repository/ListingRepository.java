package com.project.SLX.repository;

import com.project.SLX.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface ListingRepository extends JpaRepository<Listing,Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE Listing l set l.views = l.views + 1 WHERE l.listing_id = ?",
            nativeQuery = true)
    void incrementViewsById(Long id);

    @Query(value = "SELECT l FROM listing l LEFT JOIN FETCH l.images WHERE l.listingId = :listingId")
    Optional<Listing> findByListingId(@Param("listingId") Long listingId);

    Page<Listing> findAllByCurrency(@Param("currency") String currency, Pageable pageable);

    @Query(value = "SELECT l FROM listing l WHERE (UPPER(l.title) LIKE CONCAT('%',UPPER(:search),'%') OR l.description LIKE CONCAT('%', UPPER(:search), '%') OR l.type LIKE CONCAT('%', UPPER(:search), '%'))")
    Page<Listing> findAllSearch(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT l FROM listing l WHERE l.currency = :currency AND (UPPER(l.title) LIKE CONCAT('%',UPPER(:search),'%') OR l.description LIKE CONCAT('%', UPPER(:search), '%') OR l.type LIKE CONCAT('%', UPPER(:search), '%'))")
    Page<Listing> findAllSearchCurrency(@Param("currency") String currency, @Param("search") String search, Pageable pageable);

    @Query(value = "SELECT l FROM listing l WHERE l.owner.userId = :userId")
    Page<Listing> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT l FROM listing l WHERE l.owner.userId = :userId AND l.currency = :currency")
    Page<Listing> findByUserIdCurrency(@Param("userId") Long userId, @Param("currency") String currency, Pageable pageable);

    @Query(value = "SELECT l FROM listing l WHERE l.owner.userId = :userId AND (UPPER(l.title) LIKE CONCAT('%', UPPER(:search), '%') OR l.description LIKE CONCAT('%', UPPER(:search), '%') OR l.type LIKE CONCAT('%', UPPER(:search), '%'))")
    Page<Listing> findByUserIdSearch(@Param("userId") Long userId, @Param("search") String search, Pageable pageable);

    @Query(value = "SELECT l FROM listing l WHERE l.owner.userId = :userId AND l.currency = :currency AND (UPPER(l.title) LIKE CONCAT('%', UPPER(:search), '%') OR l.description LIKE CONCAT('%', UPPER(:search), '%') OR l.type LIKE CONCAT('%', UPPER(:search), '%'))")
    Page<Listing> findByUserIdSearchCurrency(@Param("userId") Long userId, @Param("currency") String currency, @Param("search") String search, Pageable pageable);

    @Query(value = "SELECT l FROM listing l JOIN l.users lu WHERE lu.userId = :userId")
    Page<Listing> getAllBookmarked(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT l FROM listing l JOIN l.users lu WHERE lu.userId = :userId AND l.currency = :currency")
    Page<Listing> getAllBookmarkedCurrency(@Param("userId") Long userId, @Param("currency") String currency, Pageable pageable);

    @Query(value = "SELECT l FROM listing l JOIN l.users lu WHERE lu.userId = :userId AND (UPPER(l.title) LIKE CONCAT('%', UPPER(:search), '%') OR l.description LIKE CONCAT('%', UPPER(:search), '%') OR l.type LIKE CONCAT('%', UPPER(:search), '%'))")
    Page<Listing> getAllBookmarkedSearch(@Param("userId") Long userId, @Param("search") String search, Pageable pageable);

    @Query(value = "SELECT l FROM listing l JOIN l.users lu WHERE lu.userId = :userId AND l.currency = :currency AND (UPPER(l.title) LIKE CONCAT('%', UPPER(:search), '%') OR l.description LIKE CONCAT('%', UPPER(:search), '%') OR l.type LIKE CONCAT('%', UPPER(:search), '%'))")
    Page<Listing> getAllBookmarkedSearchCurrency(@Param("userId") Long userId, @Param("currency") String currency, @Param("search") String search, Pageable pageable);
}
