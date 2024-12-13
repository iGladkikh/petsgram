package ru.yandex.practicum.catsgram.service;

public enum SortOrder {
    ASC, DESC;

    // Преобразует строку в элемент перечисления
    public static SortOrder from(String order) {
        return switch (order.toLowerCase()) {
            case "asc" -> ASC;
            case "desc" -> DESC;
            default -> null;
        };
    }
}
