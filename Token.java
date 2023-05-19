public class Token {
    public final TokenType type;
    public final int lineNumber;
    public final int position;

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

