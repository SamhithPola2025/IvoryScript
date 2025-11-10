# IvoryScript Examples

Here are some practical examples to get you started. Copy and paste these, modify them, break them, learn from them.

## Basic Variables and Math

```ivory
var x = 10;
var y = 20;
var sum = x + y;
print sum;  // 30

var name = "Alice";
var age = 25;
print name + " is " + age + " years old";
```

## Working with Arrays

```ivory
var numbers = [1, 2, 3, 4, 5];
print numbers[0];        // 1
print numbers.length;    // 5

numbers[2] = 99;
print numbers;           // [1, 2, 99, 4, 5]

var mixed = [1, "hello", true, 3.14];
print mixed;
```

## Working with Dictionaries

```ivory
var person = {
    "name": "Bob",
    "age": 30,
    "city": "NYC"
};

print person["name"];    // Bob
print person["age"];     // 30

person["job"] = "Developer";
print person;
```

## Loops

### While Loop

```ivory
var i = 0;
while (i < 5) {
    print i;
    i = i + 1;
}
```

### For Loop

```ivory
for (var i = 0; i < 5; i = i + 1) {
    print i;
}
```

### Iterating Over an Array

```ivory
var arr = [10, 20, 30, 40];
for (var i = 0; i < arr.length; i = i + 1) {
    print arr[i];
}
```

## Functions

### Simple Function

```ivory
fun greet(name) {
    print "Hello, " + name + "!";
}

greet("Alice");
greet("Bob");
```

### Function with Return

```ivory
fun square(x) {
    return x * x;
}

var result = square(5);
print result;  // 25
```

### Multiple Parameters

```ivory
fun add(a, b) {
    return a + b;
}

fun multiply(a, b) {
    return a * b;
}

print add(5, 3);        // 8
print multiply(4, 7);   // 28
```

## Conditionals

### If/Else

```ivory
var age = 20;

if (age >= 18) {
    print "Adult";
} else {
    print "Minor";
}
```

### Nested If

```ivory
var score = 85;

if (score >= 90) {
    print "A";
} else if (score >= 80) {
    print "B";
} else if (score >= 70) {
    print "C";
} else {
    print "F";
}
```

## Choose Statement

```ivory
var day = 3;

choose (day) {
    option 1:
        print "Monday";
        disrupt;
    option 2:
        print "Tuesday";
        disrupt;
    option 3:
        print "Wednesday";
        disrupt;
    otherwise:
        print "Other day";
}
```

## Classes

### Simple Class

```ivory
class Person {
    fun introduce() {
        print "Hi, I'm a person";
    }
}

var p = Person();
p.introduce();
```

### Class with Methods

```ivory
class Calculator {
    fun add(a, b) {
        return a + b;
    }
    
    fun subtract(a, b) {
        return a - b;
    }
}

var calc = Calculator();
print calc.add(10, 5);        // 15
print calc.subtract(10, 5);   // 5
```

### Inheritance

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
    
    fun bark() {
        print "Bark bark!";
    }
}

var dog = Dog();
dog.speak();  // Woof!
dog.bark();   // Bark bark!
```

### Using Super

```ivory
class Vehicle {
    fun start() {
        print "Vehicle starting";
    }
}

class Car < Vehicle {
    fun start() {
        print "Car starting";
        super.start();
    }
}

var car = Car();
car.start();
// Prints:
// Car starting
// Vehicle starting
```

### Method Overriding and Inheritance

```ivory
class Shape {
    fun area() {
        return 0;
    }
    
    fun describe() {
        print "I am a shape";
    }
}

class Rectangle < Shape {
    fun init(w, h) {
        this.width = w;
        this.height = h;
    }
    
    fun area() {
        return this.width * this.height;
    }
}

var rect = Rectangle(5, 10);
print rect.area();        // 50 (uses Rectangle's area)
rect.describe();          // "I am a shape" (uses Shape's describe, not overridden)
```

## String Methods

```ivory
var text = "Hello World";

print text.length();              // 11
print text.substring(0, 5);       // Hello
print text.substring(6, 11);      // World
print text.toUpper();             // HELLO WORLD
print text.toLower();             // hello world
print text[0];                    // H
print text[6];                    // W
```

## Nested Data Structures

```ivory
var company = {
    "name": "Acme Corp",
    "employees": [
        {"name": "Alice", "age": 30},
        {"name": "Bob", "age": 25},
        {"name": "Charlie", "age": 35}
    ],
    "departments": {
        "engineering": 10,
        "sales": 5,
        "marketing": 3
    }
};

print company["name"];
print company["employees"][0]["name"];           // Alice
print company["departments"]["engineering"];     // 10
```

## Practical Example: Simple Calculator

```ivory
fun calculate(operation, a, b) {
    if (operation == "add") {
        return a + b;
    } else if (operation == "subtract") {
        return a - b;
    } else if (operation == "multiply") {
        return a * b;
    } else if (operation == "divide") {
        return a / b;
    } else {
        return nil;
    }
}

print calculate("add", 10, 5);        // 15
print calculate("multiply", 4, 7);    // 28
```

## Practical Example: Finding Max in Array

```ivory
fun findMax(arr) {
    if (arr.length == 0) {
        return nil;
    }
    
    var max = arr[0];
    for (var i = 1; i < arr.length; i = i + 1) {
        if (arr[i] > max) {
            max = arr[i];
        }
    }
    return max;
}

var numbers = [3, 7, 2, 9, 1, 5];
print findMax(numbers);  // 9
```

## Practical Example: Counting Words

```ivory
fun countWords(text) {
    var words = [];
    var currentWord = "";
    
    for (var i = 0; i < text.length(); i = i + 1) {
        var char = text[i];
        if (char == " ") {
            if (currentWord != "") {
                words[words.length] = currentWord;
                currentWord = "";
            }
        } else {
            currentWord = currentWord + char;
        }
    }
    
    if (currentWord != "") {
        words[words.length] = currentWord;
    }
    
    return words.length;
}

print countWords("hello world");           // 2
print countWords("one two three four");    // 4
```

## More Complex: Simple Todo List

```ivory
class TodoList {
    fun init() {
        this.todos = [];
    }
    
    fun add(task) {
        this.todos[this.todos.length] = task;
    }
    
    fun list() {
        for (var i = 0; i < this.todos.length; i = i + 1) {
            print (i + 1) + ". " + this.todos[i];
        }
    }
    
    fun remove(index) {
        var newTodos = [];
        for (var i = 0; i < this.todos.length; i = i + 1) {
            if (i != index) {
                newTodos[newTodos.length] = this.todos[i];
            }
        }
        this.todos = newTodos;
    }
}

var todos = TodoList();
todos.add("Buy groceries");
todos.add("Write code");
todos.add("Walk the dog");
todos.list();
todos.remove(1);
todos.list();
```

These examples should give you a good starting point. Experiment, modify them, and build your own programs!

