# Edge Cases and Rules

Here are the weird quirks and gotchas you should know about. Some of these are intentional, some are just how it works.

## Type Rules

### Numbers
All numbers are doubles internally. When you write `5`, it's actually `5.0`. When printing, trailing `.0` gets stripped, so `5.0` prints as `5`, but `5.5` prints as `5.5`.

### Dictionary Keys
Dictionary keys **must** be strings. You can't use numbers, booleans, or anything else as keys. This will throw an error:

```ivory
var dict = {5: "value"};  // Error: Dictionary keys must be strings
```

You have to do:

```ivory
var dict = {"5": "value"};  // This works
```

### String Concatenation
The `+` operator does different things depending on what you're adding:
- Two numbers: addition
- At least one string: concatenation (converts both to strings)

```ivory
5 + 3           // 8 (addition)
"5" + 3         // "53" (concatenation)
5 + "3"         // "53" (concatenation)
"hello" + "world"  // "helloworld"
```

You can't add two non-number, non-string values. That'll error out.

## Indexing Rules

### Array Indexing
- Indices must be numbers
- Indices must be within bounds (0 to length-1)
- Negative indices or out-of-bounds indices throw errors

```ivory
var arr = [1, 2, 3];
arr[0]          // 1 (works)
arr[2]          // 3 (works)
arr[5]          // Error: Array index out of bounds
arr[-1]         // Error: Array index out of bounds
arr["0"]        // Error: Array index must be a number
```

### Dictionary Indexing
- Keys must be strings
- Accessing a non-existent key throws an error (unlike some languages that return nil)

```ivory
var dict = {"a": 1};
dict["a"]       // 1 (works)
dict["b"]       // Error: Key 'b' not found in dictionary
dict[0]         // Error: Dictionary key must be a string
```

### String Indexing
- Indices must be numbers
- Indices must be within bounds (0 to length-1)
- Returns a single-character string, not a char

```ivory
var str = "hello";
str[0]          // "h" (works)
str[4]          // "o" (works)
str[10]         // Error: String index out of bounds
str[-1]         // Error: String index out of bounds
```

You can't assign to string indices. Strings are immutable.

## Assignment Rules

You can only assign to:
- Variables: `x = 5;`
- Object properties: `obj.field = 5;`
- Array indices: `arr[0] = 5;`
- Dictionary keys: `dict["key"] = 5;`

You **cannot** assign to:
- String indices: `str[0] = "x";` (Error: Can only assign to array or dictionary indices)
- Literal values: `5 = 10;` (parse error)
- Function calls: `func() = 5;` (parse error)

## Function Calls

### Arity Checking
Functions check the exact number of arguments. Too many or too few will error:

```ivory
fun add(a, b) {
    return a + b;
}

add(1, 2)       // 3 (works)
add(1)          // Error: Expected 2 arguments but got 1
add(1, 2, 3)    // Error: Expected 2 arguments but got 3
```

### What Can Be Called
Only functions and classes can be called. Trying to call anything else errors:

```ivory
var x = 5;
x();            // Error: Can only call functions and classes
```

## Super and This

### Super Restrictions
- `super` can only be used inside class methods
- `super` can only be used when there's actually a superclass
- `super` must be followed by a method that exists in the superclass

```ivory
super.speak();  // Error if not in a class method
```

### This Restrictions
- `this` can only be used inside class methods
- `this` refers to the current instance

If you try to use `this` outside a method, you'll get an "Undefined variable" error.

## Inheritance and Superclasses

### Superclass Rules
- A class can only inherit from one superclass (single inheritance)
- The superclass must be defined before the subclass tries to inherit from it
- If you try to inherit from a non-existent class, you'll get an error

```ivory
class Dog < Animal { }  // Error if Animal doesn't exist
```

### Method Resolution
When you call a method on an instance:
1. First, it looks in the instance's own class
2. If not found, it looks in the superclass
3. If not found there, it looks in the superclass's superclass, and so on
4. If still not found, you get an "Undefined property" error

```ivory
class Animal {
    fun speak() { print "Animal"; }
}

class Dog < Animal {
    fun bark() { print "Woof"; }
}

var dog = Dog();
dog.speak();  // Works - found in Animal
dog.bark();   // Works - found in Dog
dog.meow();   // Error - not found anywhere
```

### Method Overriding
When a subclass defines a method with the same name as the superclass, the subclass method takes precedence:

