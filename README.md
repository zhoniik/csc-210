# README

## Project Structure

```
doc-demo/
├── pom.xml
├── src
│   ├── main
│   │   └── java
│   │       └── com
│   │           └── example
│   │               └── docs
│   │                   ├── Calculator.java
│   │                   └── package-info.java
│   ├── test
│   │   └── java
│   │       └── com
│   │           └── example
│   │               └── docs
│   │                   └── CalculatorTest.java
│   └── site
│       ├── site.xml
│       └── markdown
│           ├── index.md
│           └── usage.md

```

# From the project root
`mvn clean verify`

# Generate just the API docs
`mvn javadoc:javadoc`

# Build the full site (Javadoc, reports, and your markdown pages)
`mvn site`

# Preview locally
`mvn site:run   # then open http://localhost:8080`

# (Optional) Deploy the site somewhere (requires configuration)
`mvn site-deploy`

## Compile the project
`mvn compile`


