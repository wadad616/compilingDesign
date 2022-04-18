package grammer;

import Utils.AllName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNode {
    //为子语法树的节点
    List<TreeNode> child;
    //指向兄弟节点
    TreeNode sibling;
    //记录父亲节点
    TreeNode father;
    //记录在源程序的行号
    int lineno;
    //语法树节点类型
    AllName.NodeKind nodeKind;
    //成员节点类型
    AllName.memberKind memberKind;
    //节点个数
    int idNum = 0;
    //节点中各个标识符的名字
    List<String> name;
    //暂定
    List<Integer> table;
    //暂时不知道有什么用，该怎么去使用
    List<String> typeName;
    //记录语法树节点的其他属性
    Attr attr;

    public Attr getAttr() {
        return attr;
    }

    public void setAttr(String type) {
        new Attr(type);
    }

    public class Attr {
        public ArrayAttr arrayAttr;
        public procAttr procAttr;
        public ExpAttr expAttr;

        public Attr() {
        }

        public Attr(String type) {
            if (type.equals("array")) {
                arrayAttr = new ArrayAttr();
            } else if (type.equals("proc")) {
                procAttr = new procAttr();
            } else if (type.equals("exp")) {
                expAttr = new ExpAttr();
            }
        }
    }

    public void boolChild() {
        if (this.child == null) {
            this.child = new ArrayList<>();
        }
    }
    public void boolName(){
        if(this.name==null){
            this.name=new ArrayList<>();
        }
    }

    public class ArrayAttr {
        public int low;
        public int up;
        //注意东西是用于声明阶段的,所以应该使用声明阶段的枚举类型
        public AllName.memberKind childType;
    }

    public class procAttr {
        public AllName.LexType paramType;
    }

    public class ExpAttr {
        public AllName.LexType op;
        public int val;
        public AllName.LexType varKind;
        public AllName.LexType type;
    }

}
