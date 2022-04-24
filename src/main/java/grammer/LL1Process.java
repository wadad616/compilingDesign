package grammer;

import Utils.AllName;
import Utils.Expression;
import Utils.SelectedProcess;
import grammer.TreeNode;
import lexicalAnalysis.LexToken;

import java.util.*;

import static Utils.AllName.NodeKind.*;


//注意压栈需要反向压栈，注意初始化的时候就是反向的
//每次做题的时候注意一下栈内的状态
//结果为$ 的地方需要改进.第一改进一下那个压栈函数
// 第二改一下有些出栈的过程需要进行修改，父节点不一定需要进行弹出
//往回看一下有进行树间关系连接的时候，父亲是否是父亲
//TODO 一个more可能有多个地方会使用到，看看前面有没有使用到
//TODO 之前许多节点创建的时候并没有进行压栈操作
//已经完成 todo if语句和when语句可以进行适当的调整，将if语句中的then和else当成两个if语句的儿子，而不是同级别
//todo 每个进行弹栈的地方都需要重现进行思考
//todo 注意一下数组成员的childId
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
    Stack<TreeNode> typeStack;

    //表达式节点栈,或者操作数节点
    Stack<TreeNode> expStack;

    //LL1表
    Map<String, Map<String, Integer>> LL1Table;

    //表达式
    List<List<String>> rightExpList;

    //终结符
    public Set<String> VT;
    //非终结符
    public Set<String> VN;

    TreeNode ifNode;
    TreeNode whenNode;
    TreeNode tempVarNode;
    int expFlag;

    AllName.LexType getExpResult = AllName.LexType.NotBool;
    //思考了一下觉得还是有必有的
    AllName.LexType getExpResult1 = AllName.LexType.NotBool;


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

    int getPri(String type) {
        switch (type) {
            case "DOT":
                return 0;
            case "LT", "EQ":
                return 1;
            case "PLUS", "MINUS":
                return 2;
            case "TIMES", "OVER":
                return 3;
            default:
                return -1;
        }
    }

    int getCurrentPri() {
        if (typeStack.empty()) {
            return 0;
        } else {
            //获得操作符的符号
            return getPri(typeStack.peek().attr.expAttr.op.toString());
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
                if (integer == null) {
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
        peek.lineno = getCurrentToken().getLineShow();
        pushSymbol(2, 1);
    }


    /**
     * @Description 添加了两个儿子 但是有一个儿子并没有添加  因为那个儿子是有多个的
     * DeclarePart ::= TypeDec VarDec ProcDec     ["PROCEDURE","VAR","BEGIN","TYPE"]
     * TODO
     * @Date 2022/4/21 18:40
     **/
    void process4() {
        pushSymbol(4);
        TreeNode treeNode = nodeStack.peek();
        treeNode.boolChild();
        //总体声明
        treeNode.nodeKind = AllName.NodeKind.TotalDecK;
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
     * @Description 基本操作
     * TypeDec ::= TypeDeclaration     ["TYPE"]
     * TODO
     * @Date 2022/4/21 18:55
     **/
    void process6() {
        pushSymbol(6);
    }

    /**
     * @Description 想了一下能够进入这里就说明了必定匹配成功了 新建了节点 有压栈无出栈
     * TypeDeclaration ::= TYPE TypeDecList  ["TYPE"]
     * TODO
     * @Date 2022/4/21 19:03
     **/
    void process7() {
        pushSymbol(7);
        //建立基本的父子关系
        //TypeK
        TreeNode t = new TreeNode();
        t.nodeKind = TypeK;
        LexToken token = getCurrentToken();
        t.lineno = token.getLineShow();
        TreeNode peek = nodeStack.peek();
        peek.child.add(t);
        t.father = peek;
        nodeStack.push(t);
    }

    /**
     * @Description 有压栈 进行基础设置
     * TypeDecList ::= TypeId = TypeName ; TypeDecMore  ["ID"]
     * TODO
     * @Date 2022/4/21 19:11
     **/
    void process8() {
        pushSymbol(8);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.DecK;
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
    }

    /**
     * @Description 无作用 我尝试一下先全部弹掉
     * TypeDecMore ::= $                ["PROCEDURE","VAR","BEGIN"]
     * TODO
     * @Date 2022/4/21 19:17
     **/
    void process9() {
        while (nodeStack.peek().nodeKind != AllName.NodeKind.TypeK) {
            nodeStack.pop();
        }
        //最终需要将这弹出
        nodeStack.pop();
    }

    /**
     * @Description 基本处理
     * TypeDecMore ::= TypeDecList      ["ID"]
     * TODO
     * @Date 2022/4/21 19:18
     **/
    void process10() {
        pushSymbol(10);
    }


    /**
     * @Description 进行DecK节点的内容的补充
     * TypeId ::= ID    ["ID"]
     * TODO Test
     * @Date 2022/4/21 19:18
     **/
    void process11() {
        pushSymbol(11);
        TreeNode peek = nodeStack.peek();
        peek.lineno = getCurrentToken().getLineShow();
        peek.boolName();
        peek.name.add(getCurrentToken().getSem());
        peek.idNum++;
    }

    /**
     * @Description 无操作
     * TypeName ::= BaseType        ["CHAR","INTEGER"]
     * TODO
     * @Date 2022/4/21 19:21
     **/
    void process12() {
        pushSymbol(12);
    }

    /**
     * @Description 无操纵
     * TypeName ::= StructureType   ["ARRAY","RECORD"]
     * TODO
     * @Date 2022/4/21 19:23
     **/
    void process13() {
        pushSymbol(13);
    }

    /**
     * @Description 无操作
     * TypeName ::= ID              ["ID"]
     * TODO
     * @Date 2022/4/21 19:27
     **/
    void process14() {
        pushSymbol(14);
        TreeNode t = nodeStack.peek();
        t.memberKind = AllName.memberKind.IdK;
        t.idNum++;
        t.boolName();
        t.name.add(getCurrentToken().getSem());
        if (t.typeName == null) {
            t.typeName = new ArrayList<>();
        }
        t.typeName.add(getCurrentToken().getSem());
        if (t.lineno != 0) {
            t.lineno = getCurrentToken().getLineShow();
        }
    }

    /**
     * @Description 设置类型
     * BaseType ::= INTEGER     ["INTEGER"]
     * TODO
     * @Date 2022/4/21 20:28
     **/
    void process15() {
        pushSymbol(15);
        TreeNode t = nodeStack.peek();
        if (t.memberKind == AllName.memberKind.ArrayK) {
            t.attr.arrayAttr.childType = AllName.memberKind.IntegerK;
        } else {
            t.memberKind = AllName.memberKind.IntegerK;
            if (t.lineno != 0) {
                t.lineno = getCurrentToken().getLineShow();
            }
        }

    }

    /**
     * @Description 设置类型
     * TODO
     * @Date 2022/4/21 20:29
     **/
    void process16() {
        pushSymbol(16);
        TreeNode t = nodeStack.peek();
        if (t.memberKind == AllName.memberKind.ArrayK) {
            t.attr.arrayAttr.childType = AllName.memberKind.IntegerK;
        } else {
            t.memberKind = AllName.memberKind.CharK;
            if (t.lineno != 0) {
                t.lineno = getCurrentToken().getLineShow();
            }
        }
    }

    //StructureType ::= ArrayType      ["ARRAY"]
    void process17() {
        pushSymbol(17);
    }

    //StructureType ::= RecType        ["RECORD"]
    void process18() {
        pushSymbol(18);
    }


    //ArrayType ::= ARRAY [ Low .. Top ] OF BaseType   ["ARRAY"]
    void process19() {
        pushSymbol(19);
        TreeNode t = nodeStack.peek();
        t.memberKind = AllName.memberKind.ArrayK;
        if (t.lineno != 0) {
            t.lineno = getCurrentToken().getLineShow();
        }
        if (t.attr != null) {
            t.setAttr("array");
        }

    }

    //Low ::= INTC     ["INTC"]
    void process20() {
        pushSymbol(20);
        TreeNode t = nodeStack.peek();
        int i = 0;
        try {
            i = Integer.parseInt(getCurrentToken().getSem());
        } catch (Exception e) {
            //错误处理
        }
        t.attr.arrayAttr.low = i;

    }

    //Top ::= INTC     ["INTC"]
    void process21() {
        pushSymbol(21);
        TreeNode t = nodeStack.peek();
        int i = 0;
        try {
            i = Integer.parseInt(getCurrentToken().getSem());
        } catch (Exception e) {
            //错误处理
        }
        t.attr.arrayAttr.low = i;
    }

    //RecType ::= RECORD FieldDecList END      ["RECORD"]
    //可能需要一个 临时的saveP
    void process22() {
        pushSymbol(22);
        TreeNode t = nodeStack.peek();
        t.memberKind = AllName.memberKind.ReturnK;
        if (t.lineno != 0) {
            t.lineno = getCurrentToken().getLineShow();
        }
    }

    //FieldDecList ::= BaseType IdList ; FieldDecMore      ["CHAR","INTEGER"]
    void process23() {
        pushSymbol(23);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.DecK;
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
    }

    //FieldDecList ::= ArrayType IdList ; FieldDecMore     ["ARRAY"]
    void process24() {
        pushSymbol(24);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.DecK;
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
    }

    //FieldDecMore ::= $                   ["END"]
    void process25() {
        while (nodeStack.peek().memberKind != AllName.memberKind.RecordK) {
            nodeStack.pop();
        }
        nodeStack.pop();
    }

    //FieldDecMore ::= FieldDecList        ["ARRAY","CHAR","INTEGER"]
    void process26() {
        pushSymbol(26);
    }


    //IdList ::= ID IdMore     ["ID"]
    void process27() {
        pushSymbol(27);
        TreeNode peek = nodeStack.peek();
        peek.boolName();
        peek.name.add(getCurrentToken().getSem());
        peek.idNum++;
    }

    //IdMore ::= $             [";"]
    void process28() {
        pushSymbol(28);
    }

    //IdMore ::= , IdList      [","]
    void process29() {
        pushSymbol(29);
    }


    //VarDec ::= $                 ["PROCEDURE","BEGIN"]
    //有 $但不需要进行基本运算的地方
    void process30() {
        pushSymbol(30);
    }

    //VarDec ::= VarDeclaration    ["VAR"]
    void process31() {
        pushSymbol(31);
    }

    //VarDeclaration ::= VAR VarDecList    ["VAR"]
    void process32() {
        pushSymbol(32);
        TreeNode t = new TreeNode();
        t.nodeKind = VarK;
        LexToken token = getCurrentToken();
        t.lineno = token.getLineShow();
        TreeNode peek = nodeStack.peek();
        peek.child.add(t);
        t.father = peek;
        nodeStack.push(t);
    }

    //VarDecList ::= TypeName VarIdList ; VarDecMore   ["ARRAY","RECORD","CHAR","ID","INTEGER"]
    void process33() {
        pushSymbol(33);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.DecK;
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
    }

    //VarDecMore ::= $             ["PROCEDURE","BEGIN"]
    //等下再来这里
    void process34() {
        while (nodeStack.peek().nodeKind != VarK) {
            nodeStack.pop();
        }
        nodeStack.pop();

    }

    //VarDecMore ::= VarDecList    ["ARRAY","RECORD","CHAR","ID","INTEGER"]
    void process35() {
        pushSymbol(35);
    }

    //VarIdList ::= ID VarIdMore       ["ID"]
    void process36() {
        pushSymbol(36);
        TreeNode peek = nodeStack.peek();
        peek.boolName();
        peek.name.add(getCurrentToken().getSem());
        peek.idNum++;
    }

    //VarIdMore ::= $              [";"]
    void process37() {
        pushSymbol(37);
    }

    //VarIdMore ::= , VarIdList    [","]
    void process38() {
        pushSymbol(38);
    }


    //ProcDec ::= $                ["BEGIN"]
    void process39() {
        pushSymbol(39);
    }

    //ProcDec ::= ProcDeclaration  ["PROCEDURE"]
    void process40() {
        pushSymbol(40);
    }


    //ProcDeclaration ::= PROCEDURE ProcName ( ParamList ) ; ProcDecPart ProcBody ProcDecMore   ["PROCEDURE"]
    void process41() {
        pushSymbol(41);
        TreeNode t = new TreeNode();
        t.nodeKind = ProcDecK;
        LexToken token = getCurrentToken();
        t.lineno = token.getLineShow();
        TreeNode peek = nodeStack.peek();
        peek.child.add(t);
        t.father = peek;
        nodeStack.push(t);
    }

    //ProcDecMore ::= $                ["BEGIN"]
    void process42() {
        pushSymbol(42);
        while (nodeStack.peek().nodeKind != ProK) {
            nodeStack.pop();
        }
        nodeStack.pop();
    }

    //ProcDecMore ::= ProcDeclaration  ["PROCEDURE"]
    void process43() {
        pushSymbol(43);
    }

    //ProcName ::= ID                       ["ID"]
    void process44() {
        pushSymbol(44);
        TreeNode peek = nodeStack.peek();
        peek.boolName();
        peek.name.add(getCurrentToken().getSem());
        peek.idNum++;
    }

    //ParamList ::= $              ["RPAREN"]
    //这里还需要
    //TODO 记得补上
    void process45() {
        pushSymbol(45);

    }

    //ParamList ::= ParamDecList   ["ARRAY","VAR","RECORD","CHAR","ID","INTEGER"]
    void process46() {
        pushSymbol(46);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = ParamDeck;
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
    }

    //ParamDecList ::= Param ParamMore     ["ARRAY","VAR","RECORD","CHAR","ID","INTEGER"]
    void process47() {
        pushSymbol(47);
    }

    //ParamMore ::= $                  ["RPAREN"]
    void process48() {
        pushSymbol(48);
        while (nodeStack.peek().nodeKind != ParamDeck) {
            nodeStack.pop();
        }
        nodeStack.pop();
    }

    //ParamMore ::= ; ParamDecList     ["SEMI"]
    void process49() {
        pushSymbol(49);
        nodeStack.pop();
    }

    //Param ::= TypeName FormList          ["ARRAY","RECORD","CHAR","ID","INTEGER"]
    void process50() {
        pushSymbol(50);
        //基础设置
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.DecK;
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
        if (treeNode.attr == null) {
            treeNode.setAttr("proc");
        }
        treeNode.attr.procAttr.paramType = AllName.LexType.ValParamType;
    }

    //Param ::= VAR TypeName FormList      ["VAR"]
    void process51() {
        pushSymbol(51);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.DecK;
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
        if (treeNode.attr == null) {
            treeNode.setAttr("proc");
        }
        treeNode.attr.procAttr.paramType = AllName.LexType.VarParaType;
    }

    //FormList ::= ID FidMore      ["ID"]
    void process52() {
        pushSymbol(52);
        TreeNode peek = nodeStack.peek();
        peek.boolName();
        peek.name.add(getCurrentToken().getSem());
        peek.idNum++;
    }

    //FidMore ::= $                ["SEMI","RPAREN"]
    void process53() {
        pushSymbol(53);
    }

    //FidMore ::= , FormList       ["COMMA"]
    void process54() {
        pushSymbol(54);
    }

    //ProcDecPart ::= DeclarePart      ["PROCEDURE","VAR","BEGIN","TYPE"]
    void process55() {
        pushSymbol(55);
    }

    //ProcBody ::= ProgramBody     ["BEGIN"]
    void process56() {
        pushSymbol(56);
    }

    //ProgramBody ::= BEGIN StmList END        ["BEGIN"]
    void process57() {
        pushSymbol(57);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.StmLK;
        if (treeNode.lineno != 0) {
            treeNode.lineno = getCurrentToken().getLineShow();
        }
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
    }

    //StmList ::= Stm StmMore      ["READ","RETURN","WHILE","ID","IF","WRITE"]
    void process58() {
        pushSymbol(58);
    }

    //StmMore ::= $            ["FI","ELSE","END","ENDWH"]
    //这个的作用就是弹两次
    void process59() {
        pushSymbol(59);

    }

    //StmMore ::= ; StmList    ["SEMI"]
    void process60() {
        pushSymbol(60);
        nodeStack.pop();
    }

    //Stm ::= ConditionalStm
    void process61() {
    }

    //Stm ::= LoopStm          ["WHILE"]
    void process62() {
        pushSymbol(62);
    }

    //Stm ::= InputStm         ["READ"]
    void process63() {
        pushSymbol(63);
    }

    //Stm ::= OutputStm        ["WRITE"]
    void process64() {
        pushSymbol(64);
    }

    //Stm ::= ReturnStm        ["RETURN"]
    void process65() {
        pushSymbol(65);
    }

    //Stm ::= ID AssCall       ["ID"]
    void process66() {
        pushSymbol(66);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();


        //由于赋值表达式的左部必定是一个变量表达式节点，所以先创建一个节点


        TreeNode treeNode1 = new TreeNode();
        treeNode1.nodeKind = ExpK;
        treeNode1.boolName();
        treeNode1.name.add(getCurrentToken().getSem());

        treeNode1.name = new ArrayList<>();
        treeNode1.name.add(getCurrentToken().getSem());
        treeNode1.idNum++;

        //像这种只可以为标识符类型
        treeNode1.memberKind = AllName.memberKind.IdK;
        treeNode1.idNum++;
        //现在还无法判断此节点的ExpAttr的内容

        treeNode.boolChild();
        treeNode.child.add(treeNode1);

        //为了之后创建的东西
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;

        //进行压栈
        nodeStack.push(treeNode);
        expStack.push(treeNode1);
        tempVarNode = treeNode1;

    }

    //AssCall ::= AssignmentRest       ["DOT","ASSIGN","LMIDPAREN"]
    void process67() {
        pushSymbol(67);
        nodeStack.peek().memberKind = AllName.memberKind.AssignK;
    }

    //AssCall ::= CallStmRest          ["LPAREN"]
    void process68() {
        pushSymbol(68);
        nodeStack.peek().memberKind = AllName.memberKind.CallK;
    }

    //AssignmentRest ::= VariMore := Exp       ["DOT","ASSIGN","LMIDPAREN"]
    //VariMore交给了tempVar进行处理
    void process69() {
        pushSymbol(69);
    }

    //ConditionalStm ::= IF RelExp THEN StmList ELSE StmList FI    ["IF"]
    //只要确保RelExp弹出就可以了
    //这个地方也是需要先进行ExpK的相关赋值
    void process70() {
        pushSymbol(70);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.IfK;

        TreeNode treeNode1 = new TreeNode();
        treeNode1.nodeKind = ExpK;
        treeNode1.memberKind = AllName.memberKind.OpK;

        TreeNode treeNode2 = new TreeNode();
        treeNode2.nodeKind = StmtK;
        treeNode2.lineno = getCurrentToken().getLineShow();
        treeNode2.memberKind = AllName.memberKind.ThenK;

        TreeNode treeNode3 = new TreeNode();
        treeNode3.nodeKind = StmtK;
        treeNode3.lineno = getCurrentToken().getLineShow();
        treeNode3.memberKind = AllName.memberKind.ElseK;

        treeNode.boolChild();
        treeNode.child.add(treeNode1);
        treeNode.child.add(treeNode2);
        treeNode.child.add(treeNode3);

        //建立Stmt和Stml的关系 或者是Stmt和Stmt关系
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;

        nodeStack.push(treeNode);
        nodeStack.push(treeNode3);
        nodeStack.push(treeNode2);
        nodeStack.push(treeNode1);

    }

    //LoopStm ::= WHILE RelExp DO StmList ENDWH       ["WHILE"]
    //这个地方是需要的
    void process71() {
        pushSymbol(71);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.WhileK;

        TreeNode treeNode1 = new TreeNode();
        treeNode1.nodeKind = ExpK;
        treeNode1.memberKind = AllName.memberKind.OpK;


        TreeNode treeNode2 = new TreeNode();
        treeNode2.nodeKind = StmtK;
        treeNode2.lineno = getCurrentToken().getLineShow();
        treeNode2.memberKind = AllName.memberKind.DoK;

        treeNode.boolChild();
        treeNode.child.add(treeNode1);
        treeNode.child.add(treeNode2);

        //建立Stmt和Stml的关系 或者是Stmt和Stmt关系
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;

        nodeStack.push(treeNode);
        nodeStack.push(treeNode2);
        nodeStack.push(treeNode1);

    }


    //InputStm ::= READ ( Invar )      ["READ"]
    void process72() {
        pushSymbol(72);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.ReadK;

        //建立Stmt和Stml的关系 或者是Stmt和Stmt关系
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;

        nodeStack.push(treeNode);
    }

    //Invar ::= ID                     ["ID"]
    void process73() {
        pushSymbol(73);

        TreeNode peek = nodeStack.peek();
        peek.boolName();
        peek.name.add(getCurrentToken().getSem());
        peek.idNum++;
    }

    //Exp 注意这玩意
    //OutputStm ::= WRITE ( Exp )       ["WRITE"]
    void process74() {
        pushSymbol(74);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.WriteK;


        //建立Stmt和Stml的关系 或者是Stmt和Stmt关系
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;

        //压栈操作
        nodeStack.push(treeNode);
    }

    //ReturnStm ::= RETURN    ["RETURN"]
    void process75() {
        pushSymbol(75);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.ReturnK;

        //建立Stmt和Stml的关系 或者是Stmt和Stmt关系
        TreeNode peek = nodeStack.peek();
        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;
    }

    //CallStmRest ::= ( ActParamList )     ["LPAREN"]
    void process76() {
        pushSymbol(76);
    }

    //ActParamList ::= $                   ["RPAREN"]

    void process77() {
        pushSymbol(77);
    }

    //ActParamList ::= Exp ActParamMore    ["INTC","LPAREN","ID"]
    void process78() {
        pushSymbol(78);

    }

    //ActParamMore ::= $                   ["RPAREN"]
    //这里需要进行弹栈，似乎不需要进行双弹
    void process79() {
        pushSymbol(79);
    }

    //ActParamMore ::= , ActParamList      ["COMMA"]
    //这里需要进行弹栈
    //现在发现这里不需要进行弹栈操作了Exp处理完后会自动弹栈，不会我也会强制他会
    void process80() {
        pushSymbol(80);
    }


    //RelExp ::= Exp OtherRelE     ["INTC","LPAREN","ID"]
    //在这个地方的Exp就不进行nodeStack的压栈操作了
    void process81() {
        pushSymbol(81);
        getExpResult = AllName.LexType.NO;
    }

    //OtherRelE ::= CmpOp Exp      ["LT","EQ"]
    //typeStack 操作符栈
    //很简单的一个道理，现在的右边的那个Exp还没有进行处理
    //所以需要进行简单的压栈处理
    //我tm终于理解了
    //注意后面弹栈的时候去建立关系的时候，这种关系不需要
    void process82() {
        pushSymbol(82);
        String sem = getCurrentToken().getType().toString();
        int currentPri = getCurrentPri();
        int pri = getPri(sem);
        while (currentPri >= pri) {
            TreeNode p1 = expStack.pop();
            TreeNode p2 = expStack.pop();
            TreeNode pop = typeStack.pop();
            pop.boolChild();
            pop.child.add(p1);
            pop.child.add(p2);
            expStack.add(pop);
        }
        TreeNode t = nodeStack.pop();
        if (t.attr != null) {
            t.setAttr("exp");
        }
        //设置操作符的种类
        t.attr.expAttr.op = getCurrentToken().getType();
        typeStack.push(t);
        getExpResult = AllName.LexType.YES;
    }

    //Term ::= Factor OtherFactor      ["INTC","LPAREN","ID"]
    void process83() {
        pushSymbol(83);
    }

    //OtherFactor ::= $                ["COMMA","FI","SEMI","LT","RMIDPAREN","RPAREN","DO","EQ","MINUS","ELSE","END","THEN","ENDWH","PLUS"]
    void process84() {
        pushSymbol(84);
        if (expFlag != 0 && match("RPAREN")) {
            while (typeStack.peek().nodeKind != Special) {
                TreeNode p1 = expStack.pop();
                TreeNode p2 = expStack.pop();
                TreeNode pop = typeStack.pop();
                pop.boolChild();
                pop.child.add(p1);
                pop.child.add(p2);
                expStack.add(pop);
            }
            typeStack.pop();
            expFlag--;
        } else if (getExpResult1 == AllName.LexType.YES) {
            while (typeStack.peek().nodeKind != Special) {
                TreeNode p1 = expStack.pop();
                TreeNode p2 = expStack.pop();
                TreeNode pop = typeStack.pop();
                pop.boolChild();
                pop.child.add(p1);
                pop.child.add(p2);
                expStack.add(pop);
            }
            //去除不必要
            typeStack.pop();
            TreeNode pop = expStack.pop();
            TreeNode pop1 = nodeStack.pop();
            pop1.boolChild();
            pop1.child.add(pop);

            //为了之前的那个赋值表达式
            if (nodeStack.peek().memberKind == AllName.memberKind.IdK&&nodeStack.peek().nodeKind==ExpK){
                nodeStack.pop();
            }
        } else {
            while (!typeStack.isEmpty()) {
                TreeNode p1 = expStack.pop();
                TreeNode p2 = expStack.pop();
                TreeNode pop = typeStack.pop();
                pop.boolChild();
                pop.child.add(p1);
                pop.child.add(p2);
                expStack.add(pop);
            }
            if (getExpResult == AllName.LexType.YES) {
                expStack.pop();
                //不需要进行赋值
                getExpResult = AllName.LexType.NO;
            } else {
                //一般的表达式
                TreeNode pop = expStack.pop();
                TreeNode peek = nodeStack.peek();
                peek.boolChild();
                peek.child.add(pop);
            }
        }
    }

    //OtherTerm ::= AddOp Exp      ["PLUS","MINUS"]
    void process85() {
        pushSymbol(85);
        String sem = getCurrentToken().getType().toString();
        int currentPri = getCurrentPri();
        int pri = getPri(sem);
        while (currentPri >= pri) {
            TreeNode p1 = expStack.pop();
            TreeNode p2 = expStack.pop();
            TreeNode pop = typeStack.pop();
            pop.boolChild();
            pop.child.add(p1);
            pop.child.add(p2);
            expStack.add(pop);
        }
        TreeNode treeNode1 = new TreeNode();
        treeNode1.nodeKind = ExpK;
        treeNode1.memberKind = AllName.memberKind.OpK;
        if (treeNode1.attr != null) {
            treeNode1.setAttr("exp");
        }
        //设置操作符的种类
        treeNode1.attr.expAttr.op = getCurrentToken().getType();
        typeStack.push(treeNode1);
    }

    //Term ::= Factor OtherFactor      ["INTC","LPAREN","ID"]
    void process86() {
        pushSymbol(86);
    }

    //OtherFactor ::= $                ["COMMA","FI","SEMI","LT","RMIDPAREN","RPAREN","DO","EQ","MINUS","ELSE","END","THEN","ENDWH","PLUS"]
    void process87() {
        pushSymbol(87);
    }

    //OtherFactor ::= MultOp Term      ["OVER","TIMES"]
    void process88() {
        pushSymbol(88);
        String sem = getCurrentToken().getType().toString();
        int currentPri = getCurrentPri();
        int pri = getPri(sem);
        while (currentPri >= pri) {
            TreeNode p1 = expStack.pop();
            TreeNode p2 = expStack.pop();
            TreeNode pop = typeStack.pop();
            pop.boolChild();
            pop.child.add(p1);
            pop.child.add(p2);
            expStack.add(pop);
        }
        TreeNode treeNode1 = new TreeNode();
        treeNode1.nodeKind = ExpK;
        treeNode1.memberKind = AllName.memberKind.OpK;
        if (treeNode1.attr != null) {
            treeNode1.setAttr("exp");
        }
        //设置操作符的种类
        treeNode1.attr.expAttr.op = getCurrentToken().getType();
        typeStack.push(treeNode1);
    }

    //Factor ::= ( Exp )       ["LPAREN"]
    void process89() {
        pushSymbol(89);

        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = Special;
        typeStack.push(treeNode);
    }


    //Factor ::= INTC          ["INTC"]
    void process90() {
        pushSymbol(90);
        TreeNode treeNode = new TreeNode();
        treeNode.setAttr("exp");
        treeNode.nodeKind = ExpK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.ConstK;
        String sem = getCurrentToken().getSem();
        int val = 0;
        try {
            val = Integer.parseInt(sem);
        } catch (Exception e) {
            //错误处理
        }
        //表示此表达式值为Integer
        treeNode.attr.expAttr.type = AllName.LexType.Integer;
        treeNode.attr.expAttr.val = val;
        expStack.push(treeNode);
    }

    //Factor ::= Variable      ["ID"]
    void process91() {
        pushSymbol(91);
    }

    //Variable ::= ID VariMore        ["ID"]
    void process92() {
        pushSymbol(92);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = ExpK;
        treeNode.memberKind = AllName.memberKind.IdK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.setAttr("exp");

        //标识符相关的处理
        treeNode.boolName();
        String sem = getCurrentToken().getSem();
        treeNode.name.add(sem);
        treeNode.idNum++;

        expStack.push(treeNode);

    }

    //VariMore ::= $               ["COMMA","FI","TIMES","SEMI","LT","RMIDPAREN","RPAREN","DO","ASSIGN","EQ","MINUS","OVER","ELSE","END","THEN","ENDWH","PLUS"]
    void process93() {
        pushSymbol(93);
        TreeNode peek = expStack.peek();
        if (peek.attr != null) {
            peek.setAttr("exp");
        }
        peek.attr.expAttr.varKind = AllName.LexType.idV;
        //这里需要将其弹出来
        //todo 这里可能需要更加严格的条件
        if (nodeStack.peek().memberKind == AllName.memberKind.IdK&&nodeStack.peek().nodeKind==ExpK){
            nodeStack.pop();
        }
    }

    //VariMore ::= [ Exp ]         ["LMIDPAREN"]
    //感觉没有必有进行进一步的设计
    //注意peek是本来是没有压再nodeStack的
    void process94() {
        pushSymbol(94);
        TreeNode peek = expStack.peek();
        if (peek.attr != null) {
            peek.setAttr("exp");
        }
        peek.attr.expAttr.varKind = AllName.LexType.ArrayMembV;


        getExpResult1 = AllName.LexType.YES;
        //这里先将peek压下栈，为了进行树间连接
        nodeStack.push(peek);


        //可以去测试一下叠加中括号
        //再次借用一下special
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = Special;
        typeStack.push(treeNode);
    }

    //VariMore ::= . FieldVar      ["DOT"]
    void process95() {
        pushSymbol(95);
        TreeNode peek = expStack.peek();
        if (peek.attr != null) {
            peek.setAttr("exp");
        }
        peek.attr.expAttr.varKind = AllName.LexType.FieldMembV;

        //这里先将peek压下栈，为了进行树间连接
        nodeStack.push(peek);
    }


    //FieldVar ::= ID FieldVarMore    ["ID"]
    void process96() {
        pushSymbol(96);
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = ExpK;
        treeNode.boolName();
        treeNode.name.add(getCurrentToken().getSem());

        treeNode.name = new ArrayList<>();
        treeNode.name.add(getCurrentToken().getSem());
        treeNode.idNum++;
        TreeNode peek = nodeStack.peek();

        peek.boolChild();
        peek.child.add(treeNode);
        treeNode.father = peek;

        //方便进行后续的赋值
        nodeStack.push(treeNode);

    }


    //FieldVarMore ::= $           ["COMMA","FI","TIMES","SEMI","LT","RMIDPAREN","RPAREN","DO","ASSIGN","EQ","MINUS","OVER","ELSE","END","THEN","ENDWH","PLUS"]
    void process97() {
        pushSymbol(97);
        TreeNode pop = nodeStack.pop();
        pop.memberKind = AllName.memberKind.IdK;
        if (pop.attr != null) {
            pop.setAttr("exp");
        }
        pop.attr.expAttr.varKind = AllName.LexType.idV;

        //去除为了连接使用节点
        nodeStack.pop();

    }


    //FieldVarMore ::= [ Exp ]     ["LMIDPAREN"]
    void process98() {
        pushSymbol(98);

        TreeNode peek = nodeStack.pop();
        if (peek.attr != null) {
            peek.setAttr("exp");
        }
        peek.attr.expAttr.varKind = AllName.LexType.ArrayMembV;

        //去除为了连接使用节点
        nodeStack.pop();


        getExpResult1 = AllName.LexType.YES;
        //这里先将peek压下栈，为了进行树间连接
        nodeStack.push(peek);


        //可以去测试一下叠加中括号
        //再次借用一下special
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = Special;
        typeStack.push(treeNode);
    }


    //CmpOp ::= <               ["LT"]
    void process99() {
        pushSymbol(99);
    }

    //CmpOp ::= =               ["EQ"]
    void process100() {
        pushSymbol(100);
    }

    //AddOp ::= +               ["PLUS"]
    void process101() {
        pushSymbol(101);
    }

    //AddOp ::= -               ["MINUS"]
    void process102() {
        pushSymbol(102);
    }

    //MultOp ::= *              ["TIMES"]
    void process103() {
        pushSymbol(103);
    }

    //MultOp ::= /              ["OVER"]
    void process104() {
        pushSymbol(104);
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
