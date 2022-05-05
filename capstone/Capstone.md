# Section 1: Program

Please include a zip file of the final repository in this directory.

# Section 2: Teamwork

Describe how your team worked on this capstone project. List each team member’s primary contributions and estimate the percentage of time that was spent by each team member on the project. Identify team members generically as team member 1, team member 2, etc.

Our Group was comprised of two Team Members. Team Member 1 refers to me and Team Member 2 refers to my partner.

Team Member 1 was responsible for writing all the code to pass the tokenzier, parser, evaluation and bytecode tests.

Team Member 2 was responsible for writing 3 additional tests for the Compiler to pass and The Technical Documentation.

We Communicated through Discord and met in Person to coordinate and discuss the requirments.

Team Member 1 contributed 120-150 hours to make the compiler while Team Member contributed 2-4 hours to complete the tests and documentation. 

# Section 3: Design pattern

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
    
In computing, memoization is applied when dealing with expensive recursive function calls and helps to optimize this process. This technique stores the results of expensive function calls and caches the result. So the next time the fucntion is called you can just look it up in the cache inseatd of repeatong the function call. 

The benifits of doing this are that it will save time for costly recurisve computaions. Also If we didnt do this there potentially could be some garbage collection errors depending on the language. Additionally memoizing could help runtime, but in exchange we had to gove up some memory space. 

# Section 4: Technical writing. Include the technical document that accompanied your capstone project.

## Introduction:

This Project is a Compiler for the Catscript Language written in Java. Catscrpit a statically typed
language and supports standard expressiosn and statements that will all be outlined below. 

## Features:

### Expressiosns:

#### Multiplicication:

    var fifteen = 3*5
    print(fifteen)

output: 15

#### Division:

    var five = 10/2
    print(five)

output: 5

#### Addition:

    var two = 1+1
    print(two)

output: 2

#### Subtraction:

    var thirteen = 14-1
    print(thirteen)

output: 13

#### Compound:

    var three = (10-1) * 2 / (4 + 2)
    print(three)

output: 3

#### Comparison:

    var x = 2

##### Equals:

    print(x == 1)
    print(x == 2)
    print(x == 3)

output: false true false

##### Not Equals:

    print(x != 1)
    print(x != 2)
    print(x != 3)

output: true false true

##### Greater than or Equal:

    print(x >= 1)
    print(x >= 2)
    print(x >= 3)

output: true true false

##### Less than or Equal:

    print(x <= 1)
    print(x <= 2)
    print(x <= 3)

ouput: false true true

##### Greater than:

    print(x > 1)
    print(x > 2)
    print(x > 3)

output: true false false

##### Less than:

    print(x < 1)
    print(x < 2)
    print(x < 3)

output: false false true

#### Unary: 

    var x = not true
    print(x)

output: false

    var x = -1
    print(x)

output: -1

### Statements:

#### Print:

    print("Hello World")

output: Hello World

#### Variable:

    var x = "Variable"
    print(x)

output: Variable

#### Assignment:

    var x = 0
    x = 1+1
    print(x)

output: 2

#### If:

    var x = 1
    var y = 2
    var z = 3

##### Default If:

    if(x < y){
        print(x)
    } 

ouput: 1

##### If with Else If:

    if(x > y){
        print(x)
    } else if(y < z){
        print(y)
    }

ouput: 2

##### If with Else If and Else:

    if(x > y){
        print(x)
    } else if(y > z){
        print(y)
    }else{
        print(z)
    }

output: 3

#### For:

    for(x in [1, 2, 3]) { print(x) }

output: 1 2 3

#### Function:
    
##### Function Decleration:
    
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

##### Function Call:

    func1(1,2,3)

    var x = func2(10, true)
    print(x)

output: 1 2 3 10


# Section 5: UML. 

Include a UML diagram for parse elements

# Section 6: Design trade-offs

Decided to use recursive decent parsing(which is a top down parsing algorithm) instead of a parser generator. We chose to do this becasue recursive decent parsing is much simpler and easier to understnd than a parser generator. Also most production parsers are recursive descent, so it will give us a better undetsanding of how real world compiler work. However recursive decent parsing required me to write much more code than a parser generator.

Parse Tree Nodes Evaluate and Compile directly, Instead of using the visitor pattern or some other way to do that. This is a simpler way to do this becasue we are not 'seperating our concerns'. Everything is in one place, so if errors occur it will be easier to diagnose the problem. 

Created a method to check the next token, and used it in parseAssighnmentStatement() and parseIfStatement(). This wasnt nessecary but It made it simpler to check the next token without consuming it, if i didnt add this fucntion I would have had to write more code in parseAssighnmentStatement() and parseIfStatement() to get around that problem.  

# Section 7: Software development life cycle model

Describe the model that you used to develop your capstone project. How did this model help and/or hinder your team?

We are using Test Driven Development (TDD) for this project. The positives are that its extremly staright forward, and you know exactly what to do. I personally have loved using TDD in this class and would like to impement in my professional career if I can. the only negative is that you have to come up with good tetst that capture the scope of your project, which can be challengin and time consuming. 
