package ru.practicum.shareit.booking.db.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.db.model.Booking;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long userId, Sort sort);

    List<Booking> findBookingsByBookerIdAndEndIsBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findBookingsByBookerIdAndStartAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findBookingsByBookerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId " +
            "and b.start <= :time " +
            "and b.end >= :time " +
            "order by b.start desc")
    List<Booking> findCurrent(Long userId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "order by b.start desc")
    List<Booking> findByOwnerId(Long userId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.start <= :time " +
            "and b.end >= :time " +
            "order by b.start desc")
    List<Booking> findCurrentByOwnerId(Long userId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.end < :time " +
            "order by b.start desc")
    List<Booking> findPastByOwnerId(Long userId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.start > :time " +
            "order by b.start desc")
    List<Booking> findFutureByOwnerId(Long userId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.owner.id = :userId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findByOwnerIdAndByStatus(Long userId, BookingStatus status);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and (b.end = (select max(bk.end) from Booking bk where bk.item.id = :itemId and bk.end < :time) " +
            "or b.start = (select min(bk.start) from Booking bk where bk.item.id = :itemId and bk.start > :time))")
    List<Booking> findLastAndNextById(Long itemId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.id in (:itemIds) " +
            "and (b.end = (select max(bk.end) from Booking bk where bk.item.id in (:itemIds) and bk.end < :time) " +
            "or b.start = (select min(bk.start) from Booking bk where bk.item.id in (:itemIds) and bk.start > :time))")
    List<Booking> findLastAndNextByIdList(List<Long> itemIds, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.booker.id = :userId " +
            "and b.end < :time")
    Booking findByItemIdAndBookerId(Long itemId, Long userId, LocalDateTime time);
}
