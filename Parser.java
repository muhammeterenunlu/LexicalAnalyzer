import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parser {

    private Token currentToken;

    private List<Token> tokens;
    private int currentTokenIndex;

    public Parser(String fileName) throws IOException, LexicalException {
        this.tokens = PPLLScanner.tokenizeForParser(fileName);
        this.currentTokenIndex = 0;
        if (tokens.size() > 0) {
            this.currentToken = getNextToken();
        } else {
            throw new RuntimeException("No tokens to parse");
        }
    }
    private Token getNextToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex++);
        } else {
            throw new IndexOutOfBoundsException("No more tokens available");
        }
    }

    public void parseProgram() {
        while (currentToken.type != TokenType.EOF) {
            try {
                parseTopLevelForm();
            } catch (SyntaxError | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void parseTopLevelForm() throws IOException, SyntaxError {
        match(TokenType.LEFTPAR);
        parseSecondLevelForm();
        match(TokenType.RIGHTPAR);
    }

    public void parseSecondLevelForm() throws IOException, SyntaxError {
        if (currentToken.type == TokenType.LEFTPAR) {
            match(TokenType.LEFTPAR);
            parseFunCall();
            match(TokenType.RIGHTPAR);
        } else {
            parseDefinition();
        }
    }


    public void parseDefinition() throws IOException, SyntaxError {
        match(TokenType.DEFINE);
        parseDefinitionRight();
    }


    private void match(TokenType expectedType) throws SyntaxError {
        Token currentToken = getNextToken();
        if (currentToken.type != expectedType) {
            throw new SyntaxError("Expected " + expectedType + " but found " + currentToken.type, currentToken);
        }
    }
    public void parseExpression() throws SyntaxError, IOException {
        if (currentToken.type == TokenType.IDENTIFIER ||
                currentToken.type == TokenType.NUMBER ||
                currentToken.type == TokenType.CHAR ||
                currentToken.type == TokenType.BOOLEAN ||
                currentToken.type == TokenType.STRING) {
            currentToken = getNextToken();
        } else if (currentToken.type == TokenType.LEFTPAR) {
            match(TokenType.LEFTPAR);
            parseExpr();
            match(TokenType.RIGHTPAR);
        } else {
            throw new SyntaxError("Expected IDENTIFIER, NUMBER, CHAR, BOOLEAN, STRING, or (, got: " + currentToken.type, currentToken);
        }
    }

    public void parseLetExpression() throws IOException, SyntaxError {
        match(TokenType.LET);
        parseLetExpr();
    }


    private void parseDefinitionRight() throws IOException, SyntaxError {
        if (currentToken.type == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
            parseExpression();
        } else {
            match(TokenType.LEFTPAR);
            match(TokenType.IDENTIFIER);
            parseArgList();
            match(TokenType.RIGHTPAR);
            parseStatements();
        }
    }

    private void parseArgList() throws IOException, SyntaxError {
        if (currentToken.type == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
            parseArgList();
        }
    }

    private void parseStatements() throws IOException, SyntaxError {
        if (currentToken.type == TokenType.IDENTIFIER ||
                currentToken.type == TokenType.NUMBER ||
                currentToken.type == TokenType.CHAR ||
                currentToken.type == TokenType.BOOLEAN ||
                currentToken.type == TokenType.STRING ||
                currentToken.type == TokenType.LEFTPAR) {
            parseExpression();
            parseStatements();
        } else if (currentToken.type == TokenType.DEFINE) {
            parseDefinition();
            parseStatements();
        }
    }
    public void parseFunCall() throws IOException, SyntaxError {
        match(TokenType.IDENTIFIER);
        parseExpressions();
    }

    public void parseExpr() throws IOException, SyntaxError {
        if (currentToken.type == TokenType.LET) {
            parseLetExpression();
        } else if (currentToken.type == TokenType.COND) {
            parseCondExpression();
        } else if (currentToken.type == TokenType.IF) {
            parseIfExpression();
        } else if (currentToken.type == TokenType.BEGIN) {
            parseBeginExpression();
        } else {
            parseFunCall();
        }
    }



    public void parseCondExpression() throws IOException, SyntaxError {
        match(TokenType.COND);
        parseCondBranches();
    }

    public void parseCondBranches() throws IOException, SyntaxError {
        match(TokenType.LEFTPAR);
        parseExpression();
        parseStatements();
        match(TokenType.RIGHTPAR);
        if (currentToken.type == TokenType.LEFTPAR) {
            parseCondBranches();
        }
    }

    public void parseIfExpression() throws IOException, SyntaxError {
        match(TokenType.IF);
        parseExpression();
        parseExpression();
        parseEndExpression();
    }


    public void parseBeginExpression() throws IOException, SyntaxError {
        match(TokenType.BEGIN);
        parseStatements();
    }

    public void parseExpressions() throws IOException, SyntaxError {
        if (currentToken.type != TokenType.RIGHTPAR) {
            parseExpression();
            parseExpressions();
        }
    }

    public void parseVarDefs() throws IOException, SyntaxError {
        match(TokenType.LEFTPAR);
        match(TokenType.IDENTIFIER);
        parseExpression();
        match(TokenType.RIGHTPAR);
        if (currentToken.type == TokenType.LEFTPAR) {
            parseVarDefs();
        }
    }

    public void parseLetExpr() throws IOException, SyntaxError {
        if (currentToken.type == TokenType.LEFTPAR) {
            match(TokenType.LEFTPAR);
            parseVarDefs();
            match(TokenType.RIGHTPAR);
            parseStatements();
        } else if (currentToken.type == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
            match(TokenType.LEFTPAR);
            parseVarDefs();
            match(TokenType.RIGHTPAR);
            parseStatements();
        }
    }



    public void parseEndExpression() throws IOException, SyntaxError {
        if (currentToken.type != TokenType.EOF) {
            parseExpression();
        }
    }



}