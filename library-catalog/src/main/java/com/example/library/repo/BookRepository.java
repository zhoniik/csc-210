package com.example.library.repo;

import com.example.library.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    void save(Book book);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByAuthor(String author);
    List<Book> findAll();
    boolean deleteByIsbn(String isbn);
}

