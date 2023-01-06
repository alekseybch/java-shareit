package ru.practicum.shareit.request.db.model;

import lombok.*;
import ru.practicum.shareit.user.db.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_requests")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "id", nullable = false)
    private User requestor;

    @Column(nullable = false)
    private LocalDateTime created;
}
