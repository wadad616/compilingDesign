package Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * @Description //现在已经做完分离VN,VT 将表达式与内部进行映射
 * <p>
 * TODO 获得First集
 * todo 最后搞个list方便进行处理,就先不管什么内存问题了
 * TODO Fellow集合
 * TODO Select集合
 * @Date 2022/4/15 21:43
 **/

public class SelectedProcess {
    public Set<String> VT;
    public Set<String> VN;

    //方便之后的遍历，主要为了避免使用VT遍历顺序无法确定带来的调试问题
    public List<String> VNList;//非终结符
    public List<Expression> expressionList;//表达式
    //为了方便找到表达式
    public Map<String, List<Expression>> expressionMap;

    //所有单一符号的First集
    public Map<String, Set<String>> singleFirst;

    //表达式的First集
    public Map<Expression, Set<String>> expressionFirst;

    //表达式的follow集,对应所有的VN
    public Map<String, Set<String>> followSet;

    //表示的predict集，每一个表达式有一个predict集合
    public Map<Expression, Set<String>> predictSet;


    public SelectedProcess(Set<String> VT, Set<String> VN) {
        this.VT = VT;
        this.VN = VN;
    }

    public SelectedProcess(String path) {
        //读取文件
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //初始化
        VN = new HashSet<>();
        VT = new HashSet<>();
        expressionList = new ArrayList<>();
        expressionMap = new HashMap<>();
        singleFirst = new HashMap<>();
        followSet = new HashMap<>();
        VNList = new ArrayList<>();
        expressionFirst = new HashMap<>();
        predictSet = new HashMap<>();

        //向文件中读取数据
        List<String> list = new ArrayList<>();

        //提取所有的非终结符和终结符
        List<String[]> lists = new ArrayList<>();
        while (scanner.hasNextLine()) {
            list.add(scanner.nextLine());
        }
        for (String s : list) {
            String[] split = s.split(" ");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
            }
            lists.add(split);
        }

        //提取所有的非终结符
        for (String[] s : lists) {
            if (!VN.contains(s[0])) {
                //获得顺序的非终结符序列
                VNList.add(s[0]);
            }
            VN.add(s[0]);
            singleFirst.put(s[0], new HashSet<>());
            //非终结符初始化
            followSet.put(s[0], new HashSet<>());
        }
        //提取所有的终结符
        for (String[] s : lists) {
            for (int i = 2; i < s.length; i++) {
                if (!VN.contains(s[i])) {
                    VT.add(s[i]);
                    Set<String> set = new HashSet<>();
                    set.add(s[i]);
                    //傻逼问题
                    singleFirst.put(s[i], set);
                }
            }
        }

