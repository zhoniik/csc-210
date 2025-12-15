package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.repo.BookRepository;
import com.example.library.util.IsbnValidator;

import java.util.List;
import java.util.Optional;

public class CatalogService {
    private final BookRepository repo;

    public CatalogService(BookRepository repo) {
        this.repo = repo;
    }

    public void addBook(Book book) {
        if (!IsbnValidator.isValidIsbn13(book.getIsbn())) {
            throw new IllegalArgumentException("Invalid ISBN-13: " + book.getIsbn());
        }
        repo.save(book);
    }

    public Optional<Book> getByIsbn(String isbn) {
        return repo.findByIsbn(isbn);
    }

    public List<Book> findByAuthor(String author) {
        return repo.findByAuthor(author);
    }

    public List<Book> listAll() {
        return repo.findAll();
    }

    public boolean removeByIsbn(String isbn) {
        return repo.deleteByIsbn(isbn);
    }
}

