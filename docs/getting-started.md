# Getting Started with IvoryScript

So you want to run some IvoryScript code? Here's how to get it working.

## Prerequisites

You need Java installed. That's it. Check if you have it:

```bash
java -version
```

If that works, you're good to go.

## Building

First, compile the Java source files:

```bash
javac -d out src/com/mainsrc/ivoryscript/*.java
```

This compiles everything into the `out` directory. If you see errors, make sure you're in the project root directory.

## Running a File

Once compiled, you can run an IvoryScript file like this:

```bash
java -cp out com.mainsrc.ivoryscript.IvoryScript yourfile.ivory
```

Replace `yourfile.ivory` with whatever file you want to run. The `.ivory` extension is just a convention - you can name it whatever you want.

## REPL Mode

If you run the interpreter without any arguments, it starts a REPL (Read-Eval-Print Loop):

```bash
java -cp out com.mainsrc.ivoryscript.IvoryScript
```

You'll see a `>` prompt. Type code and press enter to execute it:

```
> var x = 10;
> print x;
10
> var arr = [1, 2, 3];
> print arr;
[1, 2, 3]
> 
```

Press Ctrl+C (or Ctrl+D on some systems) to exit.

## Quick Test

Try running the example file:

```bash
java -cp out com.mainsrc.ivoryscript.IvoryScript docs/example.ivory
```

If that works and prints a bunch of output, you're all set!

## Writing Your First Program

Create a file called `hello.ivory`:

```ivory
var name = "World";
print "Hello, " + name + "!";
```

Then run it:

```bash
java -cp out com.mainsrc.ivoryscript.IvoryScript hello.ivory
```

You should see `Hello, World!` printed.

## Troubleshooting

**"Error: Could not find or load main class"**
- Make sure you compiled first with `javac`
- Check that you're using the correct classpath: `-cp out`

**"Error: file not found"**
- Make sure the file path is correct
- Use quotes if your filename has spaces: `"my file.ivory"`

**Compilation errors**
- Make sure you're in the project root directory
- Check that all source files are present in `src/com/mainsrc/ivoryscript/`

That's it! You're ready to start coding in IvoryScript.

