package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long id);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long id);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
          AND b.status = 'APPROVED'
          AND b.end < :now
        ORDER BY b.end DESC
        """)
    List<Booking> findPastApprovedByOwner(@Param("ownerId") Long ownerId,
                                          @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
          AND b.status = 'APPROVED'
          AND b.start > :now
        ORDER BY b.start ASC
        """)
    List<Booking> findFutureApprovedByOwner(@Param("ownerId") Long ownerId,
                                            @Param("now") LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}
