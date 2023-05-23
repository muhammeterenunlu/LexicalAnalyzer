public class Token {
    public final TokenType type;
    public final int lineNumber;
    public final int position;
    public final String value; // Added value field

    public Token(TokenType type, int lineNumber, int position, String value) { // Modified constructor
        this.type = type;
        this.lineNumber = lineNumber;
        this.position = position;
        this.value = value; // Assigning the value
    }

    @Override
    public String toString() {
        return type + " " + lineNumber + ":" + position + " " + value; // Modified to include value
    }
}

