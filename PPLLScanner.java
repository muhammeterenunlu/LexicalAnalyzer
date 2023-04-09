import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PPLLScanner {

    public static void main(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 1) {
            System.err.println("Usage: java PPLLScanner <input_file>");
            System.exit(1);
        }

        try {
            // Read the contents of the input file
            String input = readFile(args[0]);
            // Tokenize the input
            List<Token> tokens = tokenize(input);
            // Print the tokens
            for (Token token : tokens) {
                System.out.println(token);
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        } catch (LexicalException e) {
            System.err.println("Lexical error: " + e.getMessage());
        }
    }

    // Read the contents of a file into a string
    private static String readFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line;
            // Read each line and append it to the StringBuilder
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    // Tokenize the input string
    private static List<Token> tokenize(String input) throws LexicalException {
        List<Token> tokens = new ArrayList<>();
        int lineNumber = 1;
        int position = 1;

        // Remove comments from the input
        input = removeComments(input);

        int index = 0;
        while (index < input.length()) {
            boolean found = false;

            // Iterate over each TokenType and try to match the input
            for (TokenType tokenType : TokenType.values()) {
                int newIndex = tokenType.match(input, index);
                if (newIndex != -1) {
                    found = true;
                    tokens.add(new Token(tokenType, lineNumber, position));
                    position += newIndex - index;
                    index = newIndex;
                    break;
                }
            }

            // If no TokenType matches, handle whitespace and unrecognized tokens
            if (!found) {
                if (input.charAt(index) == '\n') {
                    lineNumber++;
                    position = 1;
                    index++;
                } else if (Character.isWhitespace(input.charAt(index))) {
                    position++;
                    index++;
                } else {
                    throw new LexicalException("Unrecognized token '" + input.charAt(index) + "' at line " + lineNumber + ", position " + position);
                }
            }
        }
        return tokens;
    }

    // Remove comments from the input string
    private static String removeComments(String input) {
        StringBuilder sb = new StringBuilder();
        boolean inComment = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            // If a comment start symbol is found, set inComment to true
            if (c == '~') {
                inComment = true;
            // If a newline character is found, set inComment to false
            } else if (c == '\n') {
                inComment = false;
            }
            // Append characters to the StringBuilder if not in a comment
            if (!inComment) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}