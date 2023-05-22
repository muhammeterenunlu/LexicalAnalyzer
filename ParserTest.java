import java.io.IOException;

public class ParserTest {



    public static void main(String[] args) throws LexicalException, IOException {
        Parser parser = new Parser("fibonacci.ppll");

        parser.parseProgram();



    }
}
