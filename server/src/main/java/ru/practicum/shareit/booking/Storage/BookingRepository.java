package ru.practicum.shareit.booking.Storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where b.booker_id=:userId " +
            "ORDER BY b.start_date desc",
            nativeQuery = true)
    List<Booking> findByBooker_Id(Long userId, Pageable pageable);

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where b.booker_id=:userId AND b.status=:status ",
            nativeQuery = true)
    List<Booking> findBookingsUserByStatus(@Param("userId") Long userId, @Param("status") String status, Pageable pageable);

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where b.booker_id=:userId AND (b.status='APPROVED' OR b.status='WAITING') AND b.start_date > CURRENT_TIMESTAMP ",
            nativeQuery = true)
    List<Booking> findBookingsUserByFuture(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where b.booker_id=:userId AND b.status='APPROVED' AND b.end_date < CURRENT_TIMESTAMP ",
            nativeQuery = true)
    List<Booking> findBookingsUserByPast(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where b.booker_id=:userId " +
            " AND CURRENT_TIMESTAMP  between b.start_date AND b.end_date ",
            nativeQuery = true)
    List<Booking> findBookingsUserByCurrent(@Param("userId") Long userId, Pageable pageable);


    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where i.owner_id=:userId AND b.status=:status ",
            nativeQuery = true)
    List<Booking> findBookingsOwnerItemByStatus(@Param("userId") Long userId, String status, Pageable pageable);

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where i.owner_id=:userId AND (b.status='APPROVED' OR b.status='WAITING') AND b.start_date > CURRENT_TIMESTAMP ",
            nativeQuery = true)
    List<Booking> findBookingsOwnerItemByFuture(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where i.owner_id=:userId AND b.status='APPROVED' AND b.start_date < CURRENT_TIMESTAMP ",
            nativeQuery = true)
    List<Booking> findBookingsOwnerItemByPast(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where i.owner_id=:userId  AND CURRENT_TIMESTAMP  between b.start_date AND b.end_date ",
            nativeQuery = true)
    List<Booking> findBookingsOwnerItemByCurrent(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select * " +
            " from booking AS b " +
            "left join users as us on b.booker_id = us.user_id " +
            "left join items as i on b.item_id = i.item_id " +
            "where i.owner_id=:userId " +
            "ORDER BY b.start_date desc",
            nativeQuery = true)
    List<Booking> findByItemOwnerId(Long userId, Pageable pageable);
}