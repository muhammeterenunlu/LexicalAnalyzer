class SyntaxError extends Exception {
    public SyntaxError(String message, Token token) {
        super("SYNTAX ERROR [" + token.lineNumber + ":" + token.position + "]: " + message);
    }
}