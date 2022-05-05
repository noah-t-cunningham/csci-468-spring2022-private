# Section 1: Program

Please include a zip file of the final repository in this directory.

# Section 2: Teamwork

Describe how your team worked on this capstone project. List each team member’s primary contributions and estimate the percentage of time that was spent by each team member on the project. Identify team members generically as team member 1, team member 2, etc.

My partner was a testing partner and provided 3 tests for my compiler. They also provided the documentaion for the CatScript Language. The compilers we made were colpletley done on our own. The compiler took about 120-150 hours to make while the tests and documentation took maybe an hour or two to come up with. 

# Section 3: Design pattern

Identify one design pattern that was used in your capstone project and describe exactly where in the code it is located. 

Highlight the design pattern in yellow. Explain why you used the pattern and didn’t just code directly.

The pattern I used was Memoization/Flywheel. The code is located in src/main/java/edu.montana.csci.csci468/parser/CatScriptType on line 36. 

    static HashMap<CatscriptType, ListType> cache = new HashMap<>();
    public static CatscriptType getListType(CatscriptType type) {
        ListType listType = cache.get(type);
        if(listType == null){
            listType = new ListType(type);
            cache.put(type, listType);
        }
        return new ListType(type);
    }
    
I did this becasue if I just returned a new list type there could potentially be a garbage collection error, additionally memoizing this will help
runtime in exchange for space. 

# Section 4: Technical writing. Include the technical document that accompanied your capstone project.

## Expressiosns:

### Multiplicication:

    var fifteen = 3*5
    print(fifteen)

output: 15

### Division:

    var five = 10/2
    print(five)

output: 5

### Addition:

    var two = 1+1
    print(two)

output: 2

### Subtraction:

    var thirteen = 14-1
    print(thirteen)

output: 13

### Compound:

    var three = (10-1) * 2 / (4 + 2)
    print(three)

output: 3

### Comparison:

    var x = 2

#### Equals:

    print(x == 1)
    print(x == 2)
    print(x == 3)

output: false true false

#### Not Equals:

    print(x != 1)
    print(x != 2)
    print(x != 3)

output: true false true

#### Greater than or Equal:

    print(x >= 1)
    print(x >= 2)
    print(x >= 3)

output: true true false

#### Less than or Equal:

    print(x <= 1)
    print(x <= 2)
    print(x <= 3)

ouput: false true true

#### Greater than:

    print(x > 1)
    print(x > 2)
    print(x > 3)

output: true false false

#### Less than:

    print(x < 1)
    print(x < 2)
    print(x < 3)

output: false false true

### Unary: 

    var x = not true
    print(x)

output: false

    var x = -1
    print(x)

output: -1

## Statements:

### Print:

    print("Hello World")

output: Hello World

### Variable:

    var x = "Variable"
    print(x)

output: Variable

### Assignment:

    var x = 0
    x = 1+1
    print(x)

output: 2

### If:

    var x = 1
    var y = 2
    var z = 3

#### Default If:

    if(x < y){
        print(x)
    } 

ouput: 1

#### If with Else If:

    if(x > y){
        print(x)
    } else if(y < z){
        print(y)
    }

ouput: 2

#### If with Else If and Else:

    if(x > y){
        print(x)
    } else if(y > z){
        print(y)
    }else{
        print(z)
    }

output: 3

### For:

    for(x in [1, 2, 3]) { print(x) }

output: 1 2 3

### Function:
    
#### Function Decleration:
    
    function func1(a, b, c){
        print(a)
        print(b)
        print(c)
    }

    function func2 (x:int, y:bool) : int{
        if(y == true){
            return x
        }
    }

#### Function Call:

    func1(1,2,3)

    var x = func2(10, true)
    print(x)

output: 1 2 3 10


# Section 5: UML. 

Include a UML diagram for parse elements

# Section 6: Design trade-offs

To be discussed later in the class

# Section 7: Software development life cycle model

Describe the model that you used to develop your capstone project. How did this model help and/or hinder your team?

We are using Test Driven Development (TDD) for this project
