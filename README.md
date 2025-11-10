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

## AI Usage

I used AI assistance (specifically Cursor's AI features) during the development of this project. AI helped with:
- Debugging when I got stuck on errors
- Implementing new features like arrays
- Understanding complex concepts from Crafting Interpreters (there do happen to be a lot of them)

The core structure and initial implementation came from following the Crafting Interpreters tutorial, but AI was useful for extending beyond what the book covered and fixing bugs. I tried to understand everything that was added, and the code reflects my learning process. If you're also learning, this project might be helpful to see how someone else (with AI assistance) built an interpreter.

## License

See [LICENSE](LICENSE) file for details.
