# README

## Project Structure

```
simple-maven-project/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    └── App.java
```

## Compile the project
`mvn compile`

## Run the project
`mvn exec:java -Dexec.mainClass="com.example.App"`

## Or package into a JAR
`mvn package`

## Run the JAR
`java -cp target/simple-maven-project-1.0-SNAPSHOT.jar com.example.App`

