package ru.practicum.shareit.item.db.model;

import lombok.*;
import ru.practicum.shareit.request.db.model.ItemRequest;
import ru.practicum.shareit.user.db.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 70, nullable = false)
    private String name;

    @Column(length = 200, nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private ItemRequest request;
}
