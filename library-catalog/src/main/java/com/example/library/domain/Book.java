package com.example.library.domain;

import java.util.Objects;

public final class Book {
    private final String isbn;   // normalized ISBN-13 (digits only)
    private final String title;
    private final String author;
    private final int year;

    public Book(String isbn, String title, String author, int year) {
        if (isbn == null || title == null || author == null) {
            throw new IllegalArgumentException("isbn, title, and author must be non-null");
        }
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override public int hashCode() { return Objects.hash(isbn); }

    @Override public String toString() {
        return "Book{" + "isbn='" + isbn + "', title='" + title + "', author='" + author + "', year=" + year + '}';
    }
}