```ivory
class Animal {
    fun speak() { print "Animal sound"; }
}

class Dog < Animal {
    fun speak() { print "Woof!"; }
}

var dog = Dog();
dog.speak();  // Prints "Woof!" (Dog's version, not Animal's)
```

To call the superclass version, use `super`:

```ivory
class Dog < Animal {
    fun speak() {
        print "Woof!";
        super.speak();  // Calls Animal's speak()
    }
}
```

## Truthiness

The following are **falsy**:
- `false`
- `nil`

Everything else is **truthy**:
- `true`
- `0` (yes, zero is truthy)
- `""` (empty string is truthy)
- `[]` (empty array is truthy)
- `{}` (empty dictionary is truthy)

This means:

```ivory
if (0) {
    print "zero is truthy";
}

if ("") {
    print "empty string is truthy";
}

if (nil) {
    print "this won't print";
}
```

## Choose Statement Behavior

The `choose` statement has fall-through behavior. Once a match is found, it continues executing all subsequent cases until it hits a `disrupt`:

```ivory
var x = 1;
choose (x) {
    option 1:
        print "one";
    option 2:
        print "two";
    otherwise:
        print "default";
}
```

This will print:
```
one
two
default
```

You need `disrupt` to stop it:

```ivory
var x = 1;
choose (x) {
    option 1:
        print "one";
        disrupt;
    option 2:
        print "two";
        disrupt;
    otherwise:
        print "default";
}
```

Now it only prints `one`.

## Variable Scoping

Variables are function-scoped, not block-scoped. A variable declared in a block is accessible outside that block:

```ivory
if (true) {
    var x = 10;
}
print x;  // This works, prints 10
```

This is probably not what you want, but that's how it works right now.

## Property Access

### What Has Properties
Only these things have properties you can access with `.`:
- Class instances
- Strings (for methods like `.length()`)
- Arrays (only `.length`)
- Dictionaries (only `.length`)

Everything else will error:

```ivory
var x = 5;
x.length;       // Error: Only instances, strings, arrays, and dictionaries have properties
```

### Setting Properties
You can only set properties on class instances:

```ivory
var arr = [1, 2, 3];
arr.length = 10;    // Error: Only instances have fields
```

## Division by Zero

Division by zero throws a runtime error:

```ivory
var x = 5 / 0;  // Error: Division by zero
```

## Operator Restrictions

### Arithmetic Operators
`+`, `-`, `*`, `/` require numbers (except `+` which can concatenate strings):

```ivory
5 - "3"         // Error: Operands must be numbers
5 * true        // Error: Operands must be numbers
```

### Comparison Operators
`==` and `!=` work on any types. `nil == nil` is `true`. Everything else uses Java's `.equals()`.

`<`, `>`, `<=`, `>=` only work on numbers:

```ivory
5 < 10          // true (works)
"a" < "b"       // Error: Operands must be numbers
```

## Return Behavior

If a function doesn't explicitly return a value, it returns `nil`:

```ivory
fun doSomething() {
    print "doing stuff";
}

var result = doSomething();
print result;  // prints "nil"
```

## Error Handling

When a runtime error occurs, the interpreter prints the error and stops execution. It doesn't continue running the rest of the program. There's no try/catch mechanism.

## String Methods

String methods return new values, they don't modify the original:

```ivory
var str = "hello";
str.toUpper();      // Returns "HELLO"
print str;          // Still prints "hello"
```

If you want to change it, you need to reassign:

```ivory
var str = "hello";
str = str.toUpper();
print str;          // Now prints "HELLO"
```

## Substring Bounds

The `substring(start, end)` method requires:
- Both arguments must be numbers
- `start` must be >= 0
- `end` must be <= string length
- `start` must be <= `end`

```ivory
var str = "hello";
str.substring(0, 5)     // "hello" (works)
str.substring(0, 10)    // Error: Invalid substring indices
str.substring(5, 0)     // Error: Invalid substring indices
```

## Empty Collections

Empty arrays and dictionaries are truthy:

```ivory
if ([]) {
    print "empty array is truthy";
}

if ({}) {
    print "empty dict is truthy";
}
```

Their `.length` property returns `0`:

```ivory
[].length       // 0
{}.length       // 0
```

## That's About It

These are the main gotchas. Most of them make sense once you know about them, but some are just quirks of the implementation. If you hit something weird, check back here.

