# IvoryScript Syntax Guide

So you want to write some IvoryScript? Cool. Here's how it works.

## The Basics

Everything ends with a semicolon. Yeah, I know, but it keeps things simple. Variables are declared with `var`, and you can assign values right away or leave them as `nil`.

```ivory
var x = 10;
var name = "hello";
var nothing;
```

Numbers are just numbers. Strings use double quotes. Booleans are `true` and `false`. And `nil` is our null value.

## Comments

You've got two options. Single line comments with `//`:

```ivory
var x = 5; // this is a comment
```

Or multi-line comments with `/* */`:

```ivory
/*
 * This is a multi-line comment
 * Pretty useful for longer explanations
 */
```

## Arrays

Arrays are just lists of stuff. Create them with square brackets:

```ivory
var arr = [1, 2, 3, "hello", true];
```

Access elements with `arr[index]`. Indexing starts at 0, like it should. You can also get the length with `arr.length`:

```ivory
var arr = [10, 20, 30];
print arr[0];        // prints 10
print arr.length;    // prints 3
arr[1] = 99;         // now arr is [10, 99, 30]
```

## Dictionaries

Dictionaries are key-value pairs. Keys have to be strings, values can be anything:

```ivory
var person = {"name": "Alice", "age": 30, "active": true};
```

Access values with `dict["key"]`:

```ivory
print person["name"];        // prints Alice
person["city"] = "NYC";      // adds a new key
print person.length;         // prints 4
```

## Operators

Math works like you'd expect:

```ivory
var sum = 5 + 3;      // 8
var diff = 10 - 4;    // 6
var prod = 2 * 3;     // 6
var quot = 8 / 2;     // 4
```

Comparison operators:

```ivory
5 == 5        // true
5 != 3        // true
5 < 10        // true
5 > 2         // true
5 <= 5        // true
5 >= 4        // true
```

Logical operators:

```ivory
true and false    // false
true or false     // true
!true             // false
```

String concatenation uses `+`:

```ivory
var greeting = "Hello" + " " + "World";  // "Hello World"
```

## Control Flow

### If/Else

Standard if statements:

```ivory
if (x > 10) {
    print "big";
} else {
    print "small";
}
```

### While Loops

Keep looping while a condition is true:

```ivory
var i = 0;
while (i < 5) {
    print i;
    i = i + 1;
}
```

### For Loops

For loops work like C-style loops:

```ivory
for (var i = 0; i < 5; i = i + 1) {
    print i;
}
```

You can leave parts empty:

```ivory
for (;;) {
    print "infinite loop";
}
```

### Break

Use `disrupt` to break out of loops. Yeah, I called it disrupt instead of break. Deal with it.

```ivory
while (true) {
    if (someCondition) {
        disrupt;
    }
}
```

### Choose Statement

This is basically a switch statement, but I called it `choose` because why not. Use `option` for cases and `otherwise` for default:

```ivory
var x = 2;
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

You need `disrupt` if you don't want it to fall through to the next case.

## Functions

Define functions with `fun`:

```ivory
fun greet(name) {
    print "Hello, " + name;
}

greet("Alice");
```

Functions can return values:

```ivory
fun add(a, b) {
    return a + b;
}

var result = add(5, 3);
```

## Classes

Classes are pretty straightforward. Define them with `class`:

```ivory
class Person {
    fun sayHello() {
        print "Hello!";
    }
}

var p = Person();
p.sayHello();
```

### Inheritance

Use `<` for inheritance. I know, it's weird, but it works:

```ivory
class Animal {
    fun speak() {
        print "Some sound";
    }
}

class Dog < Animal {
    fun speak() {
        print "Woof!";
    }
    
    fun superSpeak() {
        super.speak();
    }
}

var dog = Dog();
dog.speak();        // prints "Woof!"
dog.superSpeak();   // prints "Some sound"
```

Inside methods, `this` refers to the current instance. `super` lets you call parent class methods.

**Important notes about inheritance:**
- A class can only inherit from one superclass
- Methods in the subclass override methods in the superclass with the same name
- You can access overridden methods using `super.methodName()`
- If a method doesn't exist in the subclass, it looks in the superclass automatically
- `super` can only be used inside class methods, not at the top level

## Strings

Strings have some built-in methods:

```ivory
var str = "Hello World";

str.length()           // returns 11
str.substring(0, 5)    // returns "Hello"
str.toUpper()          // returns "HELLO WORLD"
str.toLower()          // returns "hello world"
```

You can also index strings like arrays:

```ivory
var str = "hello";
print str[0];    // prints "h"
print str[4];    // prints "o"
```

## Built-in Functions

There are a few built-in functions that are always available:

### type(value)

Returns the type of a value as a string:

```ivory
type(42)              // "number"
type("hello")         // "string"
type(true)            // "boolean"
type([1, 2, 3])       // "array"
type({"a": 1})        // "dictionary"
```

### length(value)

Gets the length of strings, arrays, or dictionaries:

```ivory
length("hello")       // 5
length([1, 2, 3])     // 3
length({"a": 1})      // 1
```

### toString(value)

Converts any value to a string:

```ivory
toString(42)          // "42"
toString(true)        // "true"
```

### input()

Reads a line from standard input:

```ivory
var name = input();
print "Hello, " + name;
```

## Property Access

You can access properties with the dot operator:

```ivory
var arr = [1, 2, 3];
print arr.length;     // 3

var str = "hello";
print str.length();   // 5

var dog = Dog();
dog.speak();
```

## Nested Structures

You can nest arrays and dictionaries:

```ivory
var data = {
    "users": [
        {"name": "Alice", "age": 30},
        {"name": "Bob", "age": 25}
    ],
    "count": 2
};

print data["users"][0]["name"];    // prints "Alice"
```

## That's It

Pretty much everything you need to know. The language is dynamically typed, so you don't need to declare types. Just write code and it should work. If something breaks, you'll get an error message telling you what went wrong.

Have fun coding!

