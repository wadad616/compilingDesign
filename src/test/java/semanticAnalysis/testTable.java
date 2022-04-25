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
        Lex lex = new Lex("source/testTable/source.txt");
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
        Lex lex = new Lex("source/testTable/source1.txt");
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
        Lex lex = new Lex("source/testTable/source2.txt");
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
        Lex lex = new Lex("source/testTable/数组上下界错误.txt");
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
        Lex lex = new Lex("source/testTable/记录中错误.txt");
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
    public void 变量重复错误() {
        Lex lex = new Lex("source/testTable/变量重复错误.txt");
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
    public void 函数测试() {
        Lex lex = new Lex("source/testTable/函数测试.txt");
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

    @Test
    public void 字符不能进行算术运算() {
        Lex lex = new Lex("source/testAll/字符不能进行算术运算.txt");
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
    @Test
    public void 过程参数类型未匹配() {
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
    @Test
    public void 过程调用参数未定义() {
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

    @Test
    public void 参数数量错误() {
        Lex lex = new Lex("source/testAll/参数数量错误.txt");
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
    @Test
    public void 非过程调用() {
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
    @Test
    public void 写语句表达式类型错误() {
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
    @Test
    public void 无法读取非变量标识符() {
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
    @Test
    public void 赋值语句不相容() {
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
    @Test
    public void 条件语句类型不匹配() {
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
    @Test
    public void 记录中不存在此成员() {
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

    @Test
    public void 数组越界() {
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
    @Test
    public void 非记录进行记录调用() {
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
    @Test
    public void 非数组进行数组调用() {
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
    @Test
    public void 数组下标不为数字() {
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
    @Test
    public void 类型标识进行错误操作() {
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
    @Test
    public void 不同类型进行运算() {
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
    @Test
    public void 赋值语句左式不为变量() {
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
