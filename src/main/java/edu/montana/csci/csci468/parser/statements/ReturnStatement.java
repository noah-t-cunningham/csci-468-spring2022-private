package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.eval.ReturnException;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Opcodes;

public class ReturnStatement extends Statement {
    private Expression expression;
    private FunctionDefinitionStatement function;

    public void setExpression(Expression parseExpression) {
        this.expression = addChild(parseExpression);
    }

    public void setFunctionDefinition(FunctionDefinitionStatement func) {
        this.function = func;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        if (expression != null) {
            expression.validate(symbolTable);
            if (!function.getType().isAssignableFrom(expression.getType())) {
                expression.addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        } else {
            if (!function.getType().equals(CatscriptType.VOID)) {
                addError(ErrorType.INCOMPATIBLE_TYPES);
            }
        }
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        // throw ReturnException of the expression
        Object returnVal = expression.evaluate(runtime);
        // thought id put this here since the logic makes sense but it doesnt seem to effect the passing of any tests
        runtime.setValue(function.getName(), returnVal);
        throw new ReturnException(returnVal);

    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        CatscriptType retType = function.getType();
        CatscriptType expType = expression.getType();
        if(expType.equals(CatscriptType.VOID)){
            //expression.compile(code);
            code.addInstruction(Opcodes.RETURN);
        } else{

            // do we need to box
            // issue an i or a return
            expression.compile(code);
            if (retType.equals(CatscriptType.INT) || retType.equals(CatscriptType.BOOLEAN)) {
                code.addInstruction(Opcodes.IRETURN);
            } else {
                if (expType.equals(CatscriptType.INT) || expType.equals(CatscriptType.BOOLEAN)){
                    box(code, expType);
                }
                code.addInstruction(Opcodes.ARETURN);
            }
        }

    }

}