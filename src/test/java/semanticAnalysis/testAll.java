package semanticAnalysis;

import grammer.DescentMethod;
import grammer.TreeNode;
import lexicalAnalysis.Lex;
import lexicalAnalysis.LexToken;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class testAll {
    @Test
    public void testAllTest() {
        Lex lex = new Lex("source/testAll/source.txt");
        lex.LexProcess();
        List<LexToken> tokenList = lex.tokenList;
        DescentMethod descentMethod = new DescentMethod(tokenList);
        TreeNode program = descentMethod.program();
        SymbolTable symbolTable = new SymbolTable(program);
        List<Map<String, SymbolAttribute>> symbolTables = symbolTable.getSymbolTables();
        for (Map<String, SymbolAttribute> map : symbolTables) {
            System.out.println("===================================");
            for (Map.Entry<String, SymbolAttribute> e : map.entrySet()) {
                System.out.println(e);
            }
        }
        System.out.println(symbolTable.error);

        symbolTable.traverseAll(program);
        System.out.println(symbolTable.error);

    }
}
