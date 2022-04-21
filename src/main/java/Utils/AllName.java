package Utils;

import java.util.HashMap;
import java.util.Map;

public class AllName {
    public static enum LexType {
        //默认值
        LexTypeDefault,
        /*簿记单词符号*/
        EOF, ERROR,
        /*保留字*/
        PROGRAM, PROCEDURE, TYPE, VAR, IF, THEN, ELSE, FI, WHILE,
        DO, ENDWH, BEGIN, END, READ, WRITE, ARRAY, OF, RECORD, RETURN,
        //类型
        INTEGER, CHAR,
        /*多字符单词符号*/
        ID, INTC, CHARC,
        /*特殊符号*/
        ASSIGN, EQ, LT, PLUS, MINUS, TIMES, OVER, LPAREN, RPAREN, DOT, COLON, SEMI, COMMA, LMIDPAREN, RMIDPAREN, UNDERANGE,

        //表示参数是形参还是实参
        ValParamType, VarParaType,
        //变量类型
        idV, ArrayMembV, FieldMembV,
        //语法树节点中检查类型
        Void, Integer, Boolean
    }

    //语法树的节点类型    待补充
    public static enum NodeKind {
        //默认值
        NodeKindDefault,
        ProK, PheadK, TypeK, VarK, ProcDecK, StmLK, DecK, StmtK, ExpK, TotalDecK,ParamDeck
    }

    //语法树的节点的具体类型
    public static enum memberKind {
        //默认值
        memberKindDefault,
        //dec类型
        ArrayK, CharK, IntegerK, RecordK,
        //IdK既可以dec类型，也可以为exp类型
        IdK,
        //stmt类型
        IfK, WhileK, AssignK, ReadK, WriteK, CallK, ReturnK,
        //特殊stmt类型 这些stmtK 后可以接上多个普通的stmt类型，就和StmLK差不多了
        ThenK,ElseK,DoK,
        //exp类型  IdK既可以dec类型，也可以为exp类型 或者将IdK看作式变量也可以
        OpK, ConstK,

    }


    public static Map<String, LexType> reservedWord;
    public static Map<String, LexType> divideWord;

    static {
        //进行初始化的过程
        reservedWord = new HashMap<>();
        divideWord = new HashMap<>();
        //保留字的初始化
        reservedWord.put("program", LexType.PROGRAM);
        reservedWord.put("procedure", LexType.PROCEDURE);
        reservedWord.put("type", LexType.TYPE);
        reservedWord.put("var", LexType.VAR);
        reservedWord.put("if", LexType.IF);
        reservedWord.put("then", LexType.THEN);
        reservedWord.put("else", LexType.ELSE);
        reservedWord.put("fi", LexType.FI);
        reservedWord.put("while", LexType.WHILE);
        reservedWord.put("do", LexType.DO);
        reservedWord.put("endwh", LexType.ENDWH);
        reservedWord.put("begin", LexType.BEGIN);
        reservedWord.put("end", LexType.END);
        reservedWord.put("read", LexType.READ);
        reservedWord.put("write", LexType.WRITE);
        reservedWord.put("array", LexType.ARRAY);
        reservedWord.put("of", LexType.OF);
        reservedWord.put("record", LexType.RECORD);
        reservedWord.put("return", LexType.RETURN);
        reservedWord.put("integer", LexType.INTEGER);
        reservedWord.put("char", LexType.CHAR);

        //分隔符的初始化
        divideWord.put(":=", LexType.ASSIGN);
        divideWord.put("=", LexType.EQ);
        divideWord.put("<", LexType.LT);
        divideWord.put("+", LexType.PLUS);
        divideWord.put("-", LexType.MINUS);
        divideWord.put("*", LexType.TIMES);
        divideWord.put("/", LexType.OVER);
        divideWord.put("(", LexType.LPAREN);
        divideWord.put(")", LexType.RPAREN);
        divideWord.put(".", LexType.DOT);
        divideWord.put(":", LexType.COLON);
        divideWord.put(";", LexType.SEMI);
        divideWord.put(",", LexType.COMMA);
        divideWord.put("[", LexType.LMIDPAREN);
        divideWord.put("]", LexType.RMIDPAREN);
        divideWord.put("..", LexType.UNDERANGE);
    }

    public static boolean IfReservedWord(String s) {
        return reservedWord.containsKey(s);
    }
}
