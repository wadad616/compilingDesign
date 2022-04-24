package grammer;

import Utils.AllName;
import lexicalAnalysis.LexToken;

import java.util.ArrayList;
import java.util.List;

import static Utils.AllName.NodeKind.*;

//每一个next 需不需要进行进一步的处理
//记住只在进行真实匹配的时候进行next
//记录类型节点输出的时候看一下父亲节点
//每当遇到一个next() 一个函数注意此函数是否需要去设置所在行
//exp处理的时候注意一下来源
//注意在varimore中设置一下exp的attr
//调用语句的儿子为Exp ，名字是一个exp 算了
//我觉得不能这样  调用语句有标识符，不同于一般赋值语句 左式的标识符也是表达式
//找一下警告中的used 判断一下程序中的错误
//type 和 var 处有点问题 可能没有type 和var！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！


public class DescentMethod {
    //表示的token序列
    List<LexToken> tokenList;
    //表示此时token序列读取的位置
    int tokenIndex;

    //标识临时变量
    String tempName;
    TreeNode root;

    public DescentMethod(List<LexToken> tokenList) {
        this.tokenList = tokenList;
        this.tokenIndex = 0;
    }

    public DescentMethod() {
    }

    TreeNode syntaxError() {
        return null;
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


    LexToken getCurrentToken() {
        return tokenList.get(tokenIndex);
    }

    /**
     * @param
     * @return grammer.TreeNode
     * @Description //TODO
     * @Date 2022/4/17 10:52
     **/

    public TreeNode program() {
        root = new TreeNode();
        root.nodeKind = AllName.NodeKind.ProK;
        root.child = new ArrayList<>();

        TreeNode t1 = programHead();
        if (t1 != null) {
            t1.father = root;
            root.child.add(t1);
        }
        //

        TreeNode t2 = declarePart();
        if (t2 != null) {
            t2.father = root;
            root.child.add(t2);
        }

        TreeNode t3 = programBody();
        if (t3 != null) {
            t3.father = root;
            root.child.add(t3);
        }

        //需要加上错误处理
        if (match("DOT")) {

        }
        return root;
    }

    /**
     * @return grammer.TreeNode
     * @Description 表达式ProgramHead ::= PROGRAM ProgramName 相关的处理
     * TODO
     * @Date 2022/4/17 11:22
     **/
    TreeNode programHead() {
        TreeNode treeNode = new TreeNode();
        treeNode.child = new ArrayList<>();
        treeNode.nodeKind = AllName.NodeKind.PheadK;
        if (match("PROGRAM")) {
            next();
            if (match("ID")) {
                treeNode.idNum = 1;
                treeNode.boolName();
                treeNode.name.add(getCurrentToken().getSem());
                treeNode.lineno = getCurrentToken().getLineShow();
                next();
            } else {
                //错误处理
            }
        } else {
            //错误处理
        }
        return treeNode;
    }


    /**
     * @return grammer.TreeNode
     * @Description // DeclarePart ::= TypeDec VarDec ProcDec
     * TODO 进行错误处理 测试
     * @Date 2022/4/17 14:36
     **/
    TreeNode declarePart() {
        TreeNode treeNode = new TreeNode();
        treeNode.boolChild();
        //总体声明
        treeNode.nodeKind = AllName.NodeKind.TotalDecK;

        //TypeK
        TreeNode t = new TreeNode();
        treeNode.boolChild();
        t.nodeKind = TypeK;
        //没有条件
        typeDec(t);
        //错误处理

        //varK
        TreeNode v = new TreeNode();
        treeNode.boolChild();
        v.nodeKind = AllName.NodeKind.VarK;
        //没有条件
        varDec(v);
        //错误处理


        treeNode.child.add(t);
        t.father = treeNode;
        treeNode.child.add(v);
        v.father = treeNode;

        procDec(treeNode);
        //综合错误处理

        return treeNode;
    }

    /**
     * @param t
     * @Description 完成下述表达式的匹配
     * TypeDec ::= $                   ["PROCEDURE","VAR","BEGIN"]
     * TypeDec ::= TypeDeclaration     ["TYPE"]
     * TODO 错误处理  测试
     * @Date 2022/4/17 14:36
     **/
    void typeDec(TreeNode t) {
        if (match(new String[]{"PROCEDURE", "VAR", "BEGIN"})) {
            //主要这个地方不可以进行next,只有匹配完成才可以进行next
            return;
        } else if (match("TYPE")) {
            typeDeclaration(t);
        }
        //错误处理，进行跳过还是怎么样
    }

    /**
     * @param t
     * @Description TypeDeclaration ::= TYPE TypeDecList  ["TYPE"]
     * TODO 错误处理 测试
     * @Date 2022/4/17 14:58
     **/
    void typeDeclaration(TreeNode t) {
        //只有匹配完成才可以进行match
        if (t.lineno == 0) {
            t.lineno = getCurrentToken().getLineShow();
        }
        next();
        //此处就可以进行匹配需不需要呢??????? ,
        //解决了.后面是先匹配后进入,这里也是先匹配后进行
        typeDecList(t);
        //相关错误处理
    }

    /**
     * @param t: 此参数传入的是TypeK节点
     * @return: void
     * @Description TypeDecList ::= TypeId = TypeName ; TypeDecMore  ["ID"]
     * TODO 错误处理 test
     * @Date 2022/4/17 15:18
     **/
    void typeDecList(TreeNode t) {
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = AllName.NodeKind.DecK;
        typeId(treeNode);
        //进行树中节点的连接

        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
        if (match("EQ")) {
            next();
        } else {
            //错误处理
        }
        //就相等于typeName
        typeName(treeNode);
        //错误处理
        if (match("SEMI")) {
            next();
        } else {
            //错误处理;
        }
        typeDecMore(t);
        //错误处理
    }

    /**
     * @param t: 此参数传入的是TypeK节点
     * @return: void
     * @Description 进行下述式子的计算
     * TypeDecMore ::= $                ["PROCEDURE","VAR","BEGIN"]
     * TypeDecMore ::= TypeDecList      ["ID"]
     * TODO
     * @Date 2022/4/17 15:25
     **/
    void typeDecMore(TreeNode t) {
        if (match(new String[]{"PROCEDURE", "VAR", "BEGIN"})) {
            return;
        } else if (match("ID")) {
            typeDecList(t);
        }
        //进行错误处理
    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为类型声明节点 在typeDecList()创建
     * @return: void
     * @Description TODO 错误处理 test
     * @Date 2022/4/17 15:35
     **/
    void typeId(TreeNode t) {
        //因为已经进行了匹配才可以进入这里,所以忽略了匹配
        if (t.lineno == 0)
            t.lineno = getCurrentToken().getLineShow();
        t.boolName();
        t.name.add(getCurrentToken().getSem());
        t.idNum++;
        next();
    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为类型声明节点 在typeDecList()创建 或者varDecList()创建
     * @return: void
     * @Description 进行下式的处理
     * TypeName ::= BaseType        ["CHAR","INTEGER"]
     * TypeName ::= StructureType   ["ARRAY","RECORD"]
     * TypeName ::= ID              ["ID"]
     * TODO 错误处理, test
     * @Date 2022/4/17 15:41
     **/
    void typeName(TreeNode t) {
        if (match(new String[]{"CHAR", "INTEGER"})) {
            baseType(t);
            //错误处理
        } else if (match(new String[]{"ARRAY", "RECORD"})) {
            structureType(t);
            //错误处理
        } else if (match("ID")) {
            t.memberKind = AllName.memberKind.IdK;
            //标识符相关的处理
            t.idNum++;
            t.boolName();
            t.name.add(getCurrentToken().getSem());
            //不知道有声明用
            if (t.typeName == null) {
                t.typeName = new ArrayList<>();
            }
            t.typeName.add(getCurrentToken().getSem());
            if (t.lineno == 0) {
                t.lineno = getCurrentToken().getLineShow();
            }
            next();
        }
        //进行错误处理
    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为数组类型声明节点 普通声明节点 记录内部变量节点
     *           在typeDecList()创建 或者在fieldDecList()中创建
     * @return: void
     * @Description 需要进行next
     * BaseType ::= INTEGER     ["INTEGER"]
     * BaseType ::= CHAR        ["CHAR"]
     * TODO Test
     * @Date 2022/4/17 16:10
     **/
    void baseType(TreeNode t) {
        //不需要错误处理
        if (match("INTEGER")) {
            t.memberKind = AllName.memberKind.IntegerK;
        } else if (match("CHAR")) {
            t.memberKind = AllName.memberKind.CharK;
        }
        if (t.lineno == 0) {
            t.lineno = getCurrentToken().getLineShow();
        }
        next();
    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为类型声明节点 在typeDecList()创建
     * @return: void
     * @Description 没有进行真正匹配无需next
     * StructureType ::= ArrayType      ["ARRAY"]
     * StructureType ::= RecType        ["RECORD"]
     * TODO 错误处理 test
     * @Date 2022/4/17 16:13
     **/
    void structureType(TreeNode t) {
        if (match("ARRAY")) {
            arrayType(t);
            //进行错误处理
        } else if (match("ARRAY")) {
            recType(t);
            //进行错误的处理
        }

    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为数组类型声明节点 在typeDecList()创建
     * @Description 进行了匹配需要进行next
     * ArrayType ::= ARRAY [ Low .. Top ] OF BaseType   ["ARRAY"]
     * Low ::= INTC     ["INTC"]
     * Top ::= INTC     ["INTC"]
     * TODO 错误处理 test
     * @Date 2022/4/17 16:16
     **/
    void arrayType(TreeNode t) {
        //数组相关信息初始化
        t.setAttr("array");
        if (t.lineno == 0)
            t.lineno = getCurrentToken().getLineShow();
        if (!match("ARRAY")) {
            //错误处理
        }
        next();
        if (!match("LMIDPAREN")) {
            //错误处理
        }
        next();
        if (!match("INTC")) {
            //错误处理
        } else {
            int i = 0;
            try {
                i = Integer.parseInt(getCurrentToken().getSem());
            } catch (Exception e) {
                //错误处理
            }
            t.attr.arrayAttr.low = i;
        }
        next();
        if (!match("UNDERANGE")) {
            //错误处理
        }
        next();
        if (!match("INTC")) {
            //错误处理
        } else {
            int i = 0;
            try {
                i = Integer.parseInt(getCurrentToken().getSem());
            } catch (Exception e) {
                //错误处理
            }
            t.attr.arrayAttr.up = i;
        }
        next();
        if (!match("RMIDPAREN")) {
            //错误处理
        }
        next();
        if (!match("OF")) {
            //错误处理
        }
        next();

        baseType(t);
        //错误处理

        //正常情况下进行相关的赋值处理
        t.attr.arrayAttr.childType = t.memberKind;
        t.memberKind = AllName.memberKind.ArrayK;

    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为记录类型声明节点 在typeDecList()创建
     * @Description 需要next()
     * RecType ::= RECORD FieldDecList END      ["RECORD"]
     * TODO 错误处理 test
     * @Date 2022/4/17 16:17
     **/
    void recType(TreeNode t) {
        t.memberKind = AllName.memberKind.RecordK;
        //匹配完后才可以进入这里
        next();
        fieldDecList(t);
        //进行错误处理

        if (!match("END")) {
            //错误处理
        }
        next();
    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为记录类型声明节点 在typeDecList()创建
     * @Description 不用next()
     * FieldDecList ::= BaseType IdList ; FieldDecMore      ["CHAR","INTEGER"]
     * FieldDecList ::= ArrayType IdList ; FieldDecMore     ["ARRAY"]
     * TODO 错误处理测试
     * @Date 2022/4/17 16:46
     **/
    void fieldDecList(TreeNode t) {
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = DecK;
        t.boolChild();
        if (match(new String[]{"CHAR", "INTEGER"})) {
            //进行此处生成的memberKine 中的定义
            baseType(treeNode);
            //错误处理
            idList(treeNode);
            //错误处理

        } else if (match("ARRAY")) {
            arrayType(treeNode);

        } else {
            //错误处理
        }
        t.child.add(treeNode);
        treeNode.father = t;
        if (!match("SEMI")) {
            //错误处理
        }
        next();
        fieldDecMore(t);
    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为记录类型声明节点 在typeDecList()创建
     * @Description 不需要next
     * FieldDecMore ::= $                   ["END"]
     * FieldDecMore ::= FieldDecList        ["ARRAY","CHAR","INTEGER"]
     * TODO
     * @Date 2022/4/17 18:58
     **/
    void fieldDecMore(TreeNode t) {
        if (match("END")) {
            return;
        } else if (match(new String[]{"ARRAY", "CHAR", "INTEGER"})) {
            fieldDecList(t);
            //错误处理
        }
    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为CHAR INTEGER ARRAY节点 fieldDecList()创建
     * @return: void
     * @Description 需要next
     * IdList ::= ID IdMore     ["ID"]
     * TODO 错误处理 test
     * @Date 2022/4/17 19:02
     **/
    void idList(TreeNode t) {
        if (match("ID")) {
            t.boolName();
            t.name.add(getCurrentToken().getSem());
            if (t.lineno == 0)
                t.lineno = getCurrentToken().getLineShow();
            t.idNum++;
            next();
            idMore(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的DecK节点 具体来说就为CHAR INTEGER ARRAY节点 fieldDecList()创建
     * @return: void
     * @Description 不需要next()
     * IdMore ::= $             [";"]
     * IdMore ::= , IdList      [","]
     * TODO 错误处理 test
     * @Date 2022/4/17 19:11
     **/
    void idMore(TreeNode t) {
        if (match("SEMI")) {
            return;
        } else if (match("COMMA")) {
            next();
            idList(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是VarK节点, 具体来说 无 ,在declarePart()处创建
     * @return: void
     * @Description 无需next
     * VarDec ::= $                 ["PROCEDURE","BEGIN"]
     * VarDec ::= VarDeclaration    ["VAR"]
     * TODO 错误处理 test
     * @Date 2022/4/17 20:40
     **/
    void varDec(TreeNode t) {
        if (match(new String[]{"PROCEDURE", "BEGIN"})) {
            return;
        } else if (match("VAR")) {
            varDeclaration(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是VarK节点, 具体来说 无 ,在declarePart()处创建
     * @Description 需要next , 仔细思考后觉得此处需要设置line
     * VarDeclaration ::= VAR VarDecList    ["VAR"]
     * TODO 错误处理 test
     * @Date 2022/4/17 20:52
     **/
    void varDeclaration(TreeNode t) {
        if (match("VAR")) {
            t.lineno = getCurrentToken().getLineShow();
            next();
            varDecList(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是VarK节点, 具体来说 无 ,在declarePart()处创建
     * @Description 需next, 不需要进行line的设置, 会在之前的函数中设置
     * VarDecList ::= TypeName VarIdList ; VarDecMore   ["ARRAY","RECORD","CHAR","ID","INTEGER"]
     * TODO 错误处理 test
     * @Date 2022/4/17 20:56
     **/
    void varDecList(TreeNode t) {
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = DecK;
        t.boolChild();
        treeNode.father = t;
        t.child.add(treeNode);
        //给t进行memberKind的赋值
        typeName(treeNode);
        //进行错误处理

        //进行节点标识符的设置
        varIdList(treeNode);
        //进行错误处理;
        if (!match("SEMI")) {
            //错误处理
        }
        next();
        //可能有多个变量声明
        varDecMore(t);
        //错误处理

    }

    /**
     * @param t: 此参数传入的是VarK节点, 具体来说 无 ,在declarePart()处创建
     * @Description 不需要next
     * VarDecMore ::= $             ["PROCEDURE","BEGIN"]
     * VarDecMore ::= VarDecList    ["ARRAY","RECORD","CHAR","ID","INTEGER"]
     * TODO 错误处理 test
     * @Date 2022/4/17 21:17
     **/
    void varDecMore(TreeNode t) {
        if (match(new String[]{"PROCEDURE", "BEGIN"})) {
            return;
        } else if (match(new String[]{"ARRAY", "RECORD", "CHAR", "ID", "INTEGER"})) {
            varDecList(t);
            //错误处理
            return;
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是DecK节点, 具体来说各种DecK的具体类型都可以 ,在varDecList()处创建
     * @Description 需要next 需要设置位置行号
     * VarIdList ::= ID VarIdMore       ["ID"]
     * TODO 错误处理 test
     * @Date 2022/4/17 21:20
     **/
    void varIdList(TreeNode t) {
        if (match("ID")) {
            t.boolName();
            t.name.add(getCurrentToken().getSem());
            t.idNum++;
            if (t.lineno == 0) {
                t.lineno = getCurrentToken().getLineShow();
            }
            next();
        } else {
            //错误处理
        }
        varIdMore(t);
        //错误处理
    }

    /**
     * @param t: 此参数传入的是DecK节点, 具体来说各种DecK的具体类型都可以 ,在varDecList()处创建
     * @Description 可能需要next 不需要line
     * VarIdMore ::= $              [";"]
     * VarIdMore ::= , VarIdList    [","]
     * TODO 错误处理 test
     * @Date 2022/4/17 21:30
     **/
    void varIdMore(TreeNode t) {
        if (match("SEMI")) {
            return;
        } else if (match("COMMA")) {
            next();
            varIdList(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是TotalDecK节点, 在declarePart()处创建
     * @Description 不需要next
     * ProcDec ::= $                ["BEGIN"]
     * ProcDec ::= ProcDeclaration  ["PROCEDURE"]
     * TODO 错误处理 test
     * @Date 2022/4/17 22:51
     **/
    void procDec(TreeNode t) {
        if (match("BEGIN")) {
            return;
        } else if (match("PROCEDURE")) {
            procDeclaration(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是TotalDecK节点, 在declarePart()处创建
     * @return: void
     * @Description 需要next 需要设置行
     * ProcDeclaration ::= PROCEDURE ProcName ( ParamList ) ; ProcDecPart ProcBody ProcDecMore   ["PROCEDURE"]
     * ProcName ::= ID                                                                           ["ID"]
     * TODO 错误处理 test
     * @Date 2022/4/17 23:00
     **/
    void procDeclaration(TreeNode t) {
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = ProcDecK;
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
        treeNode.lineno = getCurrentToken().getLineShow();
        if (!match("PROCEDURE")) {
            //错误处理
        }
        next();
        if (match("ID")) {
            treeNode.boolName();
            treeNode.name.add(getCurrentToken().getSem());
            treeNode.idNum++;
            next();
        } else {
            //错误处理
        }
        next();
        if (!match("LPAREN")) {
            //错误处理
        }
        next();
        //设置节点参数
        paramList(treeNode);
        //错误处理
        if (!match("RPAREN")) {
            //错误处理
        }
        next();
        //传参设置节点的父子关系
        procDecPart(treeNode);
        //传参设置父子关系
        procBody(treeNode);

        //多个过程
        ProcDecMore(t);
    }

    /**
     * @param t: 此参数传入的是TotalDecK节点, 在declarePart()处创建
     * @Description 不需要next
     * ProcDecMore ::= $                ["BEGIN"]
     * ProcDecMore ::= ProcDeclaration  ["PROCEDURE"]
     * TODO 错误处理 test
     * @Date 2022/4/17 23:25
     **/
    void ProcDecMore(TreeNode t) {
        if (match("BEGIN")) {
            return;
        } else if (match("PROCEDURE")) {
            procDeclaration(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是ProcDecK节点, 在procDeclaration()处创建
     * @return: void
     * @Description 不需要next
     * ParamList ::= $              ["RPAREN"]
     * ParamList ::= ParamDecList   ["ARRAY","VAR","RECORD","CHAR","ID","INTEGER"]
     * TODO 错误处理 test
     * @Date 2022/4/17 23:27
     **/
    void paramList(TreeNode t) {
        if (match("RPAREN")) {
            return;
        } else if (match(new String[]{"ARRAY", "VAR", "RECORD", "CHAR", "ID", "INTEGER"})) {
            paramDecList(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是ProcDecK节点, 在procDeclaration()处创建
     * @Description 不需要next
     * ParamDecList ::= Param ParamMore     ["ARRAY","VAR","RECORD","CHAR","ID","INTEGER"]
     * TODO 错误处理 test
     * @Date 2022/4/17 23:32
     **/
    void paramDecList(TreeNode t) {
        param(t);
        //错误处理
        paramMore(t);
        //错误处理
    }

    /**
     * @param t: 此参数传入的是ProcDecK节点, 在procDeclaration()处创建
     * @Description 可能需要next 不需要设置行
     * ParamMore ::= $                  ["RPAREN"]
     * ParamMore ::= ; ParamDecList     ["SEMI"]
     * TODO
     * @Date 2022/4/17 23:39
     **/
    void paramMore(TreeNode t) {
        if (match("RPAREN")) {
            return;
        } else if (match("SEMI")) {
            next();
            paramDecList(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是ProcDecK节点, 在procDeclaration()处创建
     * @Description 此处可能需要进行next 可能进行line的设置
     * Param ::= TypeName FormList          ["ARRAY","RECORD","CHAR","ID","INTEGER"]
     * Param ::= VAR TypeName FormList      ["VAR"]
     * TODO 错误处理 test
     * @Date 2022/4/17 23:58
     **/
    void param(TreeNode t) {
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = DecK;
        treeNode.father = t;
        t.boolChild();
        t.child.add(treeNode);
        if (match(new String[]{"ARRAY", "RECORD", "CHAR", "ID", "INTEGER"})) {
            if (treeNode.attr == null) {
                treeNode.setAttr("proc");
            }
            treeNode.attr.procAttr.paramType = AllName.LexType.ValParamType;

            typeName(treeNode);
            //错误处理

            formList(treeNode);
            //错误处理
        } else if (match("VAR")) {
            if (treeNode.lineno != 0)
                treeNode.lineno = getCurrentToken().getLineShow();
            if (treeNode.attr == null) {
                treeNode.setAttr("proc");
            }
            treeNode.attr.procAttr.paramType = AllName.LexType.VarParaType;
            next();
            typeName(treeNode);
            //错误处理

            formList(treeNode);
            //错误处理
        }
        //错误处理

    }

    /**
     * @param t: 此参数传入的是DecK节点,具体类型为各种具体声明类型, 在param()处创建
     * @Description 需要next ,需要设置line
     * FormList ::= ID FidMore      ["ID"]
     * TODO 错误处理 test
     * @Date 2022/4/18 0:18
     **/
    void formList(TreeNode t) {
        if (match("ID")) {
            if (t.lineno == 0) {
                t.lineno = getCurrentToken().getLineShow();
            }
            t.boolName();
            t.name.add(getCurrentToken().getSem());
            t.idNum++;
            next();
            fidMore(t);
            //进行错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是DecK节点,具体类型为各种具体声明类型, 在param()处创建
     * @Description 可能需要进行next
     * FidMore ::= $                ["SEMI","RPAREN"]
     * FidMore ::= , FormList       ["COMMA"]
     * TODO 错误检查 test
     * @Date 2022/4/18 0:19
     **/
    void fidMore(TreeNode t) {
        if (match(new String[]{"SEMI", "RPAREN"})) {
            return;
        } else if (match("COMMA")) {
            next();
            formList(t);
            //错误处理
        }
        //错误处理

    }

    /**
     * @param t: 此参数传入的是ProcDecK节点, 在procDeclaration()处创建
     * @Description 无需next
     * ProcDecPart ::= DeclarePart      ["PROCEDURE","VAR","BEGIN","TYPE"]
     * TODO 错误检查 test
     * @Date 2022/4/18 0:31
     **/
    void procDecPart(TreeNode t) {
        TreeNode treeNode = declarePart();
        //错误检查
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
    }

    /**
     * @param t: 此参数传入的是ProcDecK节点, 在procDeclaration()处创建
     * @Description 无需next
     * ProcBody ::= ProgramBody     ["BEGIN"]
     * TODO 错误检查 test
     * @Date 2022/4/18 0:34
     **/
    void procBody(TreeNode t) {
        TreeNode treeNode = programBody();
        //错误检查

        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
    }

    /**
     * @return: grammer.TreeNode
     * @Description 需要next 需要设置line
     * ProgramBody ::= BEGIN StmList END        ["BEGIN"]
     * TODO
     * @Date 2022/4/18 0:40
     **/

    TreeNode programBody() {
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmLK;
        if (treeNode.lineno != 0) {
            treeNode.lineno = getCurrentToken().getLineShow();
        }
        if (!match("BEGIN")) {
            //错误处理
        }
        next();
        stmList(treeNode);
        //错误处理

        if (!match("END")) {
            //错误处理
        }
        next();
        return treeNode;
    }

    /**
     * @param t: 此参数传入的是StmLK或者特殊StmtK节点, 在programBody()或者conditionalStm()或者while处创建
     * @Description 不需要next
     * StmList ::= Stm StmMore      ["READ","RETURN","WHILE","ID","IF","WRITE"]
     * TODO 错误处理 test
     * @Date 2022/4/18 0:44
     **/
    void stmList(TreeNode t) {
        stm(t);
        //错误处理
        stmMore(t);
        //错误处理
    }

    /**
     * @param t: 此参数传入的是StmLK或者特殊StmtK节点, 在programBody()或者conditionalStm()或者while处创建
     * @Description 可能需要next 不需要去设置line 需要注意的是如果有分号则标识有语句 没有分号表示的是结束
     * StmMore ::= $            ["FI","ELSE","END","ENDWH"]
     * StmMore ::= ; StmList    ["SEMI"]
     * TODO 错误处理 test
     * @Date 2022/4/18 0:49
     **/
    void stmMore(TreeNode t) {
        if (match(new String[]{"FI", "ELSE", "END", "ENDWH"})) {
            return;
        } else if (match("SEMI")) {
            next();
            stmList(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是StmLK或者特殊StmtK节点, 在programBody()或者conditionalStm()或者while处创建
     *           所以说值得注意点 在ID那里需要创建一个表达式节点
     * @Description 不需要next 暂且记录一下line ，之后可能会使用到
     * Stm ::= ConditionalStm   ["IF"]
     * Stm ::= LoopStm          ["WHILE"]
     * Stm ::= InputStm         ["READ"]
     * Stm ::= OutputStm        ["WRITE"]
     * Stm ::= ReturnStm        ["RETURN"]
     * Stm ::= ID AssCall       ["ID"]
     * TODO 错误处理 test
     * @Date 2022/4/18 0:56
     **/
    void stm(TreeNode t) {
        if (match("IF")) {
            conditionalStm(t);
            //错误处理
        } else if (match("WHILE")) {
            loopStm(t);
            //错误处理
        } else if (match("READ")) {
            returnStm(t);
            //错误处理
        } else if (match("WRITE")) {
            inputStm(t);
            //错误处理
        } else if (match("RETURN")) {
            outputStm(t);
            //错误处理
        } else if (match("ID")) {
            TreeNode treeNode = new TreeNode();
            treeNode.nodeKind = StmtK;
            treeNode.lineno = getCurrentToken().getLineShow();
//          treeNode.name=new ArrayList<>();
//          treeNode.name.add(getCurrentToken().getSem());
//          treeNode.idNum++;
            //为了之后创建的东西
            tempName = getCurrentToken().getSem();
            t.boolChild();
            t.child.add(treeNode);
            treeNode.father = t;
            next();
            //进行语句具体类型的设置
            assCall(treeNode);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是StmtK节点, 在stm()处创建
     * @Description 此处不需要进行next
     * AssCall ::= AssignmentRest       ["DOT","ASSIGN","LMIDPAREN"]
     * AssCall ::= CallStmRest          ["LPAREN"]
     * TODO 错误检查 test
     * @Date 2022/4/18 1:17
     **/
    void assCall(TreeNode t) {
        if (match(new String[]{"DOT", "ASSIGN", "LMIDPAREN"})) {
            assignmentRest(t);
            //错误处理
        } else if (match("LPAREN")) {
            callStmRest(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * 这里之所以需要这么做是为了统一
     * @param t: 此参数传入的是StmtK节点, 在stm()处创建
     * @Description 需要next  注意此处需要创建一个表达式节点配合使用,需要对t进行具体节点的赋值,利用t进行line的赋值
     * AssignmentRest ::= VariMore := Exp       ["DOT","ASSIGN","LMIDPAREN"]
     * TODO 错误检查 test
     * @Date 2022/4/18 9:22
     **/
    void assignmentRest(TreeNode t) {
        t.memberKind = AllName.memberKind.AssignK;
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = ExpK;
        treeNode.lineno = t.lineno;
        treeNode.boolName();
        treeNode.name.add(tempName);
        //像这种只可以为标识符类型
        treeNode.memberKind = AllName.memberKind.IdK;
        treeNode.idNum++;
        //当前无法判断ExpAttr的内容所以先不进行相关设置
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;

        if (!match(new String[]{"DOT", "ASSIGN", "LMIDPAREN"})) {
            //错误处理
        }

        //对于变量表达式相关的处理
        variMore(treeNode);
        //错误处理

        if (!match("ASSIGN")) {
            //错误处理
        }
        next();
        //为StmtK节点添加儿子节点
        exp(t);
        //错误处理
    }

    /**
     * @param t: 此参数传入的是StmLK或者特殊StmtK节点, 在programBody()或者conditionalStm()或者while处创建
     * @Description 需要next 需要line  不管这么多了，怎么简单怎么来
     * ConditionalStm ::= IF RelExp THEN StmList ELSE StmList FI    ["IF"]
     * TODO 错误检查 test
     *
     * @Date 2022/4/18 10:41
     **/
    void conditionalStm(TreeNode t) {
        if (!match("IF")) {
            //错误处理
        }
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.IfK;
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
        next();

        exp(treeNode);

        //THEN后语句
        if (!match("THEN")) {
            //错误处理
        }
        TreeNode treeNode1 = new TreeNode();
        treeNode1.nodeKind = StmtK;
        treeNode1.lineno = getCurrentToken().getLineShow();
        treeNode1.memberKind = AllName.memberKind.ThenK;
        treeNode.boolChild();
        treeNode.child.add(treeNode1);
        treeNode1.father = treeNode;
        stmList(treeNode1);
        //错误处理
        next();
        //ELSE后语句
        if (!match("ELSE")) {
            //错误处理
        }
        TreeNode treeNode2 = new TreeNode();
        treeNode2.nodeKind = StmtK;
        treeNode2.lineno = getCurrentToken().getLineShow();
        treeNode2.memberKind = AllName.memberKind.ElseK;
        treeNode.boolChild();
        treeNode.child.add(treeNode2);
        treeNode2.father = treeNode;
        stmList(treeNode2);
        //错误处理
        next();

        if (!match("FI")) {
            //错误处理
        }
        next();
    }

    /**
     * @param t: 此参数传入的是StmLK或者特殊StmtK节点, 在programBody()或者conditionalStm()或者loopStm()处创建
     * @Description 需要next 和设置line
     * LoopStm ::= WHILE RelExp DO StmList ENDWH       ["WHILE"]
     * TODO 错误检查 test
     * @Date 2022/4/18 11:21
     **/
    void loopStm(TreeNode t) {
        if (!match("WHILE")) {
            //错误处理
        }
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.WhileK;
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
        next();

        exp(treeNode);

        //THEN后语句
        if (!match("DoK")) {
            //错误处理
        }
        TreeNode treeNode1 = new TreeNode();
        treeNode1.nodeKind = StmtK;
        treeNode1.lineno = getCurrentToken().getLineShow();
        treeNode1.memberKind = AllName.memberKind.DoK;
        treeNode.boolChild();
        treeNode.child.add(treeNode1);
        treeNode1.father = treeNode;
        stmList(treeNode1);
        //错误处理
        next();

        if (!match("ENDWH")) {
            //错误处理
        }
        next();
    }

    /**
     * @param t: 此参数传入的是StmLK或者特殊StmtK节点, 在programBody()或者conditionalStm()或者loopStm()处创建
     * @Description 需要next 和设置line
     * InputStm ::= READ ( Invar )      ["READ"]
     * Invar ::= ID                     ["ID"]
     * TODO 错误检查 test
     * @Date 2022/4/18 11:29
     **/
    void inputStm(TreeNode t) {
        if (!match("READ")) {
            //错误处理
        }
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.ReadK;
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
        next();
        if (!match("LPAREN")) {
            //错误处理
        }
        next();
        if (!match("ID")) {
            //错误处理
        }
        treeNode.boolName();
        treeNode.name.add(getCurrentToken().getSem());
        treeNode.idNum++;
        next();
        if (!match("RPAREN")) {
            //错误处理
        }
    }

    /**
     * @param t: 此参数传入的是StmLK或者特殊StmtK节点, 在programBody()或者conditionalStm()或者loopStm()处创建
     * @Description 需要next 和设置line
     * OutputStm ::= WRITE ( Exp )       ["WRITE"]
     * TODO 错误检查 test
     * @Date 2022/4/18 11:37
     **/
    void outputStm(TreeNode t) {
        if (!match("WRITE")) {
            //错误处理
        }
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.WriteK;
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
        next();
        if (!match("LPAREN")) {
            //错误处理
        }
        next();

        exp(t);
        //错误处理
        treeNode.name.add(getCurrentToken().getSem());
        treeNode.idNum++;
        next();
        if (!match("RPAREN")) {
            //错误处理
        }
    }

    /**
     * @param t: 此参数传入的是StmLK或者特殊StmtK节点, 在programBody()或者conditionalStm()或者loopStm()处创建
     * @Description 需要next 和设置line
     * 需要注意此处进行了适当的修改， 因为通过前面的语法可以得到没有返回值这一回事
     * ReturnStm ::= RETURN    ["RETURN"]
     * TODO 错误检查 test
     * @Date 2022/4/18 12:29
     **/
    void returnStm(TreeNode t) {
        if (!match("RETURN")) {
            //错误处理
        }
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = StmtK;
        treeNode.lineno = getCurrentToken().getLineShow();
        treeNode.memberKind = AllName.memberKind.ReturnK;
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;
        //返回值
        next();
    }

    /**
     * @param t: 此参数传入的是StmtK节点, 在stm()处创建
     * @Description 需要next 需要line 注意调用语句的表达式为它各个实参
     * CallStmRest ::= ( ActParamList )     ["LPAREN"]
     * TODO
     * @Date 2022/4/18 13:12
     **/
    void callStmRest(TreeNode t) {
        if (!match("LPAREN")) {
            //错误处理
        }
        //思考了一下应该没有问题
        t.memberKind = AllName.memberKind.CallK;
        TreeNode treeNode = new TreeNode();
        treeNode.nodeKind = ExpK;
        treeNode.lineno = t.lineno;
        treeNode.boolName();
        treeNode.name.add(tempName);
        //像这种只可以为标识符类型
        treeNode.memberKind = AllName.memberKind.IdK;
        treeNode.idNum++;
        //当前无法判断ExpAttr的内容所以先不进行相关设置
        t.boolChild();
        t.child.add(treeNode);
        treeNode.father = t;

        next();
        actParamList(t);
        //错误处理
        if (!match("RPAREN")) {
            //错误处理
        }
    }

    /**
     * @param t: 此参数传入的是StmtK节点, 在stm()处创建
     * @Description 不需要进行next
     * ActParamList ::= $                   ["RPAREN"]
     * ActParamList ::= Exp ActParamMore    ["INTC","LPAREN","ID"]
     * TODO
     * @Date 2022/4/18 13:37
     **/
    void actParamList(TreeNode t) {
        if (match("RPAREN")) {
            return;
        } else if (match(new String[]{"INTC", "LPAREN", "ID"})) {
            exp(t);
            actParamMore(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是StmtK节点, 在stm()处创建
     * @Description 需要next 不需要设置行
     * ActParamMore ::= $                   ["RPAREN"]
     * ActParamMore ::= , ActParamList      ["COMMA"]
     * TODO
     * @Date 2022/4/18 13:43
     **/
    void actParamMore(TreeNode t) {
        if (match(new String[]{"RPAREN"})) {
            return;
        } else if (match("COMMA")) {
            next();
            actParamList(t);
            //错误处理
        }
        //错误处理
    }

    /**
     * @param t: 此参数传入的是StmtK节点, 在stm()处创建
     * @Description 仔细思考觉得没有问题，相当于给表达式限定条件 需要next 需要line
     * 这个函数有多种作用 ！！！！
     * RelExp ::= Exp OtherRelE     ["INTC","LPAREN","ID"]
     * OtherRelE ::= CmpOp Exp      ["LT","EQ"]
     * TODO 错误检查 test
     * @Date 2022/4/18 15:58
     **/
    TreeNode exp(TreeNode t) {
        TreeNode ret;
        if (!match(new String[]{"INTC", "LPAREN", "ID"})) {
            //错误处理
        }

        //1.先默认只有一个expression，先假设一个exp只可以生成一个节点，只不过这个节点为套接的
        //2.这么理解只有赋值才是有多个表达式的
        //3.得到bool表达式的左式
        TreeNode t1 = simpleExp();
        //错误处理
        ret = t1;

        if (match(new String[]{"LT", "EQ"})) {
            TreeNode treeNode = new TreeNode();
            treeNode.nodeKind = ExpK;
            treeNode.memberKind = AllName.memberKind.OpK;
            treeNode.lineno = getCurrentToken().getLineShow();
            treeNode.setAttr("exp");
            //赋予OP恰当的值
            treeNode.attr.expAttr.op = getCurrentToken().getType();
            //表示此表达式值为bool类型
            treeNode.attr.expAttr.type = AllName.LexType.Boolean;
            //建立父子树关系

            //只有最开始的表达式需要这样
            if (t != null) {
                treeNode.father = t;
                t.boolChild();
                t.child.add(treeNode);
            }
            //建立父子树关系
            treeNode.boolChild();
            treeNode.child.add(t1);
            t1.father = treeNode;

            next();

            //得到bool表达式的右式
            if (!match(new String[]{"INTC", "LPAREN", "ID"})) {
                //错误处理
            }
            TreeNode t2 = simpleExp();
            //错误处理

            treeNode.child.add(t2);
            t2.father = treeNode;

            ret = treeNode;
        }
        return ret;
    }

    /**
     * @return: grammer.TreeNode 放回值在exp()使用
     * @Description 需要next 需要line
     * Exp ::= Term OtherTerm       ["INTC","LPAREN","ID"]
     * OtherTerm ::= $              ["COMMA","FI","SEMI","LT","RMIDPAREN","ELSE","END","THEN","RPAREN","DO","ENDWH","EQ"]
     * OtherTerm ::= AddOp Exp      ["PLUS","MINUS"]
     * TODO 错误检查 测试
     * @Date 2022/4/18 16:25
     **/
    TreeNode simpleExp() {
        TreeNode ret;
        TreeNode left;
        if (!match(new String[]{"INTC", "LPAREN", "ID"})) {
            //错误处理
        }
        TreeNode t1 = term();
        //错误处理
        ret = t1;

        left = t1;
        //操 突然发现这个地方需要写一个循环
        while (match(new String[]{"PLUS", "MINUS"})) {
            TreeNode treeNode = new TreeNode();
            treeNode.nodeKind = ExpK;
            treeNode.memberKind = AllName.memberKind.OpK;
            treeNode.lineno = getCurrentToken().getLineShow();
            treeNode.setAttr("exp");
            //赋予OP恰当的值
            treeNode.attr.expAttr.op = getCurrentToken().getType();
            //表示此表达式值为bool类型
            treeNode.attr.expAttr.type = AllName.LexType.Integer;

            //建立父子树关系
            treeNode.boolChild();
            treeNode.child.add(left);
            left.father = treeNode;

            next();

            //得到bool表达式的右式
            if (!match(new String[]{"INTC", "LPAREN", "ID"})) {
                //错误处理
            }
            TreeNode t2 = term();
            //错误处理

            treeNode.child.add(t2);
            t2.father = treeNode;
            ret = treeNode;
            left = treeNode;
        }
        return ret;
    }

    /**
     * @return: grammer.TreeNode 返回值在simpleExp()使用
     * @Description 需要next 需要line
     * Term ::= Factor OtherFactor      ["INTC","LPAREN","ID"]
     * OtherFactor ::= $                ["COMMA","FI","SEMI","LT","RMIDPAREN","RPAREN","DO","EQ","MINUS","ELSE","END","THEN","ENDWH","PLUS"]
     * OtherFactor ::= MultOp Term      ["OVER","TIMES"]
     * TODO
     * @Date 2022/4/18 16:36
     **/
    TreeNode term() {
        TreeNode ret;
        TreeNode left;
        if (!match(new String[]{"INTC", "LPAREN", "ID"})) {
            //错误处理
        }
        TreeNode t1 = factor();
        //错误处理
        ret = t1;

        left = t1;

        while (match(new String[]{"OVER", "TIMES"})) {
            TreeNode treeNode = new TreeNode();
            treeNode.nodeKind = ExpK;
            treeNode.memberKind = AllName.memberKind.OpK;
            treeNode.lineno = getCurrentToken().getLineShow();
            treeNode.setAttr("exp");
            //赋予OP恰当的值
            treeNode.attr.expAttr.op = getCurrentToken().getType();
            //表示此表达式值为Integer
            treeNode.attr.expAttr.type = AllName.LexType.Integer;

            //建立父子树关系
            treeNode.boolChild();
            treeNode.child.add(left);
            left.father = treeNode;

            next();

            //得到bool表达式的右式
            if (!match(new String[]{"INTC", "LPAREN", "ID"})) {
                //错误处理
            }
            TreeNode t2 = factor();
            //错误处理

            treeNode.child.add(t2);
            t2.father = treeNode;
            ret = treeNode;
            left = treeNode;
        }
        return ret;
    }

    /**
     * @return: grammer.TreeNode 在term()使用
     * @Description 需要next 需要行
     * Factor ::= ( Exp )       ["LPAREN"]
     * Factor ::= INTC          ["INTC"]
     * Factor ::= Variable      ["ID"]
     * TODO 错误检查 test
     * @Date 2022/4/18 17:55
     **/
    TreeNode factor() {
        TreeNode ret = null;
        if (match("LPAREN")) {
            next();
            ret = exp(null);
            //错误处理
            if (!match("RPAREN")) {
                //错误处理
            }
            next();
        } else if (match("INTC")) {
            TreeNode treeNode = new TreeNode();
            treeNode.nodeKind = ExpK;
            treeNode.memberKind = AllName.memberKind.ConstK;
            treeNode.lineno = getCurrentToken().getLineShow();
            treeNode.setAttr("exp");

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
            ret = treeNode;
            next();
        } else if (match("ID")) {
            ret = variable();
            //错误处理
        }
        return ret;
    }

    /**
     * @return: grammer.TreeNode  在factor()处使用
     * @Description 需要next 需要行 创建新的节点
     * Variable ::= ID VariMore        ["ID"]
     * TODO 错误检查 test
     * @Date 2022/4/18 19:04
     **/
    TreeNode variable() {
        if (!match("ID")) {
            //错误处理
        }
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

        next();
        variMore(treeNode);
        //错误处理
        return treeNode;
    }

    /**
     * @param t: 表达式类型ExpK  具体类型IdK  来自variable() assignmentRest()
     * @Description 仔细思考一下应该所有的 ID 都落在这里了
     * 可能需要next 不需要行
     * VariMore ::= $               ["COMMA","FI","TIMES","SEMI","LT","RMIDPAREN","RPAREN","DO","ASSIGN","EQ","MINUS","OVER","ELSE","END","THEN","ENDWH","PLUS"]
     * VariMore ::= [ Exp ]         ["LMIDPAREN"]
     * VariMore ::= . FieldVar      ["DOT"]
     * TODO 错误检查 test
     * @Date 2022/4/18 19:23
     **/
    void variMore(TreeNode t) {
        //注意在这种情况下就不知道类型了,需要在语义分析的时候才可以进一步的确定
        if (match(new String[]{"COMMA", "FI", "TIMES", "SEMI", "LT", "RMIDPAREN", "RPAREN", "DO", "ASSIGN", "EQ", "MINUS", "OVER", "ELSE", "END", "THEN", "ENDWH", "PLUS"})) {
            if (t.attr != null) {
                t.setAttr("exp");
            }
            t.attr.expAttr.varKind = AllName.LexType.idV;
            return;
        } else if (match("LMIDPAREN")) {
            next();
            //特殊用法
            //注意这种情况可以确定t的
            if (t.attr != null) {
                t.setAttr("exp");
            }
            t.attr.expAttr.varKind = AllName.LexType.ArrayMembV;
            TreeNode treeNode = exp(null);
            //错误处理

            t.boolChild();
            //注意数组Id表达式的第一儿子就是下标
            t.child.add(treeNode);
            treeNode.father = t;
            if (!match("RMIDPAREN")) {
                //错误处理
            }
        } else if (match("DOT")) {
            next();
            if (t.attr != null) {
                t.setAttr("exp");
            }
            t.attr.expAttr.varKind = AllName.LexType.FieldMembV;
            TreeNode treeNode = fieldVar();
            //错误处理

            t.boolChild();
            //注意数组Id表达式的第一儿子就是下标
            t.child.add(treeNode);
            treeNode.father = t;
        }
        //错误处理
        System.out.println("错误");
    }

    /**
     * @return: grammer.TreeNode 在variMore()处使用
     * @Description 需要next 需要line
     * FieldVar ::= ID FieldVarMore    ["ID"]
     * TODO 错误检查 test
     * @Date 2022/4/18 19:52
     **/
    TreeNode fieldVar() {
        if (!match("ID")) {
            //错误处理
        }
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

        next();
        fieldVarMore(treeNode);
        //错误处理
        return treeNode;
    }

    /**
     * @param t: 来自fieldVar()
     * @Description 需要next 需要line
     * FieldVarMore ::= $           ["COMMA","FI","TIMES","SEMI","LT","RMIDPAREN","RPAREN","DO","ASSIGN","EQ","MINUS","OVER","ELSE","END","THEN","ENDWH","PLUS"]
     * FieldVarMore ::= [ Exp ]     ["LMIDPAREN"]
     * TODO 错误检查 test
     * @Date 2022/4/18 20:02
     **/
    void fieldVarMore(TreeNode t) {
        if (match(new String[]{"COMMA", "FI", "TIMES", "SEMI", "LT", "RMIDPAREN", "RPAREN", "DO", "ASSIGN", "EQ", "MINUS", "OVER", "ELSE", "END", "THEN", "ENDWH", "PLUS"})) {
            if (t.attr != null) {
                t.setAttr("exp");
            }
            t.attr.expAttr.varKind = AllName.LexType.idV;
            return;
        } else if (match("LMIDPAREN")) {
            next();
            //特殊用法
            //注意这种情况可以确定t的
            if (t.attr != null) {
                t.setAttr("exp");
            }
            t.attr.expAttr.varKind = AllName.LexType.ArrayMembV;
            TreeNode treeNode = exp(null);
            //错误处理

            t.boolChild();
            //注意数组Id表达式的第一儿子就是下标
            t.child.add(treeNode);
            treeNode.father = t;
            if (!match("RMIDPAREN")) {
                //错误处理
            }
        }
    }

    TreeNode parse() {
        return null;
    }


}
