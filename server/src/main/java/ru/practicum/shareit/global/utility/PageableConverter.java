package ru.practicum.shareit.global.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageableConverter {
    private PageableConverter() {
    }

    public static PageRequest getPageable(Integer from, Integer size, Sort.Direction direction, String properties) {
        return PageRequest.of((from / size), size, direction, properties);
    }
}
