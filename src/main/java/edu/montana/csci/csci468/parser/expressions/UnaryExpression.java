package edu.montana.csci.csci468.parser.expressions;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenType;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class UnaryExpression extends Expression {

    private final Token operator;
    private final Expression rightHandSide;

    public UnaryExpression(Token operator, Expression rightHandSide) {
        this.rightHandSide = addChild(rightHandSide);
        this.operator = operator;
    }

    public Expression getRightHandSide() {
        return rightHandSide;
    }

    public boolean isMinus() {
        return operator.getType().equals(TokenType.MINUS);
    }

    public boolean isNot() {
        return !isMinus();
    }

    @Override
    public String toString() {
        return super.toString() + "[" + operator.getStringValue() + "]";
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        rightHandSide.validate(symbolTable);
        if (isNot() && !rightHandSide.getType().equals(CatscriptType.BOOLEAN)) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
        } else if(isMinus() && !rightHandSide.getType().equals(CatscriptType.INT)) {
            addError(ErrorType.INCOMPATIBLE_TYPES);
        }
    }

    @Override
    public CatscriptType getType() {
        if (isMinus()) {
            return CatscriptType.INT;
        } else {
            return CatscriptType.BOOLEAN;
        }
    }

    //==============================================================
    // Implementation
    //==============================================================

    @Override
    public Object evaluate(CatscriptRuntime runtime) {
        // check if we are a minus symbol or a not symbol and preform proper unary stuff
        if (this.isMinus()) {
            Integer rhsValue = (Integer) getRightHandSide().evaluate(runtime);
            return -1 * rhsValue;
        }
        else if(this.isNot()){
            Boolean rhsValue = (Boolean) getRightHandSide().evaluate(runtime);
            return !rhsValue;
        }
        else {
            return null; // TODO handle boolean NOT
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        if (isMinus()) {
            getRightHandSide().compile(code);
            code.addInstruction(Opcodes.INEG);
        } else {
            getRightHandSide().compile(code);
            Label trueL = new Label();
            Label falseL = new Label();
            // jump ifne
            if(isNot()){ code.addJumpInstruction(Opcodes.IFNE, falseL);}

            code.pushConstantOntoStack(true);
            code.addJumpInstruction(Opcodes.GOTO, trueL);
            code.addLabel(falseL);
            code.pushConstantOntoStack(false);
            code.addLabel(trueL);
        }

    }


}
