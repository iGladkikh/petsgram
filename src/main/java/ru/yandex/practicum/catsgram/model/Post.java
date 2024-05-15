package ru.yandex.practicum.catsgram.model;

import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Post {
    Long id;
    long authorId;
    String description;
    Instant postDate;
}
