package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class IfStatement extends Statement {
    private Expression expression;
    private List<Statement> trueStatements = Collections.emptyList();
    private List<Statement> elseStatements = Collections.emptyList();

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public List<Statement> getTrueStatements() {
        return trueStatements;
    }

    public void setTrueStatements(List<Statement> statements) {
        this.trueStatements = new LinkedList<>();
        for (Statement statement : statements) {
            this.trueStatements.add(addChild(statement));
        }
    }

    public List<Statement> getElseStatements() {
        return elseStatements;
    }

    public void setElseStatements(List<Statement> statements) {
        this.elseStatements = new LinkedList<>();
        for (Statement statement : statements) {
            this.elseStatements.add(addChild(statement));
        }
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        if (!expression.getType().equals(CatscriptType.BOOLEAN)) {
            expression.addError(ErrorType.INCOMPATIBLE_TYPES);
        }
        symbolTable.pushScope();
        for (Statement trueStatement : trueStatements) {
            trueStatement.validate(symbolTable);
        }
        symbolTable.popScope();
        symbolTable.pushScope();
        for (Statement elseStatement : elseStatements) {
            elseStatement.validate(symbolTable);
        }
        symbolTable.popScope();
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        // kinda a fun one
        // create object, if its true execute the true statements, if its false execute the else statements
        Object evaluate = expression.evaluate(runtime);
        if (Boolean.TRUE.equals(evaluate)) {
            for (Statement statement : trueStatements) {
                statement.execute(runtime);
            }
        } else if (Boolean.FALSE.equals(evaluate)) {
            for (Statement statement : elseStatements) {
                statement.execute(runtime);
            }
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        expression.compile(code);
        Label trueL = new Label();
        Label falseL = new Label();

        if (elseStatements.size() > 0) {
            code.addJumpInstruction(Opcodes.IFEQ, falseL);
        } else {
            code.addJumpInstruction(Opcodes.IFEQ, trueL);
        }

        for (Statement st8mt : trueStatements) {
            st8mt.compile(code);
        }

        code.addJumpInstruction(Opcodes.GOTO, trueL);
        if (elseStatements.size() > 0) {
            code.addLabel(falseL);
            for (Statement st8 : elseStatements) {
                st8.compile(code);
            }
        }
        code.addLabel(trueL);
    }
}
