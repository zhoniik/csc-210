# Testing

## Project Structure

```
junit-no-buildtool/
├─ lib/
│  └─ junit-platform-console-standalone-1.11.3.jar   # placed/auto-downloaded
├─ src/
│  ├─ main/
│  │  └─ com/example/
│  │     ├─ math/Calculator.java
│  │     └─ util/Strings.java
│  └─ test/
│     └─ com/example/
│        ├─ math/CalculatorTest.java
│        └─ util/StringsTest.java
├─ compile.sh
├─ test.sh
├─ compile.bat
└─ test.bat


## Compile the Code

`./compile.sh`

If you are on Windows and cannot run shell scripts, you can run this whole command instead:

`javac -cp lib/junit-platform-console-standalone-1.11.3.jar -d out src/main/com/example/math/Calculator.java src/main/com/example/util/Strings.java src/myprog/com/myprog/main/Main.java src/test/com/example/math/CalculatorTest.java src/test/com/example/util/StringsTest.java`


## Run the Code

The Main class in the package com.myprog.main contains a program that uses the library code.

After compiling successfully, you can run it with the following command:

`java -cp out/myprog:out/main com.myprog.main.Main`

Note the -cp flag which directs java to use the directories out/myprog and out/main as context for byte code files for execution/dependencies

There is also a convenience script that you can use named `run.sh` if you have a Unix-like system (MacOS or Linux or Unix itself)

`./run.sh`

## Run the Tests

`./test.sh`

If you are on Windows and running the script file is not an option for you, you can run the full test command with this:

`java -jar "lib/junit-platform-console-standalone-1.11.3.jar" -cp "out/main:out/test" --scan-class-path --fail-if-no-tests`


## Clean Out Build Files

`./clean.sh`

If you are on Windows and running the script file is not an option, simply find all of the files in the out directory and delete them. They should all end in .class. Fortunately this can be done manually as there are not too many of them. 
