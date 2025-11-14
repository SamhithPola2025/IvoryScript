# IvoryScript

A lightweight, dynamically-typed scripting language implemented in Java. Simple syntax, easy to learn, fun to use.

## Features

- Dynamic typing
- Arrays and dictionaries
- Functions and classes with inheritance
- Built-in string methods
- REPL mode for interactive coding
- Clean, readable syntax

## Quick Start

1. Compile the project:
```bash
javac -d out src/com/mainsrc/ivoryscript/*.java
```

2. Run a file:
```bash
java -cp out com.mainsrc.ivoryscript.IvoryScript yourfile.ivory
```

3. Or start the REPL:
```bash
java -cp out com.mainsrc.ivoryscript.IvoryScript
```

## Recommended method to start the program:

4. Run the JAR file (or go to releases):
- To execute a script:
```bash
java -jar releases/IvoryScript.jar yourfile.ivory
```
- To start the REPL:
```bash
java -jar releases/IvoryScript.jar
```

## Documentation

Check out the [docs](docs/) folder for:
- [Getting Started](docs/getting-started.md) - How to build and run
- [Syntax Guide](docs/syntax.md) - Language syntax reference
- [Edge Cases and Rules](docs/edge-cases-and-rules.md) - Important gotchas
- [Built-in Functions](docs/built-in-functions.md) - Function reference
- [Examples](docs/examples.md) - Code examples

## Example

```ivory
var name = "World";
print "Hello, " + name + "!";

var arr = [1, 2, 3];
print arr[0];  // 1

fun greet(name) {
    return "Hello, " + name;
}

print greet("Alice");
```

## About This Project

This interpreter follows the structure and approach from [Crafting Interpreters](https://craftinginterpreters.com/) by Robert Nystrom. The book is an amazing resource for learning how interpreters work, and this project implements many of the concepts from it.

I'm still learning programming and language implementation, so this isn't the most complex or optimized interpreter out there. But it works, and building it has been a great learning experience. The code is straightforward and readable, which makes it easier to understand and modify if you're also learning.


## License

See [LICENSE](LICENSE) file for details.