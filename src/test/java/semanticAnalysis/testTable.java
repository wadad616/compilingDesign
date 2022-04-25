package semanticAnalysis;

import grammer.DescentMethod;
import grammer.TreeNode;
import lexicalAnalysis.Lex;
import lexicalAnalysis.LexToken;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class testTable {
    @Test
    public void mainTest() {
        Lex lex = new Lex("source/source.txt");
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
    }
    @Test
    public void mainTest1() {
        Lex lex = new Lex("source/source1.txt");
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
    }
    @Test
    public void mainTest2() {
        Lex lex = new Lex("source/source2.txt");
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
    }
    @Test
    public void 数组上下界错误() {
        Lex lex = new Lex("source/数组上下界错误.txt");
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
    }
    @Test
    public void 记录中错误() {
        Lex lex = new Lex("source/记录中错误.txt");
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
    }
}
