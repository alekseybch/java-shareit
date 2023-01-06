package ru.practicum.shareit.global.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.global.exception.BadPageRequestException;

public class PageableConverter {
    private PageableConverter(){
    }

    public static PageRequest getPageable(Integer from, Integer size, Sort.Direction direction, String properties) {
        if (from < 0 || size < 0) {
            throw new BadPageRequestException(String.format("Bad pageable request from = %d, size = %d", from, size));
        }
        return PageRequest.of((from / size), size, direction, properties);
    }
}
