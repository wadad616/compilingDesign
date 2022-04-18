import lexicalAnalysis.Lex;
import lexicalAnalysis.LexToken;
import org.junit.Test;

public class TestLex {
    @Test
    public void mainTest(){
        Lex lex = new Lex("test.txt");
        lex.LexProcess();
        for (Object o:lex.tokenList){
            System.out.println(o);
        }
    }
}
