package LL1;

import Utils.AllName;
import Utils.Expression;
import Utils.SelectedProcess;
import grammer.TreeNode;
import lexicalAnalysis.LexToken;

import java.util.*;

import static Utils.AllName.NodeKind.TypeK;


//注意压栈需要反向压栈，注意初始化的时候就是反向的

public class LL1Process {
    //表示的token序列
    List<LexToken> tokenList;
    //表示此时token序列读取的位置

    int tokenIndex;

    //符号表栈  其中的元素为各种VN 和 VT
    Stack<String> symbolStack;

    //节点栈
    Stack<TreeNode> nodeStack;

    //运算符栈
    Stack<String> typeStack;

    //LL1表
    Map<String, Map<String, Integer>> LL1Table;

    //表达式
    List<List<String>> rightExpList;

    //终结符
    public Set<String> VT;
    //非终结符
    public Set<String> VN;

    public LL1Process(List<LexToken> tokenList) {
        this.tokenList = tokenList;
        tokenIndex = 0;
        nodeStack = new Stack<>();
        typeStack = new Stack<>();
        initLL1Table();
    }

    //初始化LL1分析表
    void initLL1Table() {
        SelectedProcess selectedProcess = new SelectedProcess("test.txt");
        selectedProcess.traverseVN();
        List<Expression> expressionList = selectedProcess.expressionList;
        Map<String, Map<String, Integer>> LL1Table = new HashMap<>();
        rightExpList = new ArrayList<>();
        int index = 1;
        for (Expression s : expressionList) {
            Set<String> set = selectedProcess.predictSet.get(s);
            List<String> list = new ArrayList<>();
            //初始化
            for (int i = s.rightExp.size() - 1; i >= 0; i--) {
                if (AllName.divideWord.containsKey(s.rightExp.get(i))) {
                    String s1 = AllName.divideWord.get(s.rightExp.get(i)).toString();
                    list.add(s1);
                } else {
                    list.add(s.rightExp.get(i));
                }
            }
            rightExpList.add(s.rightExp);
            //初始化值
            Map<String, Integer> nowMap = null;
            if (!LL1Table.containsKey(s.leftExp)) {
                LL1Table.put(s.leftExp, new HashMap<>());
            }
            nowMap = LL1Table.get(s.leftExp);
            for (String ss : set) {
                if (AllName.divideWord.containsKey(ss)) {
                    AllName.LexType lexType = AllName.divideWord.get(ss);
                    ss = lexType.toString();
                }
                nowMap.put(ss, index);
            }
            index++;
        }
    }

    //进行匹配
    boolean match(String s) {
        return s.equals(tokenList.get(tokenIndex).getType().toString());
    }

    boolean match(List<String> list) {
        for (String s : list) {
            if (match(s)) {
                return true;
            }
        }
        return false;
    }

    boolean match(String[] list) {
        for (String s : list) {
            if (match(s)) {
                return true;
            }
        }
        return false;
    }

    boolean next() {
        if (tokenIndex < tokenList.size()) {
            tokenIndex++;
            return true;
        }
        return false;
    }

    boolean ifNull() {
        return tokenIndex == tokenList.size();
    }

    int getPri(AllName.LexType type) {
        switch (type) {
            case DOT:
                return 0;
            case LT, EQ:
                return 1;
            case PLUS, MINUS:
                return 2;
            case TIMES, OVER:
                return 3;
            default:
                return -1;
        }
    }

    boolean judVT(String s) {
        return VT.contains(s);
    }

    void pushSymbol(int num) {
        pushSymbol(num, 0);
    }

    void pushSymbol(int num, int begin) {
        List<String> list = rightExpList.get(num - 1);
        //进行第一步的压栈
        for (int i = begin; i < list.size(); i++) {
            symbolStack.push(list.get(i));
        }
    }

    LexToken getCurrentToken() {
        return tokenList.get(tokenIndex);
    }

    TreeNode parse() {
        TreeNode root = new TreeNode();
        root.nodeKind = AllName.NodeKind.ProK;
        root.child = new ArrayList<>();
        TreeNode t1 = new TreeNode();
        TreeNode t2 = new TreeNode();
        TreeNode t3 = new TreeNode();
        //进行树的连接
        root.child.add(t1);
        t1.father = root;
        root.child.add(t2);
        t2.father = root;
        root.child.add(t3);
        t3.father = root;
        nodeStack.push(t3);
        nodeStack.push(t2);
        nodeStack.push(t1);
        //可以直接先进行一波匹配
        List<String> list = rightExpList.get(0);
        //进行第一步的压栈
        for (String s : list) {
            symbolStack.push(s);
        }
        //符号栈非空的处理
        while (!symbolStack.empty()) {
            if (judVT(symbolStack.peek())) {
                if (match(symbolStack.peek())) {
                    next();
                } else {
                    //错误处理
                }
            } else {
                Integer integer = LL1Table.get(symbolStack.peek()).get(tokenList.get(tokenIndex));
                if(integer==null){
                    //错误处理
                }
                predict(integer);
            }
        }
        //符号栈为空的处理
        return root;
    }

