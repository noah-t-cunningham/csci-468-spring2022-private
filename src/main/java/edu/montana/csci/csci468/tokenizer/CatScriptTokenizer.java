package edu.montana.csci.csci468.tokenizer;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptTokenizer {

    TokenList tokenList;
    String src;
    int postion = 0;
    int line = 1;
    int lineOffset = 0;

    public CatScriptTokenizer(String source) {
        src = source;
        tokenList = new TokenList(this);
        tokenize();
    }

    private void tokenize() {
        consumeWhitespace();
        while (!tokenizationEnd()) {
            scanToken();
            consumeWhitespace();
        }
        tokenList.addToken(EOF, "<EOF>", postion, postion, line, lineOffset);
    }

    private void scanToken() {
        if(scanNumber()) {
            return;
        }
        if(scanString()) {
            return;
        }
        if(scanIdentifier()) {
            return;
        }
        scanSyntax();
    }

    private boolean scanString() {
        // TODO implement string scanning here!
        int start = postion;
        if (matchAndConsume('"')){
            // recreate the string that where tokenizing to return it, since we can only access one char at a time
            String str = "";
            while(peek() != '"' && !tokenizationEnd()){
                // handles slashes
                if(peek() == '\\'){
                    takeChar();
                    lineOffset++;
                    // handles strings than end at the EOF
                    if(!tokenizationEnd()){
                        char chr = takeChar();
                        lineOffset++;
                        str = str + chr;
                    }
                }
                else {
                    char chr = takeChar();
                    lineOffset++;
                    str = str + chr;
                }
            }
            // handles the end of the string
            if(matchAndConsume('"')) {
                tokenList.addToken(STRING, str, start, postion, line, lineOffset);
                return true;
            }
            // handles EOFs
            else{
                tokenList.addToken(ERROR, "reached EOF before End of String: " + '"' + str, start, postion, line, lineOffset);
                return true;
            }

        }
        return false;
    }

    private boolean scanIdentifier() {
        if( isAlpha(peek())) {
            int start = postion;
            while (isAlphaNumeric(peek())) {
                takeChar();
            }
            String value = src.substring(start, postion);
            if (KEYWORDS.containsKey(value)) {
                tokenList.addToken(KEYWORDS.get(value), value, start, postion, line, lineOffset);
            } else {
                tokenList.addToken(IDENTIFIER, value, start, postion, line, lineOffset);
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean scanNumber() {
        if(isDigit(peek())) {
            int start = postion;
            while (isDigit(peek())) {
                takeChar();
            }
            tokenList.addToken(INTEGER, src.substring(start, postion), start, postion, line, lineOffset);
            return true;
        } else {
            return false;
        }
    }

    private void scanSyntax() {
        // TODO - implement rest of syntax scanning
        //      - implement comments
        int start = postion;
        if(matchAndConsume('+')) {
            lineOffset++;
            tokenList.addToken(PLUS, "+", start, postion, line, lineOffset);
        }
        else if(matchAndConsume('-')) {
            lineOffset++;
            tokenList.addToken(MINUS, "-", start, postion, line, lineOffset);
        }
        else if(matchAndConsume('/')) {
            if (matchAndConsume('/')) {
                while (peek() != '\n' && !tokenizationEnd()) {
                    lineOffset++;
                    takeChar();
                }
            }
            else {
                lineOffset++;
                tokenList.addToken(SLASH, "/", start, postion, line, lineOffset);
            }
        }
        else if(matchAndConsume('=')) {
            if (matchAndConsume('=')) {
                lineOffset++;
                tokenList.addToken(EQUAL_EQUAL, "==", start, postion, line, lineOffset);
            }
            else {
                lineOffset++;
                tokenList.addToken(EQUAL, "=", start, postion, line, lineOffset);
            }
        }
        // implementing parentheses
        else if (matchAndConsume('(')){
            lineOffset++;
            tokenList.addToken(LEFT_PAREN, "(", start, postion, line, lineOffset);
        }
        else if (matchAndConsume(')')){
            lineOffset++;
            tokenList.addToken(RIGHT_PAREN, ")", start, postion, line, lineOffset);
        }
        //implementing curly braces
        else if (matchAndConsume('{')){
            lineOffset++;
            tokenList.addToken(LEFT_BRACE, "{", start, postion, line, lineOffset);
        }
        else if (matchAndConsume('}')){
            lineOffset++;
            tokenList.addToken(RIGHT_BRACE, "}", start, postion, line, lineOffset);
        }
        // implementing brackets
        else if (matchAndConsume('[')){
            lineOffset++;
            tokenList.addToken(LEFT_BRACKET, "[", start, postion, line, lineOffset);
        }
        else if (matchAndConsume(']')){
            lineOffset++;
            tokenList.addToken(RIGHT_BRACKET, "]", start, postion, line, lineOffset);
        }
        //implementing colon
        else if (matchAndConsume(':')){
            lineOffset++;
            tokenList.addToken(COLON, ":", start, postion, line, lineOffset);
        }
        //implementing comma
        else if (matchAndConsume(',')){
            lineOffset++;
            tokenList.addToken(COMMA, ",", start, postion, line, lineOffset);
        }
        //implementing dot
        else if (matchAndConsume('.')){
            lineOffset++;
            tokenList.addToken(DOT, ".", start, postion, line, lineOffset);
        }
        //implementing multiply
        else if (matchAndConsume('*')){
            lineOffset++;
            tokenList.addToken(STAR, "*", start, postion, line, lineOffset);
        }
        //implementing not equal
        else if (matchAndConsume('!')){
            if (matchAndConsume('=')) {
                lineOffset++;
                tokenList.addToken(BANG_EQUAL, "!=", start, postion, line, lineOffset);
            }
            // TODO maybe just return an error
            else {
                lineOffset++;
                tokenList.addToken(FALSE, "!", start, postion, line, lineOffset);
            }
        }
        //implementing greater and greater equal
        else if (matchAndConsume('>')){
            if (matchAndConsume('=')) {
                lineOffset++;
                tokenList.addToken(GREATER_EQUAL, ">=", start, postion, line, lineOffset);
            }
            else {
                lineOffset++;
                tokenList.addToken(GREATER, ">", start, postion, line, lineOffset);
            }
        }
        //implementing less and less equal
        else if (matchAndConsume('<')){
            if (matchAndConsume('=')) {
                lineOffset++;
                tokenList.addToken(LESS_EQUAL, "<=", start, postion, line, lineOffset);
            }
            else {
                lineOffset++;
                tokenList.addToken(LESS, "<", start, postion, line, lineOffset);
            }
        }

        else {
            lineOffset++;
            tokenList.addToken(ERROR, "<Unexpected Token: [" + takeChar() + "]>", start, postion, line, lineOffset);
        }
    }

    private void consumeWhitespace() {
        // TODO update line and lineOffsets
        while (!tokenizationEnd()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t') {
                postion++;
                lineOffset++;
                continue;
            } else if (c == '\n') {
                lineOffset = 0;
                postion++;
                line++;
                continue;
            }
            break;
        }
    }

    //===============================================================
    // Utility functions
    //===============================================================

    private char peek() {
        if (tokenizationEnd()) return '\0';
        return src.charAt(postion);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char takeChar() {
        char c = src.charAt(postion);
        postion++;
        lineOffset++;
        return c;
    }

    private boolean tokenizationEnd() {
        return postion >= src.length();
    }

    public boolean matchAndConsume(char c) {
        if (peek() == c) {
            takeChar();
            return true;
        }
        return false;
    }

    public TokenList getTokens() {
        return tokenList;
    }

    @Override
    public String toString() {
        if (tokenizationEnd()) {
            return src + "-->[]<--";
        } else {
            return src.substring(0, postion) + "-->[" + peek() + "]<--" +
                    ((postion == src.length() - 1) ? "" :
                            src.substring(postion + 1, src.length() - 1));
        }
    }
}