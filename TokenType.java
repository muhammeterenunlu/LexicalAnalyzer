public enum TokenType {
    LEFTPAR("("), 
    RIGHTPAR(")"), 
    LEFTSQUAREB("["), 
    RIGHTSQUAREB("]"),
    LEFTCURLYB("{"), 
    RIGHTCURLYB("}"),
    NUMBER, 
    BOOLEAN("true|false"),
    CHAR, 
    STRING, 
    DEFINE("define"), 
    LET("let"), 
    COND("cond"), 
    IF("if"), 
    BEGIN("begin"), 
    IDENTIFIER;

    private final String literal; // The literal representation of the token type

    // Constructor without literal
    TokenType() {
        this(null);
    }

    // Constructor with literal
    TokenType(String literal) {
        this.literal = literal;
    }

    // Get the length of the literal
    public int length() {
        return literal != null ? literal.length() : 0;
    }

    // Check if the input string matches the token type at the given index
    public int match(String input, int index) {
        // If the token type has a literal, check if the input starts with the literal at the given index
        if (literal != null && input.startsWith(literal, index)) {
            return index + literal.length();
        }

        // If the token type is NUMBER, CHAR, STRING, or IDENTIFIER, try to match the input
        if (this == NUMBER) {
            int newIndex = matchNumber(input, index);
            if (newIndex != -1) {
                return newIndex;
            }
        } else if (this == CHAR) {
            int newIndex = matchChar(input, index);
            if (newIndex != -1) {
                return newIndex;
            }
        } else if (this == STRING) {
            int newIndex = matchString(input, index);
            if (newIndex != -1) {
                return newIndex;
            }
        } else if (this == IDENTIFIER) {
            int newIndex = matchIdentifier(input, index);
            if (newIndex != -1) {
                return newIndex;
            }
        }

        // If no match is found, return -1
        return -1;
    }

    // Match a number in the input string at the given index
    private int matchNumber(String input, int index) {
        boolean hasDigits = false;
        boolean hasDecimal = false;

        // Check for a sign character (optional)
        if (index < input.length() && (input.charAt(index) == '-' || input.charAt(index) == '+')) {
            index++;
        }

        // Match the integer part
        while (index < input.length() && Character.isDigit(input.charAt(index))) {
            hasDigits = true;
            index++;
        }

        // Match the decimal point and fractional part (optional)
        if (index < input.length() && input.charAt(index) == '.' && !hasDecimal) {
            hasDecimal = true;
            index++;
            while (index < input.length() && Character.isDigit(input.charAt(index))) {
                hasDigits = true;
                index++;
            }
        }

        // If the number has digits, return the new index; otherwise, return -1
        if (hasDigits) {
            return index;
        }

        return -1;
    }        

    // Match a character literal in the input string at the given index
    private int matchChar(String input, int index) {
        // Check for the opening single quote
        if (index < input.length() && input.charAt(index) == '\'') {
            index++;
            // Check for a single character inside the quotes
            if (index < input.length() && input.charAt(index) != '\'') {
                index++;
                // Check for the closing single quote
                if (index < input.length() && input.charAt(index) == '\'') {
                    return index + 1;
                }
            }
        }
        return -1;
    }

    // Match a string literal in the input string at the given index
    private int matchString(String input, int index) {
        // Check for the opening double quote
        if (index < input.length() && input.charAt(index) == '\"') {
            index++;
            // Match characters inside the double quotes
            while (index < input.length() && input.charAt(index) != '\"') {
                index++;
            }
            // Check for the closing double quote
            if (index < input.length() && input.charAt(index) == '\"') {
                return index + 1;
            }
        }
        return -1;
    }

    // Match an identifier in the input string at the given index
    private int matchIdentifier(String input, int index) {
        // Check for a valid identifier start character
        if (index < input.length() && isIdentifierStartChar(input.charAt(index))) {
            index++;
            // Match subsequent identifier characters
            while (index < input.length() && isIdentifierChar(input.charAt(index))) {
                index++;
            }
            return index;
        }
        return -1;
    }

    // Check if a character is a valid identifier start character
    private boolean isIdentifierStartChar(char c) {
        return Character.isLetter(c) || c == '_' || c == '.' || c == '+' || c == '-' || c == '!' || c == '*' || c == '/' || c == ':' || c == '<' || c == '=' || c == '>' || c == '?';
    }

    // Check if a character is a valid identifier character
    private boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '.' || c == '+' || c == '-' || c == '!';
    }
}