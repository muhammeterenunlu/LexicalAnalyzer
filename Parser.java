import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Parser {

    private int indentLevel = 0;

    private Token currentToken;

    private List<Token> tokens;
    private int currentTokenIndex;

    private String getIndentation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

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
            Token token= tokens.get(currentTokenIndex);
            currentTokenIndex++;
            return  token;
        } else {
            throw new IndexOutOfBoundsException("No more tokens available");
        }
    }

    public void parseProgram() {
        System.out.println("<Program>");
        if (currentToken.type != TokenType.EOF) {
            try {
                parseTopLevelForm();
            } catch (SyntaxError | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void parseTopLevelForm() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<TopLevelForm>");
        indentLevel++;
        match(TokenType.LEFTPAR);
        parseSecondLevelForm();
        match(TokenType.RIGHTPAR);
        indentLevel--;
    }

    public void parseSecondLevelForm() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<SecondLevelForm>");
        indentLevel++;
        if (currentToken.type == TokenType.LEFTPAR) {
            match(TokenType.LEFTPAR);
            parseFunCall();
            match(TokenType.RIGHTPAR);
        } else {
            parseDefinition();
        }
        indentLevel--;
    }



    public void parseDefinition() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<Definition>");
        indentLevel++;
        match(TokenType.DEFINE);
        parseDefinitionRight();
        indentLevel--;
    }

    public void parseDefinitionRight() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<DefinitionRight>");
        indentLevel++;
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
        indentLevel--;
    }

    private void parseArgList() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<ArgList>");
        indentLevel++;
        if (currentToken.type == TokenType.IDENTIFIER) {
            match(TokenType.IDENTIFIER);
            parseArgList();
        }
        indentLevel--;
    }
    private void match(TokenType expectedType) throws SyntaxError {
        if (currentToken.type != expectedType) {
            throw new SyntaxError("Expected " + expectedType + " but found " + currentToken.type, currentToken);
        }
        System.out.println(getIndentation() + "<" + currentToken.type + "> " + currentToken.toString());
        currentToken = getNextToken();
    }
    public void parseExpression() throws SyntaxError, IOException {
        System.out.println(getIndentation() + "<Expression>");
        indentLevel++;
        if (currentToken.type == TokenType.IDENTIFIER ||
                currentToken.type == TokenType.NUMBER ||
                currentToken.type == TokenType.CHAR ||
                currentToken.type == TokenType.BOOLEAN ||
                currentToken.type == TokenType.STRING) {
            match(currentToken.type);
        } else if (currentToken.type == TokenType.LEFTPAR) {
            match(TokenType.LEFTPAR);
            if (currentToken.type == TokenType.RIGHTPAR) {
                System.out.println(getIndentation() + "<EmptyExpression>");
            } else {
                parseExpr();
            }
            match(TokenType.RIGHTPAR);
        } else {
            throw new SyntaxError("Expected IDENTIFIER, NUMBER, CHAR, BOOLEAN, STRING, or (, got: " + currentToken.type, currentToken);
        }
        indentLevel--;
    }


    public void parseLetExpression() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<LetExpression>");
        indentLevel++;
        match(TokenType.LET);
        parseLetExpr();
        indentLevel--;
    }

    public void parseCondExpression() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<CondExpression>");
        indentLevel++;
        match(TokenType.COND);
        parseCondBranches();
        indentLevel--;
    }

    public void parseIfExpression() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<IfExpression>");
        indentLevel++;
        match(TokenType.IF);
        parseExpression();
        parseExpression();
        parseEndExpression();
        indentLevel--;
    }

    public void parseBeginExpression() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<BeginExpression>");
        indentLevel++;
        match(TokenType.BEGIN);
        parseStatements();
        indentLevel--;
    }

    public void parseExpressions() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<Expressions>");
        indentLevel++;
        if (currentToken.type != TokenType.RIGHTPAR) {
            parseExpression();
            parseExpressions();
        }
        indentLevel--;
    }

    public void parseVarDefs() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<VarDefs>");
        indentLevel++;
        match(TokenType.LEFTPAR);
        match(TokenType.IDENTIFIER);
        parseExpression();
        match(TokenType.RIGHTPAR);
        if (currentToken.type == TokenType.LEFTPAR) {
            parseVarDefs();
        }
        indentLevel--;
    }

    public void parseLetExpr() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<LetExpr>");
        indentLevel++;
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
        indentLevel--;
    }

    public void parseEndExpression() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<EndExpression>");
        indentLevel++;
        if (currentToken.type == TokenType.IDENTIFIER ||
                currentToken.type == TokenType.NUMBER ||
                currentToken.type == TokenType.CHAR ||
                currentToken.type == TokenType.BOOLEAN ||
                currentToken.type == TokenType.STRING||
                currentToken.type == TokenType.LEFTPAR) {
            parseExpression();
        }
        indentLevel--;
    }





    public void parseCondBranches() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<CondBranches>");
        indentLevel++;
        match(TokenType.LEFTPAR);
        parseExpression();
        parseStatements();
        match(TokenType.RIGHTPAR);
        if (currentToken.type == TokenType.LEFTPAR) {
            parseCondBranches();
        }
        indentLevel--;
    }

    public void parseExpr() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<Expr>");
        indentLevel++;
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
        indentLevel--;
    }

    public void parseFunCall() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<FunCall>");
        indentLevel++;
        match(TokenType.IDENTIFIER);
        parseExpressions();
        indentLevel--;
    }

    private void parseStatements() throws IOException, SyntaxError {
        System.out.println(getIndentation() + "<Statements>");
        indentLevel++;
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
        indentLevel--;
    }







}