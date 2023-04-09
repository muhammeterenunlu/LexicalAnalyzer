public class Token {
    private final TokenType type;
    private final int lineNumber;
    private final int position;

    public Token(TokenType type, int lineNumber, int position) {
        this.type = type;
        this.lineNumber = lineNumber;
        this.position = position;
    }

    @Override
    public String toString() {
        return type + " " + lineNumber + ":" + position;
    }
}