    //跳转到对应的函数


    void process1() {
    }

    /**
     * @Description 创建语法树节点
     * ProgramHead ::= PROGRAM ProgramName     ["PROGRAM"]
     * TODO test
     * @Date 2022/4/21 16:16
     **/
    void process2() {
        pushSymbol(2);
        //head节点
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.PheadK;
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
        //为了进行后面的ID匹配
        nodeStack.push(treeNode);
    }

    /**
     * @Description //注意到这个时候已经进行之前的匹配 需要设置标识符 进行了弹栈
     * ProgramName ::= ID  ["ID"]
     * TODO 错误处理 test
     * @Date 2022/4/21 16:28
     **/
    void process3() {
        if (!match("ID")) {
            //错误处理
        }
        String sem = getCurrentToken().getSem();
        TreeNode peek = nodeStack.peek();
        peek.boolName();
        peek.name.add(sem);
        peek.idNum++;
        pushSymbol(2, 1);
    }


    /**
     * @Description 添加了两个儿子 但是有一个儿子并没有添加  因为那个儿子是有多个的
     * DeclarePart ::= TypeDec VarDec ProcDec     ["PROCEDURE","VAR","BEGIN","TYPE"]
     * TODO
     * @Date 2022/4/21 18:40
     **/
    void process4() {
        TreeNode treeNode = nodeStack.peek();
        treeNode.boolChild();
        //总体声明
        treeNode.nodeKind = AllName.NodeKind.TotalDecK;

        //TypeK
        TreeNode t = new TreeNode();
        treeNode.boolChild();
        t.nodeKind = TypeK;

        //VarK
        TreeNode v = new TreeNode();
        treeNode.boolChild();
        t.nodeKind = AllName.NodeKind.VarK;


        treeNode.child.add(t);
        t.father = treeNode;
        treeNode.child.add(v);
        v.father = treeNode;

        nodeStack.push(v);
        nodeStack.push(t);
    }

    /**
     * @Description 无操作
     * TypeDec ::= $                   ["PROCEDURE","VAR","BEGIN"]
     * TODO
     * @Date 2022/4/21 18:52
    **/
    void process5() {
    }
    /**
     * @Description
     * TODO
     * @Date 2022/4/21 18:55
    **/
    void process6() {
    }

    void process7() {
    }

    void process8() {
    }

    void process9() {
    }

    void process10() {
    }

    void process11() {
    }

    void process12() {
    }

    void process13() {
    }

    void process14() {
    }

    void process15() {
    }

    void process16() {
    }

    void process17() {
    }

    void process18() {
    }

    void process19() {
    }

    void process20() {
    }

    void process21() {
    }

    void process22() {
    }

    void process23() {
    }

    void process24() {
    }

    void process25() {
    }

    void process26() {
    }

    void process27() {
    }

    void process28() {
    }

    void process29() {
    }

    void process30() {
    }

    void process31() {
    }

    void process32() {
    }

    void process33() {
    }

    void process34() {
    }

    void process35() {
    }

    void process36() {
    }

    void process37() {
    }

    void process38() {
    }

    void process39() {
    }

    void process40() {
    }

    void process41() {
    }

    void process42() {
    }

    void process43() {
    }

    void process44() {
    }

    void process45() {
    }

    void process46() {
    }

    void process47() {
    }

    void process48() {
    }

    void process49() {
    }

    void process50() {
    }

    void process51() {
    }

    void process52() {
    }

    void process53() {
    }

    void process54() {
    }

    void process55() {
    }

    void process56() {
    }

    void process57() {
    }

    void process58() {
    }

    void process59() {
    }

    void process60() {
    }

    void process61() {
    }

    void process62() {
    }

    void process63() {
    }

    void process64() {
    }

    void process65() {
    }

    void process66() {
    }

    void process67() {
    }

    void process68() {
    }

    void process69() {
    }

    void process70() {
    }

    void process71() {
    }

    void process72() {
    }

    void process73() {
    }

    void process74() {
    }

    void process75() {
    }

    void process76() {
    }

    void process77() {
    }

    void process78() {
    }

    void process79() {
    }

    void process80() {
    }

