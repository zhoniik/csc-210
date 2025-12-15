# Packages Project


## Project Structure

```
project/
├── app/
│   └── com/example/app/Main.java
├── math/
│   └── com/example/math/Calculator.java
└── util/
    └── com/example/util/Printer.java

```

## Build Project

`javac -d out $(find . -name "*.java")`

## Run Program

`java -cp out com.example.app.Main



