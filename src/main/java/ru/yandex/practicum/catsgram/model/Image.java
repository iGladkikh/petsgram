package ru.yandex.practicum.catsgram.model;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Image {
    Long id;
    long postId;
    String originalFileName;
    String filePath;
}
