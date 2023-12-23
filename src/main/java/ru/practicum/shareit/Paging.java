package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ValidationDataException;

import java.util.Optional;

public class Paging {
    public static Pageable paging(Integer from, Optional<Integer> size) {
        Pageable pageable;
        if (from < 0 || size.isPresent() && size.get() < 1) {
            throw new ValidationDataException("Date is not valid.");
        }
        if (size.isPresent()) {
            pageable = PageRequest.of(from / size.get(), size.get());
        } else
            pageable = PageRequest.of(from, 100);
        return pageable;
    }

    public static Pageable paging(Integer from, Optional<Integer> size, Sort sort) {
        Pageable pageable;
        if (from < 0 || size.isPresent() && size.get() < 1) {
            throw new ValidationDataException("Date is not valid.");
        }
        if (size.isPresent()) {
            pageable = PageRequest.of(from / size.get(), size.get(), sort);
        } else
            pageable = PageRequest.of(from, 100, sort);
        return pageable;
    }
}