        //将各种表达式转化对应expression对象,同时将expressionMap进行初始化
        for (String[] s : lists) {
            Expression expression = new Expression();
            expression.leftExp = s[0];
            List<String> listString = new ArrayList<>();
            for (int i = 2; i < s.length; i++) {
                listString.add(s[i]);
            }
            expression.rightExp = listString;
            //表达式List的初始化
            expressionList.add(expression);
            //表达式First集的初始化放到后面，直接求的时候，边求边初始化

            //表达式Predict集的初始化放到后面，边求边初始化

            //并且对Value进行操作，在Value也集合或者Value是复杂类的时候特别好用
            if (expressionMap.containsKey(s[0])) {
                expressionMap.get(s[0]).add(expression);
            } else {
                List<Expression> list1 = new ArrayList<>();
                list1.add(expression);
                expressionMap.put(s[0], list1);
            }
        }
    }

    public SelectedProcess() {
        String[] s = new String[]{
                "TypeDecMore", "TypeId", "OtherFactor",
                "Program", "ProcDecMore", "TypeDecList",
                "OtherRelE", "Term", "ProgramName",
                "VarDeclaration", "FormList", "Top",
                "Low", "VarDec", "ProcDecPart",
                "ProgramBody", "StmMore", "FieldDecList",
                "VarDecList", "ActParamList", "Variable",
                "ProgramHead", "CmpOp", "ProcName",
                "InputStm", "RelExp", "VarIdList",
                "IdList", "ParamList", "AssignmentRest",
                "ParamDecList", "ProcBody", "MultOp",
                "LoopStm", "VariMore", "ProcDec",
                "OutputStm", "StmList", "ConditionalStm",
                "DeclarePart", "BaseType", "StructureType",
                "Param", "RecType", "AssCall",
                "FieldDecMore", "VarIdMore", "Invar",
                "ProcDeclaration", "Exp", "TypeDeclaration"};
        String[] s1 = new String[]{
                "..", "ARRAY", ":="
                , "FI", "VAR", "RECORD",
                "CHAR", "BEGIN", "DO",
                "WRITE", "RETURN", "OF",
                "ELSE", "ID", "[",
                "TYPE", "]", "IF",
                "INTEGER", "PROCEDURE", "$",
                "(", ")", "*",
                "+", ",", "-",
                ".", "/", "READ",
                "INTC", "END", "THEN",
                "WHILE", ";", "ENDWH",
                "<", "=", "PROGRAM",
        };
        List<String> VNList = Arrays.asList(s);
        List<String> VTList = Arrays.asList(s1);
        VN.addAll(VNList);
        VT.addAll(VTList);
    }

    /**
     * @param VNString
     * @Description //判断是否可以推导出空串
     * TODO TEST
     * @Date 2022/4/16 8:30
     **/
    boolean judHaveNull(String VNString) {
        if (singleFirst.get(VNString).contains("$")) {
            return true;
        }
        List<Expression> expressions = expressionMap.get(VNString);
        for (Expression e : expressions) {
            List<String> rightExp = e.rightExp;
            boolean jud1 = true;
            for (String s : rightExp) {
                if (VT.contains(s)) {
                    if (!s.equals("$")) {
                        jud1 = false;
                        break;
                    }
                } else if (judHaveNull(s)) {
                    continue;
                } else {
                    jud1 = false;
                    break;
                }
            }
            if (jud1) {
                singleFirst.get(VNString).add("$");
                return true;
            }
        }
        return false;
    }


    /**
     * @return boolean
     * @Description 获得非终结符的First集合
     * TODO Test
     * @Date 2022/4/16 9:20
     **/
    boolean getAllSingleFirst(String VNString) {
        boolean jud = false;
        List<Expression> expressions = expressionMap.get(VNString);
        Set<String> set = singleFirst.get(VNString);
        //这里出了点小问题
        for (Expression e : expressions) {
            List<String> rightExp = e.rightExp;
            for (String s : rightExp) {
                if (VT.contains(s)) {
                    if (!set.contains(s)) {
                        jud = true;
                        set.add(s);
                    }
                    break;
                } else {
                    //？递归是否有出口,似乎是有出口的
                    jud = getAllSingleFirst(s) || jud;
                    Set<String> set1 = singleFirst.get(s);
                    //将两个集合合并
                    for (String s1 : set1) {
                        if (!set.contains(s1)&&!s1.equals("$")) {
                            jud = true;
                            set.add(s1);
                        }
                    }
                    if (!set1.contains("$")) {
                        break;
                    }
                }
            }
        }
        return jud;
    }

    /**
     * @Description //求表达式的first集合
     * @Date 2022/4/16 11:38
     **/
    void getExpressionFirst() {
        for (Expression e : expressionList) {
            Set<String> set = new HashSet<>();
            for (String s : e.rightExp) {
                set.addAll(singleFirst.get(s));
                if (s.equals("$")) {
                    break;
                } else if (VT.contains(s) || !singleFirst.get(s).contains("$")) {
                    set.remove("$");
                    break;
                }
            }
            expressionFirst.put(e, set);
        }
    }

    /**
     * @param list
     * @param index
     * @return java.util.Set<java.lang.String>
     * @Description // 获得指定序列的First集合
     * TODO test
     * @Date 2022/4/16 12:05
     **/

    Set<String> getGivenFirst(List<String> list, int index) {
        Set<String> set = new HashSet<>();
        if(list.size()==index){
            set.add("$");
            return set;
        }
        if(list.get(index).equals("$")){
            set.add("$");
            return set;
        }
        for (int i = index; i < list.size(); i++) {
            String s = list.get(i);
            set.addAll(singleFirst.get(s));
            if (VT.contains(s) || !singleFirst.get(s).contains("$")) {
                set.remove("$");
                break;
            }
        }
        return set;
    }

    /**
     * @Description //获得所有的follow集
     * TODO test 存在问题，在文件的最后需要处理
     * @Date 2022/4/16 11:39
     **/
    void getFollow() {
        boolean jud = true;
        while (jud) {
            jud = false;
            for (Expression e : expressionList) {
                List<String> rightExp = e.rightExp;
                for (int i = 0; i < rightExp.size(); i++) {
                    String s = rightExp.get(i);
                    if (!VT.contains(s)) {
                        Set<String> nowFollow = followSet.get(s);
                        int size = nowFollow.size();
                        Set<String> set = getGivenFirst(rightExp, i + 1);
                        if (set.contains("$")) {
                            nowFollow.addAll(set);
                            nowFollow.addAll(followSet.get(e.leftExp));
                            nowFollow.remove("$");
                        } else {
                            nowFollow.addAll(set);
                        }
                        if (nowFollow.size() > size) {
                            jud = true;
                        }
                    }
                }
            }
        }
    }

    void getPredict() {
        for (Expression e : expressionList) {
            Set<String> set = new HashSet<>();
            Set<String> givenFirst = getGivenFirst(e.rightExp, 0);
            if (givenFirst.contains("$")) {
                set.addAll(expressionFirst.get(e));
                set.addAll(followSet.get(e.leftExp));
                set.remove("$");
            } else {
                set.addAll(expressionFirst.get(e));
            }
            predictSet.put(e, set);
        }
    }
    /**
     * @Description 初始化First集
     * 测试完成应该没有问题
     * @Date 2022/4/16 8:47
     **/
    public void traverseVN() {
        //此处进行了修改可能有问题???
        for (String s : VNList) {
            judHaveNull(s);
        }
        boolean jud = true;
        while (jud) {
            jud = false;
            for (String s : VNList) {
                jud = getAllSingleFirst(s) || jud;
            }
        }
        //获得表达式First集合
        getExpressionFirst();
        //获取fellow集
        getFollow();
        //获得predict集合
        getPredict();
    }


}

