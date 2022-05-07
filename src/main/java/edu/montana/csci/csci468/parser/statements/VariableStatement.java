package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;
import org.objectweb.asm.Opcodes;

import static edu.montana.csci.csci468.bytecode.ByteCodeGenerator.internalNameFor;

public class VariableStatement extends Statement {
    private Expression expression;
    private String variableName;
    private CatscriptType explicitType;
    private CatscriptType type;

    public Expression getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setExpression(Expression parseExpression) {
        this.expression = addChild(parseExpression);
    }

    public void setExplicitType(CatscriptType type) {
        this.explicitType = type;
    }

    public CatscriptType getExplicitType() {
        return explicitType;
    }

    public boolean isGlobal() {
        return getParent() instanceof CatScriptProgram;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        // TODO if there is an explicit type, ensure it is correct
        // if not, infer the type from the right hand side expression
        if (symbolTable.hasSymbol(variableName)) {
            CatscriptType x = symbolTable.getSymbolType(variableName);
            addError(ErrorType.DUPLICATE_NAME);
        } else {
            if (getExplicitType() != null) {
                if (!getExplicitType().isAssignableFrom(expression.getType())) {
                    addError(ErrorType.INCOMPATIBLE_TYPES);
                } else {
                    type = getExplicitType();
                }
            } else {
                type = expression.getType();
            }
            symbolTable.registerSymbol(variableName, type);
        }
    }

    public CatscriptType getType() {
        return type;
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        //I set the variableName and expression.evaluate(runtime)
        runtime.setValue(variableName, expression.evaluate(runtime));
        //super.execute(runtime);
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {


        /* original code for some reason it wasnt working when i did it like this, so i changed it a bit to
         seems like putting "L" + internalNameFor(getType().getJavaType()) + ";" in the descriptor slot was a
         no no, but i dont really know whats going on, just did stuff till it worked, lol */

//        if(isGlobal()){
//            //store in field
//            if(getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)){
//                code.addField(variableName, "I");
//                code.addVarInstruction(Opcodes.ALOAD, 0);
//                expression.compile(code);
//                code.addFieldInstruction(Opcodes.PUTFIELD,  variableName, "I", code.getProgramInternalName());
//            }else {
//                code.addField(variableName, internalNameFor(getType().getJavaType()) + ";");
//                code.addVarInstruction(Opcodes.ALOAD, 0);
//                expression.compile(code);
//                code.addFieldInstruction(Opcodes.PUTFIELD,  variableName, "L" + internalNameFor(getType().getJavaType()) + ";", code.getProgramInternalName());
//            }
//        }else{
//            //store in slot
//            Integer locSlot = code.createLocalStorageSlotFor(variableName);
//            if(getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)){
//                expression.compile(code);
//                code.addVarInstruction(Opcodes.ISTORE, locSlot);
//            }else{
//                expression.compile(code);
//                code.addVarInstruction(Opcodes.ASTORE, locSlot);
//            }
//
//        }

        if (isGlobal()) {
            //store in field
            String des;
            if (getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)) {
                des = "I";
            } else {
                des = "L" + internalNameFor(getType().getJavaType()) + ";";
            }
            code.addField(variableName, des);
            code.addVarInstruction(Opcodes.ALOAD, 0);
            expression.compile(code);
            code.addFieldInstruction(Opcodes.PUTFIELD, variableName, des, code.getProgramInternalName());
        } else {
            //store in slot
            Integer locSlot = code.createLocalStorageSlotFor(variableName);
            if (getType().equals(CatscriptType.INT) || getType().equals(CatscriptType.BOOLEAN)) {
                expression.compile(code);
                code.addVarInstruction(Opcodes.ISTORE, locSlot);
            } else {
                expression.compile(code);
                code.addVarInstruction(Opcodes.ASTORE, locSlot);
            }
        }
    }
}
