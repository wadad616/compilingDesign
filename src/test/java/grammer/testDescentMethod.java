package grammer;

import lexicalAnalysis.Lex;
import lexicalAnalysis.LexToken;
import org.junit.Test;

import java.util.List;

public class testDescentMethod {
    @Test
    public void mainTest(){
        Lex lex = new Lex("source.txt");
        lex.LexProcess();
        List<LexToken> tokenList = lex.tokenList;
        DescentMethod descentMethod = new DescentMethod(tokenList);
        TreeNode program = descentMethod.program();
    }
}
