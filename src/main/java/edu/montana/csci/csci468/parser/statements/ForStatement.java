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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class ForStatement extends Statement {
    private Expression expression;
    private String variableName;
    private List<Statement> body;

    public void setExpression(Expression expression) {
        this.expression = addChild(expression);
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setBody(List<Statement> statements) {
        this.body = new LinkedList<>();
        for (Statement statement : statements) {
            this.body.add(addChild(statement));
        }
    }

    public Expression getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        symbolTable.pushScope();
        if (symbolTable.hasSymbol(variableName)) {
            addError(ErrorType.DUPLICATE_NAME);
        } else {
            expression.validate(symbolTable);
            CatscriptType type = expression.getType();
            if (type instanceof CatscriptType.ListType) {
                symbolTable.registerSymbol(variableName, getComponentType());
            } else {
                addError(ErrorType.INCOMPATIBLE_TYPES, getStart());
                symbolTable.registerSymbol(variableName, CatscriptType.OBJECT);
            }
        }
        for (Statement statement : body) {
            statement.validate(symbolTable);
        }
        symbolTable.popScope();
    }

    private CatscriptType getComponentType() {
        return ((CatscriptType.ListType) expression.getType()).getComponentType();
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        // create iterable object that is the evaluated expression
        // loop over that iterable object, in the loop we set the value
        //loop over the body and execute the statements
        Iterable evaluate = (Iterable) expression.evaluate(runtime);
        for(Object loopVarVal : evaluate){
            runtime.setValue(variableName, loopVarVal);
            for(Statement statement: body){
                statement.execute(runtime);
            }
        }
        //super.execute(runtime);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {

        Integer iteratorSlot = code.nextLocalStorageSlot();
        Label start = new Label();
        Label end = new Label();

        expression.compile(code);

        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, internalNameFor(List.class), "iterator", "()Ljava/util/Iterator;");
        code.addVarInstruction(Opcodes.ASTORE, iteratorSlot);
        code.addLabel(start);

        code.addVarInstruction(Opcodes.ALOAD, iteratorSlot);
        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, internalNameFor(Iterator.class), "hasNext", "()Z");
        code.addJumpInstruction(Opcodes.IFEQ, end);

        CatscriptType compType = getComponentType();
        code.addVarInstruction(Opcodes.ALOAD, iteratorSlot);
        code.addMethodInstruction(Opcodes.INVOKEINTERFACE, internalNameFor(Iterator.class), "next", "()Ljava/lang/Object;");
        code.addTypeInstruction(Opcodes.CHECKCAST, internalNameFor(compType.getJavaType()));
        unbox(code, compType);

        Integer localStorageSlot = code.createLocalStorageSlotFor(variableName);
        // store val on top of op stack into slot (account for int/bool...)
        if (compType.equals(CatscriptType.INT) || compType.equals(CatscriptType.BOOLEAN)) {
            code.addVarInstruction(Opcodes.ISTORE, localStorageSlot);
        } else {
            code.addVarInstruction(Opcodes.ASTORE, localStorageSlot);
        }
        //compile the body
        for (Statement st8mt : body) {
            st8mt.compile(code);
        }
        //jump back
        code.addJumpInstruction(Opcodes.GOTO, start);
        code.addLabel(end);

    }

}
