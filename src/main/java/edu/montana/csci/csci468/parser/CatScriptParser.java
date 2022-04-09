package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import edu.montana.csci.csci468.tokenizer.CatScriptTokenizer;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenList;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptParser {

    private TokenList tokens;
    private FunctionDefinitionStatement currentFunctionDefinition;

    public CatScriptProgram parse(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();

        // first parse an expression
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = null;
        try {
            expression = parseExpression();
        } catch(RuntimeException re) {
            // ignore :)
        }
        if (expression == null || tokens.hasMoreTokens()) {
            tokens.reset();
            while (tokens.hasMoreTokens()) {
                program.addStatement(parseProgramStatement());
            }
        } else {
            program.setExpression(expression);
        }

        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    public CatScriptProgram parseAsExpression(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        program.setExpression(expression);
        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    //============================================================
    //  Statements
    //============================================================


    private Statement parseProgramStatement() {
        Statement stmt = parseFunctionDefinitionStatement();
        if (stmt != null) {
            return stmt;
        }
        return parseStatement();
    }

    private FunctionDefinitionStatement parseFunctionDefinitionStatement(){
        if(tokens.match(FUNCTION)){
            FunctionDefinitionStatement funk = new FunctionDefinitionStatement();
            funk.setStart(tokens.consumeToken());

            Token functionName = require(IDENTIFIER, funk);
            funk.setName(functionName.getStringValue());
            require(LEFT_PAREN, funk);

            //handles parameter lists
            if(!tokens.match(RIGHT_PAREN) && tokens.hasMoreTokens()){
                do {

                    Token paramName = require(IDENTIFIER, funk);
                    TypeLiteral typeLiteral = null;
                    if (tokens.matchAndConsume(COLON)) {
                        typeLiteral = parseTypeLiteral();
                    }
                    funk.addParameter(paramName.getStringValue(), typeLiteral);
                } while(tokens.matchAndConsume(COMMA));
            }
            require(RIGHT_PAREN, funk);

            //handles optional type expression
            TypeLiteral typeLiteral = null;
            if (tokens.matchAndConsume(COLON)) {
                typeLiteral = parseTypeLiteral();
            }
            funk.setType(typeLiteral);

            currentFunctionDefinition = funk;

            require(LEFT_BRACE, funk);
            LinkedList<Statement> stmts = new LinkedList<>();
            funk.setBody(stmts);

            while(!tokens.match(RIGHT_BRACE) && tokens.hasMoreTokens()){
                stmts.add(parseStatement());
                funk.setBody(stmts);
            }
            require(RIGHT_BRACE, funk);

            return  funk;

        }else{
            return null;
        }
    }

    private TypeLiteral parseTypeLiteral(){
        if(tokens.match("int")){
            TypeLiteral typeLiteral = new TypeLiteral();
            typeLiteral.setType(CatscriptType.INT);
            typeLiteral.setToken(tokens.consumeToken());
            return typeLiteral;
        }
        if(tokens.match("string")){
            TypeLiteral typeLiteral = new TypeLiteral();
            typeLiteral.setType(CatscriptType.STRING);
            typeLiteral.setToken(tokens.consumeToken());
            return typeLiteral;
        }
        if(tokens.match("bool")){
            TypeLiteral typeLiteral = new TypeLiteral();
            typeLiteral.setType(CatscriptType.BOOLEAN);
            typeLiteral.setToken(tokens.consumeToken());
            return typeLiteral;
        }
        if(tokens.match("object")){
            TypeLiteral typeLiteral = new TypeLiteral();
            typeLiteral.setType(CatscriptType.OBJECT);
            typeLiteral.setToken(tokens.consumeToken());
            return typeLiteral;
        }
        if(tokens.match("list")){
            TypeLiteral typeLiteral = new TypeLiteral();
            typeLiteral.setType(CatscriptType.getListType(CatscriptType.OBJECT));
            typeLiteral.setToken(tokens.consumeToken());
            if(tokens.matchAndConsume(LESS)){
                TypeLiteral componentType = parseTypeLiteral();
                typeLiteral.setType(CatscriptType.getListType(componentType.getType()));
                require(GREATER, typeLiteral);
            }

            return typeLiteral;
        }
        TypeLiteral typeLiteral = new TypeLiteral();
        typeLiteral.setType(CatscriptType.getListType(CatscriptType.OBJECT));
        typeLiteral.setToken(tokens.consumeToken());
        typeLiteral.addError(ErrorType.BAD_TYPE_NAME);
        return typeLiteral;
    }



    private Statement parseStatement(){
//        try {

            Statement printStmt = parsePrintStatement();
            if (printStmt != null) {
                return printStmt;
            }
            Statement forStmt = parseForStatement();
            if (forStmt != null) {
                return forStmt;
            }
            Statement ifStmt = parseIfStatement();
            if (ifStmt != null) {
                return ifStmt;
            }
            Statement varStmt = parseVariableStatement();
            if (varStmt != null) {
                return varStmt;
            }
            Statement assStmt = parseAssighnmentStatement();
            if (assStmt != null) {
                return assStmt;
            }

            Statement funkStmt = parseFunctionCallStatement();
            if (funkStmt != null) {
                return funkStmt;
            }


            Statement retStmt = parseReturnStatement();
            if (retStmt != null) {
                return retStmt;
            }
            // probably should delete this but i have a hard time letting go of code i thought might be useful

//            SyntaxErrorStatement syntaxErrorStatement = new SyntaxErrorStatement(tokens.consumeToken());
//            return syntaxErrorStatement;
//        } catch (UnknownExpressionParseException e){
//            SyntaxErrorStatement syntaxErrorStatement = new SyntaxErrorStatement(tokens.consumeToken());
//            //e.printStackTrace();
//            while(tokens.hasMoreTokens()){
//
//            }
//            return syntaxErrorStatement;
//        }
//        Statement eof = parseEOF();
//        if (eof != null){
//            return eof;
//        }
        return null;
    }

    private Statement parsePrintStatement() {
        if (tokens.match(PRINT)) {

            PrintStatement printStatement = new PrintStatement();
            printStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, printStatement);
            printStatement.setExpression(parseExpression());
            printStatement.setEnd(require(RIGHT_PAREN, printStatement));

            return printStatement;
        } else {
            return null;
        }
    }

    private Statement parseForStatement() {
        if (tokens.match(FOR)) {

            ForStatement forStatement = new ForStatement();
            forStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, forStatement);
            Token ident = require(IDENTIFIER, forStatement);
            forStatement.setVariableName(ident.getStringValue());
            //require(IDENTIFIER, forStatement);
            require(IN, forStatement);
            forStatement.setExpression(parseExpression());
            require(RIGHT_PAREN, forStatement);
            require(LEFT_BRACE, forStatement);
            LinkedList<Statement> stmts = new LinkedList<>();
            while (!tokens.match(RIGHT_BRACE) && !tokens.match(EOF)) {
                stmts.add(parseStatement());
            }
            forStatement.setBody(stmts);
            forStatement.setEnd(require(RIGHT_BRACE, forStatement));

            return forStatement;
        } else {
            return null;
        }
    }

    private Statement parseIfStatement() {
        if (tokens.match(IF)) {

            IfStatement ifStatement = new IfStatement();
            ifStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, ifStatement);
            ifStatement.setExpression(parseExpression());
            require(RIGHT_PAREN, ifStatement);
            require(LEFT_BRACE, ifStatement);
            LinkedList<Statement> stmts = new LinkedList<>();
            while (!tokens.match(RIGHT_BRACE) && !tokens.match(EOF)) {
                stmts.add(parseStatement());
            }
            ifStatement.setTrueStatements(stmts);
            // since i use .nextToken() have to make sure im not at EOF or it goes out of range
            if(!tokens.match(EOF)) {
                Token next = tokens.nextToken();
                if (next.getType() == ELSE) {
                    tokens.matchAndConsume(RIGHT_BRACE);
                    tokens.matchAndConsume(ELSE);
                    if (tokens.matchAndConsume(IF))
                        parseIfStatement();
                    else {
                        require(LEFT_BRACE, ifStatement);
                        if (!tokens.match(EOF)) {
                            LinkedList<Statement> newStmts = new LinkedList<>();
                            while (!tokens.match(RIGHT_BRACE) && !tokens.match(EOF)) {
                                // make new stmts becasuean if else stament could have soemthoing like
                                // if(something): x = 10
                                // else: x = 5
                                newStmts.add(parseStatement());
                            }
                            ifStatement.setElseStatements(newStmts);
                        }
                        ifStatement.setEnd(require(RIGHT_BRACE, ifStatement));
                    }
                }
                else {
                    ifStatement.setEnd(require(RIGHT_BRACE, ifStatement));
                }
            }
            else {
                ifStatement.setEnd(require(RIGHT_BRACE, ifStatement));
            }

            return ifStatement;
        } else {
            return null;
        }
    }

    private Statement parseVariableStatement() {
        if (tokens.match(VAR)) {

            VariableStatement varStatement = new VariableStatement();
            varStatement.setStart(tokens.consumeToken());

            Token ident = require(IDENTIFIER, varStatement);
            varStatement.setVariableName(ident.getStringValue());
            TypeLiteral typeLiteral = null;
            if (tokens.matchAndConsume(COLON)) {
                typeLiteral = parseTypeLiteral();
                varStatement.setExplicitType(typeLiteral.getType());
                require(EQUAL, varStatement);
                Expression exp = parseExpression();
                varStatement.setExpression(exp);
                varStatement.setEnd(exp.getEnd());
            }
            else {
                require(EQUAL, varStatement);
                Expression exp = parseExpression();
                varStatement.setExpression(exp);
                varStatement.setExplicitType(exp.getType());
                varStatement.setEnd(exp.getEnd());
            }

            return varStatement;
        } else {
            return null;
        }
    }



    private Statement parseAssighnmentStatement() {
        if (tokens.match(IDENTIFIER)) {
            // i created the .nextToken() method becasue i wanted to use the code i
            // already wrote in parsePrimaryExpression
            //ass is just short for Assignment okay, im not being immature...
            Token next = tokens.nextToken();
            if(next.getType() == LEFT_PAREN){
                return null;
            }
            AssignmentStatement assStatement = new AssignmentStatement();
            assStatement.setStart(tokens.getCurrentToken());

            Token ident = require(IDENTIFIER, assStatement);

            assStatement.setVariableName(ident.getStringValue());
            require(EQUAL, assStatement);

            Expression exp = parseExpression();
            assStatement.setExpression(exp);
            assStatement.setEnd(exp.getEnd());




            return assStatement;
        } else {
            return null;
        }
    }

    private Statement parseFunctionCallStatement() {
        if (tokens.match(IDENTIFIER)) {

            Expression exp = parsePrimaryExpression();
            FunctionCallStatement funkStatement = new FunctionCallStatement((FunctionCallExpression) exp);

            return funkStatement;
        } else {
            return null;
        }
    }

    private Statement parseReturnStatement(){
        if (tokens.match(RETURN)) {

            ReturnStatement retStatement = new ReturnStatement();
            retStatement.setStart(tokens.consumeToken());
            retStatement.setFunctionDefinition(currentFunctionDefinition);

            if (!tokens.match(EOF) && !tokens.match(RIGHT_BRACE)) {
                retStatement.setExpression(parseExpression());
            }

            return retStatement;
        } else {
            return null;
        }
    }

    //============================================================
    //  Expressions
    //============================================================

    private Expression parseExpression() {
        return parseEqualityExpression();
    }

    private Expression parseEqualityExpression(){
        Expression expression = parseComparisonExpression();
        while (tokens.match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseComparisonExpression();
            EqualityExpression equalityExpression = new EqualityExpression(operator, expression, rightHandSide);
            equalityExpression.setStart(expression.getStart());
            equalityExpression.setEnd(rightHandSide.getEnd());
            expression = equalityExpression;
        }
        return expression;
    }

    private Expression parseComparisonExpression(){
        Expression expression = parseAdditiveExpression();
        while (tokens.match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseAdditiveExpression();
            ComparisonExpression comparisonExpression = new ComparisonExpression(operator, expression, rightHandSide);
            comparisonExpression.setStart(expression.getStart());
            comparisonExpression.setEnd(rightHandSide.getEnd());
            expression = comparisonExpression;
        }
        return expression;
    }

    private Expression parseAdditiveExpression() {
        Expression expression = parseFactorExpression();
        while (tokens.match(PLUS, MINUS)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseFactorExpression();
            AdditiveExpression additiveExpression = new AdditiveExpression(operator, expression, rightHandSide);
            additiveExpression.setStart(expression.getStart());
            additiveExpression.setEnd(rightHandSide.getEnd());
            expression = additiveExpression;
        }
        return expression;
    }

    private Expression parseFactorExpression() {
        Expression expression = parseUnaryExpression();
        while (tokens.match(SLASH, STAR)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseUnaryExpression();
            FactorExpression factorExpression = new FactorExpression(operator, expression, rightHandSide);
            factorExpression.setStart(expression.getStart());
            factorExpression.setEnd(rightHandSide.getEnd());
            expression = factorExpression;
        }
        return expression;
    }

    private Expression parseUnaryExpression() {
        if (tokens.match(MINUS, NOT)) {
            Token token = tokens.consumeToken();
            Expression rhs = parseUnaryExpression();
            UnaryExpression unaryExpression = new UnaryExpression(token, rhs);
            unaryExpression.setStart(token);
            unaryExpression.setEnd(rhs.getEnd());
            return unaryExpression;
        } else {
            return parsePrimaryExpression();
        }
    }

    private Expression parsePrimaryExpression() {
        if (tokens.match(IDENTIFIER)) {
            Token token = tokens.consumeToken();
            // handles fucntion calls
            if(tokens.match(LEFT_PAREN)){
                tokens.consumeToken();
                List<Expression> list = new ArrayList<>();
                while(!tokens.match(RIGHT_PAREN, LEFT_PAREN, EOF)) {
                    if (!tokens.match(COMMA)){
                        Expression expression = parseExpression();
                        list.add(expression);
                    }
                    else{
                        tokens.consumeToken();
                    }
                }
                FunctionCallExpression functionCallExpression = new FunctionCallExpression(token.getStringValue(), list);
                if(tokens.match(EOF)) {
                    functionCallExpression.addError(ErrorType.UNTERMINATED_ARG_LIST);
                    return functionCallExpression;
                }
                tokens.consumeToken();
                return functionCallExpression;
            }
            // handles just identifiers
            else {
                IdentifierExpression identifierExpression = new IdentifierExpression(token.getStringValue());
                identifierExpression.setToken(token);
                return identifierExpression;
            }
        }
        // handles strings
        else if (tokens.match(STRING)) {
            Token token = tokens.consumeToken();
            StringLiteralExpression stringExpression = new StringLiteralExpression(token.getStringValue());
            stringExpression.setToken(token);
            return stringExpression;
        }
        // handles integers
        else if (tokens.match(INTEGER)) {
            Token token = tokens.consumeToken();
            IntegerLiteralExpression integerExpression = new IntegerLiteralExpression(token.getStringValue());
            integerExpression.setToken(token);
            return integerExpression;
        }
        // handles booleans
        else if (tokens.match(TRUE, FALSE)) {
            Token integerToken = tokens.consumeToken();
            BooleanLiteralExpression booleanExpression = new BooleanLiteralExpression(Boolean.parseBoolean(integerToken.getStringValue()));
            booleanExpression.setToken(integerToken);
            return booleanExpression;
        }
        // handles null
        else if(tokens.match(NULL)) {
            tokens.consumeToken();
            return new NullLiteralExpression();
        }

        // handles lists
        else if(tokens.match(LEFT_BRACKET)){
            tokens.consumeToken();
            List<Expression> list = new ArrayList<>();
            while(!tokens.match(RIGHT_BRACKET, LEFT_BRACKET, EOF)) {
                if (!tokens.match(COMMA)){
                    Expression expression = parseExpression();
                    list.add(expression);
                }
                else{
                    tokens.consumeToken();
                }
            }
            ListLiteralExpression listLiteralExpression = new ListLiteralExpression(list);
            if(tokens.match(EOF)) {
                listLiteralExpression.addError(ErrorType.UNTERMINATED_LIST);
                return listLiteralExpression;
            }
            tokens.consumeToken();
            return listLiteralExpression;
        }
        // give priority to parenthesized expressions
        else if(tokens.match(LEFT_PAREN)){
            tokens.consumeToken();
            Expression expression = parseExpression();
            ParenthesizedExpression parenthesizedExpression = new ParenthesizedExpression(expression);

            tokens.consumeToken();
            return parenthesizedExpression;

        }
        else {
            SyntaxErrorExpression syntaxErrorExpression = new SyntaxErrorExpression(tokens.consumeToken());
            return syntaxErrorExpression;
//            throw new UnknownExpressionParseException();
        }
    }

//    idk you had this in one of your videos
//    private class UnknownExpressionParseException extends RuntimeException {
//    }


    //============================================================
    //  Parse Helpers
    //============================================================
    private Token require(TokenType type, ParseElement elt) {
        return require(type, elt, ErrorType.UNEXPECTED_TOKEN);
    }

    private Token require(TokenType type, ParseElement elt, ErrorType msg) {
        if(tokens.match(type)){
            return tokens.consumeToken();
        } else {
            elt.addError(msg, tokens.getCurrentToken());
            return tokens.getCurrentToken();
        }
    }

}
