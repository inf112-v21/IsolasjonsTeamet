# INF112 Maven template 
Simple skeleton with libgdx. 


## Gradle primer
Gradle is a build tool just like Maven, but it also differs in many areas.
The two most obvious are the commands you use to do something, and the syntax
you use in the build file.

### The syntax
Gradle build scripts are programs written in Groovy (or more recently Kotlin).
You can think of Groovy as a dynamically typed Java with some other changes here and there. 
You can define functions and variables, pass them around, and all that. 
For the most part for our usecase, we hopefully won't need to use this 
programmatic aspect of Gradle too much at the start. Instead, we'll save it 
for if we need to do anything complex.

### The commands

To invoke Gradle, you use `gradlew`. If you're in the old windows command prompt, 
you use it like so `gradlew <command>`. Otherwise you use it like so `./gradlew <command>`. 

#### Compile
To compile the project, use `compileJava`. You can also use `compileTestJava` to compile the tests.

### Run
To run the project, use `run`.

### Tests
To run the tests, use `test`.

### Build
To a build a distributable, use `build`. A distribution will be generated in 
`build/distributions` which contains everything needed to run the game.

### Run CheckStyle
To run CheckStyle, use `checkstyleMain`.


## Known bugs
