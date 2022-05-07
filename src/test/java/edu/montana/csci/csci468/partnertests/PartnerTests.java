package edu.montana.csci.csci468.partnertests;
import edu.montana.csci.csci468.CatscriptTestBase;
import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PartnerTests extends CatscriptTestBase{

    //tests i created
    @Test
    void ifElseElseStatementWorksProperly() {
        assertEquals("3\n", executeProgram("if(false){ print(1) }  else if(false){ print(2) } else{print(3)}"));
    }

    @Test
    void ifElseStatementWorksProperly() {
        assertEquals("2\n", executeProgram("if(false){ print(1) }  else if(true){ print(2) }"));
    }

    @Test
    public void elseIfStatementEnsuresClosingBrace() {
        IfStatement expr = parseStatement("if(9 > 10){ print(9) } else if(10 > 11) { ", false);
        assertNotNull(expr);
        assertTrue(expr.hasErrors());
    }

    // tests my partner created
    @Test
    void myCustomTestsWorkProperly() {
        // Practical Recursion
        assertEquals("1\n2\n6\n24\n", executeProgram("function factorial(x:int):int{\n" +
                "    if(x <= 1){\n" +
                "        return 1\n" +
                "    }\n" +
                "    return x * factorial(x - 1)\n" +
                "}\n" +
                "\n" +
                "var numbers = [1, 2, 3, 4]\n" +
                "\n" +
                "for(i in numbers){\n" +
                "    var f = factorial(i)\n" +
                "    print(f)\n" +
                "}"));

        // If inside for loop test
        assertEquals("1\n2\n", executeProgram("var nums = [1, 2, 3]\n" +
                "for(i in nums){\n" +
                "    if(i <= 2){\n" +
                "        print(i)\n" +
                "    }\n" +
                "}"));

        // For inside if test
        assertEquals("1\n2\n3\n", executeProgram("var nums = [1, 2, 3]\n" +
                "var test = 2\n" +
                "if(test > 1){\n" +
                "    for(i in nums){\n" +
                "        print(i)\n" +
                "    }\n" +
                "}\n"));
    }
}
