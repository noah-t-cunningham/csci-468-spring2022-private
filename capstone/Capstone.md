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
    
We did this becasue if we just returned a new list type there could potentially be a garbage collection error, additionally memoizing this will help
runtime in exchange for space. 

# Section 4: Technical writing. Include the technical document that accompanied your capstone project.

## s: jjkjk
var x = 'Cap'
print(x)

# Section 5: UML. 

Include a UML diagram for parse elements

# Section 6: Design trade-offs

To be discussed later in the class

# Section 7: Software development life cycle model

Describe the model that you used to develop your capstone project. How did this model help and/or hinder your team?

We are using Test Driven Development (TDD) for this project
