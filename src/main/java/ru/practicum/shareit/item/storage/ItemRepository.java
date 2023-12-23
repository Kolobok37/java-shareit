package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long userId, Pageable pageable);

    @Query(value = "select * " +
            " from items AS i " +
            "left join users as us on i.owner_id = us.user_id " +
            "left join requests as r on i.request_id = r.request_id " +
            "left join booking as b on i.next_booking_id = b.booking_id " +
            "left join booking as book on i.last_booking_id = book.booking_id " +
            "where (upper(i.name) like %:text% OR upper(i.description) like %:text%) AND i.available = true",
            nativeQuery = true)
    List<Item> searchItem(@Param("text") String text, Pageable pageable);
}