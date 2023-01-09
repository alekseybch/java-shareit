package ru.practicum.shareit.booking.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.db.model.Booking;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerId(Long userId, Pageable pageable);

    Page<Booking> findBookingsByBookerIdAndEndIsBefore(Long userId, LocalDateTime time, Pageable pageable);

    Page<Booking> findBookingsByBookerIdAndStartAfter(Long userId, LocalDateTime time, Pageable pageable);

    Page<Booking> findBookingsByBookerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and b.start <= :time " +
            "and b.end >= :time")
    Page<Booking> findCurrent(Long userId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId")
    Page<Booking> findByOwnerId(Long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.start <= :time " +
            "and b.end >= :time")
    Page<Booking> findCurrentByOwnerId(Long userId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.end < :time")
    Page<Booking> findPastByOwnerId(Long userId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.start > :time")
    Page<Booking> findFutureByOwnerId(Long userId, LocalDateTime time, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.status = :status")
    Page<Booking> findByOwnerIdAndByStatus(Long userId, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and (b.start = (select max(bk.start) from Booking bk where bk.item.id = b.item.id and bk.start < :time) " +
            "or b.start = (select min(bk.start) from Booking bk where bk.item.id = b.item.id and bk.start > :time))")
    List<Booking> findLastAndNextById(Long itemId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.id in (:itemIds) " +
            "and (b.start = (select max(bk.start) from Booking bk where bk.item.id = b.item.id and bk.start < :time) " +
            "or b.start = (select min(bk.start) from Booking bk where bk.item.id = b.item.id and bk.start > :time))")
    List<Booking> findLastAndNextByIdList(List<Long> itemIds, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and :timeStart <= b.end " +
            "and :timeEnd >= b.start " +
            "and b.status = 'APPROVED'")
    List<Booking> findFreeInterval(Long itemId, LocalDateTime timeStart, LocalDateTime timeEnd);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.booker.id = :userId " +
            "and b.end < :time " +
            "and b.status = 'APPROVED'")
    Booking findByItemIdAndBookerIdAndStatusApproved(Long itemId, Long userId, LocalDateTime time);
}
