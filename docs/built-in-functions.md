# Built-in Functions Reference

These functions are always available in IvoryScript. You don't need to import anything or declare them.

## input()

Reads a line of input from standard input.

**Arity:** 0 (takes no arguments)

**Returns:** A string containing the line that was read

**Example:**
```ivory
print "What's your name?";
var name = input();
print "Hello, " + name + "!";
```

**Notes:**
- Blocks until the user presses Enter
- Returns the entire line as a string, including any spaces
- In REPL mode, this reads from the terminal
- If there's an error reading input, it throws a runtime error

## length(value)

Gets the length of a string, array, or dictionary.

**Arity:** 1

**Arguments:**
- `value` - A string, array, or dictionary

**Returns:** A number (the length)

**Example:**
```ivory
length("hello")           // 5
length([1, 2, 3, 4])      // 4
length({"a": 1, "b": 2})  // 2
```

**Notes:**
- For strings, returns the number of characters
- For arrays, returns the number of elements
- For dictionaries, returns the number of key-value pairs
- Throws an error if you pass anything else

## type(value)

Returns the type of a value as a string.

**Arity:** 1

**Arguments:**
- `value` - Any value

**Returns:** A string describing the type

**Possible return values:**
- `"nil"` - for nil values
- `"number"` - for numbers
- `"string"` - for strings
- `"boolean"` - for booleans
- `"array"` - for arrays
- `"dictionary"` - for dictionaries
- `"function"` - for functions
- `"class"` - for classes
- `"instance"` - for class instances
- `"unknown"` - for anything else (shouldn't happen)

**Example:**
```ivory
type(42)              // "number"
type("hello")         // "string"
type(true)            // "boolean"
type([1, 2, 3])       // "array"
type({"a": 1})        // "dictionary"
type(nil)             // "nil"

fun myFunc() {}
type(myFunc)          // "function"

class MyClass {}
type(MyClass)         // "class"

var obj = MyClass();
type(obj)             // "instance"
```

**Notes:**
- Useful for debugging or type checking
- All numbers return `"number"` (there's no distinction between int and float)

## toString(value)

Converts any value to its string representation.

**Arity:** 1

**Arguments:**
- `value` - Any value

**Returns:** A string

**Example:**
```ivory
toString(42)          // "42"
toString(true)        // "true"
toString(false)       // "false"
toString(nil)         // "nil"
toString([1, 2, 3])   // "[1, 2, 3]"
toString({"a": 1})    // "{\"a\": 1}"
```

**Notes:**
- Numbers are converted to strings (trailing `.0` is removed for whole numbers)
- Booleans become `"true"` or `"false"`
- `nil` becomes `"nil"`
- Arrays and dictionaries are formatted in their literal syntax
- This is the same conversion that happens when you use `+` for string concatenation

## That's All

There are only four built-in functions. The language keeps things simple. If you need more functionality, you can always write your own functions or use string/array methods.

