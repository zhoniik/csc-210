package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.repo.InMemoryBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogServiceTest {
    private CatalogService service;

    @BeforeEach
    void setUp() {
        service = new CatalogService(new InMemoryBookRepository());
        service.addBook(new Book("9780134685991", "Effective Java", "Joshua Bloch", 2018));
        service.addBook(new Book("9780596009205", "Head First Java", "Kathy Sierra", 2005));
        service.addBook(new Book("9780132350884", "Clean Code", "Robert C. Martin", 2008));
    }

    @Test
    void addAndFetchByIsbn() {
        assertTrue(service.getByIsbn("9780134685991").isPresent());
        assertFalse(service.getByIsbn("0000000000000").isPresent());
    }

    @Test
    void findByAuthorIsCaseInsensitive() {
        List<?> martins = service.findByAuthor("robert c. martin");
        assertEquals(1, martins.size());
    }

    @Test
    void removeByIsbnRemoves() {
        assertTrue(service.removeByIsbn("9780596009205"));
        assertFalse(service.getByIsbn("9780596009205").isPresent());
    }

    @Test
    void addingInvalidIsbnThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                service.addBook(new Book("invalid", "Bad Book", "Nobody", 2000)));
    }
}

