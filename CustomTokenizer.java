public class CustomTokenizer {
    private final String input; // The input string to tokenize
    private int index;          // The current index in the input string
    private int lineNumber;     // The current line number in the input
    private int position;       // The current position (column) in the input

    // Constructor that initializes the tokenizer with the input string
    public CustomTokenizer(String input) {
        this.input = input;
        this.index = 0;
        this.lineNumber = 1;
        this.position = 1;
    }

    // Method to retrieve the next token from the input string
    public Token nextToken() throws LexicalException {
        // Loop through the input string
        while (index < input.length()) {
            char current = input.charAt(index);
            index++;
            position++;

            // If the current character is whitespace
            if (Character.isWhitespace(current)) {
                // If the current character is a newline, increment the line number and reset the position
                if (current == '\n') {
                    lineNumber++;
                    position = 1;
                }
                // Skip to the next iteration
                continue;
            }

            // If the current character is a comment start symbol
            if (current == '~') {
                // Skip to the end of the line (or the end of the input)
                while (index < input.length() && input.charAt(index) != '\n') {
                    index++;
                }
                // Skip to the next iteration
                continue;
            }

            // Iterate over each TokenType and try to match the input
            for (TokenType tokenType : TokenType.values()) {
                int newIndex = tokenType.match(input, index - 1);
                if (newIndex != -1) {
                    // If a match is found, create a token and update the index and position
                    Token token = new Token(tokenType, lineNumber, position,"");
                    index = newIndex;
                    position += tokenType.length();
                    // Return the matched token
                    return token;
                }
            }

            // If no TokenType matches, throw a LexicalException with an error message
            throw new LexicalException("Unrecognized token '" + current + "' at line " + lineNumber + ", position " + position);
        }

        // If the end of the input is reached, return null
        return null;
    }
}
