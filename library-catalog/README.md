# README

## Project Structure

```
library-catalog/
├── pom.xml
└── src
    ├── main
    │   └── java
    │       └── com
    │           └── example
    │               └── library
    │                   ├── App.java
    │                   ├── domain
    │                   │   └── Book.java
    │                   ├── repo
    │                   │   ├── BookRepository.java
    │                   │   └── InMemoryBookRepository.java
    │                   ├── service
    │                   │   └── CatalogService.java
    │                   └── util
    │                       └── IsbnValidator.java
    └── test
        └── java
            └── com
                └── example
                    └── library
                        ├── util
                        │   └── IsbnValidatorTest.java
                        └── service
                            └── CatalogServiceTest.java

```

## Build and Run

cd library-catalog

## Compile the project
`mvn compile`

## Run the project
`mvn exec:java -Dexec.mainClass="com.example.library.App"`

## Run tests
`mvn test`

## Run the app
`mvn exec:java`

## Package into a JAR
`mvn package`

## Run the JAR
`java -cp target/library-catalog-1.0-SNAPSHOT.jar com.example.library.App`