    void process81() {
    }

    void process82() {
    }

    void process83() {
    }

    void process84() {
    }

    void process85() {
    }

    void process86() {
    }

    void process87() {
    }

    void process88() {
    }

    void process89() {
    }

    void process90() {
    }

    void process91() {
    }

    void process92() {
    }

    void process93() {
    }

    void process94() {
    }

    void process95() {
    }

    void process96() {
    }

    void process97() {
    }

    void process98() {
    }

    void process99() {
    }

    void process100() {
    }

    void process101() {
    }

    void process102() {
    }

    void process103() {
    }

    void process104() {
    }

    void predict(int num) {
        switch (num) {
            case 1:
                process1();
                break;
            case 2:
                process2();
                break;
            case 3:
                process3();
                break;
            case 4:
                process4();
                break;
            case 5:
                process5();
                break;
            case 6:
                process6();
                break;
            case 7:
                process7();
                break;
            case 8:
                process8();
                break;
            case 9:
                process9();
                break;
            case 10:
                process10();
                break;
            case 11:
                process11();
                break;
            case 12:
                process12();
                break;
            case 13:
                process13();
                break;
            case 14:
                process14();
                break;
            case 15:
                process15();
                break;
            case 16:
                process16();
                break;
            case 17:
                process17();
                break;
            case 18:
                process18();
                break;
            case 19:
                process19();
                break;
            case 20:
                process20();
                break;
            case 21:
                process21();
                break;
            case 22:
                process22();
                break;
            case 23:
                process23();
                break;
            case 24:
                process24();
                break;
            case 25:
                process25();
                break;
            case 26:
                process26();
                break;
            case 27:
                process27();
                break;
            case 28:
                process28();
                break;
            case 29:
                process29();
                break;
            case 30:
                process30();
                break;
            case 31:
                process31();
                break;
            case 32:
                process32();
                break;
            case 33:
                process33();
                break;
            case 34:
                process34();
                break;
            case 35:
                process35();
                break;
            case 36:
                process36();
                break;
            case 37:
                process37();
                break;
            case 38:
                process38();
                break;
            case 39:
                process39();
                break;
            case 40:
                process40();
                break;
            case 41:
                process41();
                break;
            case 42:
                process42();
                break;
            case 43:
                process43();
                break;
            case 44:
                process44();
                break;
            case 45:
                process45();
                break;
            case 46:
                process46();
                break;
            case 47:
                process47();
                break;
            case 48:
                process48();
                break;
            case 49:
                process49();
                break;
            case 50:
                process50();
                break;
            case 51:
                process51();
                break;
            case 52:
                process52();
                break;
            case 53:
                process53();
                break;
            case 54:
                process54();
                break;
            case 55:
                process55();
                break;
            case 56:
                process56();
                break;
            case 57:
                process57();
                break;
            case 58:
                process58();
                break;
            case 59:
                process59();
                break;
            case 60:
                process60();
                break;
            case 61:
                process61();
                break;
            case 62:
                process62();
                break;
            case 63:
                process63();
                break;
            case 64:
                process64();
                break;
            case 65:
                process65();
                break;
            case 66:
                process66();
                break;
            case 67:
                process67();
                break;
            case 68:
                process68();
                break;
            case 69:
                process69();
                break;
            case 70:
                process70();
                break;
            case 71:
                process71();
                break;
            case 72:
                process72();
                break;
            case 73:
                process73();
                break;
            case 74:
                process74();
                break;
            case 75:
                process75();
                break;
            case 76:
                process76();
                break;
            case 77:
                process77();
                break;
            case 78:
                process78();
                break;
            case 79:
                process79();
                break;
            case 80:
                process80();
                break;
            case 81:
                process81();
                break;
            case 82:
                process82();
                break;
            case 83:
                process83();
                break;
            case 84:
                process84();
                break;
            case 85:
                process85();
                break;
            case 86:
                process86();
                break;
            case 87:
                process87();
                break;
            case 88:
                process88();
                break;
            case 89:
                process89();
                break;
            case 90:
                process90();
                break;
            case 91:
                process91();
                break;
            case 92:
                process92();
                break;
            case 93:
                process93();
                break;
            case 94:
                process94();
                break;
            case 95:
                process95();
                break;
            case 96:
                process96();
                break;
            case 97:
                process97();
                break;
            case 98:
                process98();
                break;
            case 99:
                process99();
                break;
            case 100:
                process100();
                break;
            case 101:
                process101();
                break;
            case 102:
                process102();
                break;
            case 103:
                process103();
                break;
            case 104:
                process104();
                break;
            default:
                break;//注意
        }
    }

}
