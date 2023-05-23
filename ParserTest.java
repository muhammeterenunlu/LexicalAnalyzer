
import java.io.IOException;

public class ParserTest {
    public ParserTest() {
    }

    public static void main(String[] var0) throws LexicalException, IOException {

        String fileName= "parserTest.ppll";

        Parser var1 = new Parser(fileName);
        var1.parseProgram();
    }
}
