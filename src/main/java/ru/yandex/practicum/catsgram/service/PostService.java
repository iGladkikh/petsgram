package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
@RequiredArgsConstructor
public class PostService {
    private static final int DEFAULT_PAGINATION_SIZE = 10;
    private static final Comparator<Post> defaultComparator = Comparator.nullsLast(Comparator.comparing(Post::getPostDate).reversed());
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public Collection<Post> findAll(Map<String, String> params) {
        if (params.containsKey("sort") && SortOrder.from(params.get("sort")) == null) {
            throw new ParameterNotValidException("sort", "Получено: " + params.get("sort") + " должно быть: ask или desc");
        }

        if (params.containsKey("size") && Integer.parseInt(params.get("size")) <= 0) {
            throw new ParameterNotValidException("size", "Размер должен быть больше нуля");
        }

        if (params.containsKey("from") && Integer.parseInt(params.get("from")) < 0) {
            throw new ParameterNotValidException("from", "Начало выборки должно быть положительным числом");
        }

        return posts.values().stream()
                .sorted(params.containsKey("sort")
                        ? getPostDateComparator(params.get("sort"))
                        : defaultComparator)
                .skip(params.containsKey("from") && params.get("from") != null
                        ? Long.parseLong(params.get("from"))
                        : 0)
                .limit(params.containsKey("size") && params.get("size") != null
                        ? Long.parseLong(params.get("size"))
                        : DEFAULT_PAGINATION_SIZE)
                .toList();
    }

    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post create(Post post) {
        if (userService.findById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException(String.format("Автор с id = %d не найден", post.getAuthorId()));
        }

        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (userService.findById(newPost.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException(String.format("Автор с id = %d не найден", newPost.getAuthorId()));
        }

        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private static Comparator<Post> getPostDateComparator(String param) {
        if (SortOrder.from(param) == SortOrder.DESC) {
            return Comparator.nullsLast(Comparator.comparing(Post::getPostDate).reversed());
        }
        return Comparator.nullsLast(Comparator.comparing(Post::getPostDate));
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
