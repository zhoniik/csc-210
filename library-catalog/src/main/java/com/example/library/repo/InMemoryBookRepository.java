package com.example.library.repo;

import com.example.library.domain.Book;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> byIsbn = new ConcurrentHashMap<>();

    @Override public void save(Book book) {
        byIsbn.put(book.getIsbn(), book);
    }

    @Override public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(byIsbn.get(isbn));
    }

    @Override public List<Book> findByAuthor(String author) {
        return byIsbn.values().stream()
                .filter(b -> b.getAuthor().equalsIgnoreCase(author))
                .sorted(Comparator.comparing(Book::getYear))
                .collect(Collectors.toList());
    }

    @Override public List<Book> findAll() {
        return byIsbn.values().stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override public boolean deleteByIsbn(String isbn) {
        return byIsbn.remove(isbn) != null;
    }
}

