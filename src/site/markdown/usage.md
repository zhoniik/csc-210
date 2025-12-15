# Usage

Build and run reports locally:

```bash
mvn clean verify           # runs tests and produces JaCoCo coverage
mvn javadoc:javadoc        # generates API docs in target/site/apidocs
mvn site                   # builds the complete site under target/site
mvn site:run               # preview the site at http://localhost:8080

