package com.example.library;

import com.example.library.domain.Book;
import com.example.library.repo.InMemoryBookRepository;
import com.example.library.service.CatalogService;

public class App {
    public static void main(String[] args) {
        var service = new CatalogService(new InMemoryBookRepository());

        // Valid ISBN-13 examples
        service.addBook(new Book("9780306406157", "Theoretical Physics", "Doe", 1979));
        service.addBook(new Book("9780134685991", "Effective Java", "Joshua Bloch", 2018));

        System.out.println("All books:");
        service.listAll().forEach(System.out::println);

        System.out.println("\nFind by ISBN:");
        System.out.println(service.getByIsbn("9780134685991").orElse(null));
    }
}

