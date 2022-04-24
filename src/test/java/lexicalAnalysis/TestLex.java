package lexicalAnalysis;

import org.junit.Test;

public class TestLex {
    @Test
    public void mainTest(){
        Lex lex = new Lex("source.txt");
        lex.LexProcess();
        for (Object o:lex.tokenList){
            System.out.println(o);
        }
    }
}
