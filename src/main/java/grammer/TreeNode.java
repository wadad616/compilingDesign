package grammer;

import Utils.AllName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNode {
    //为子语法树的节点
    public List<TreeNode> child;
    //指向兄弟节点
    public TreeNode sibling;
    //记录父亲节点
    public TreeNode father;
    //记录在源程序的行号
    public int lineno;
    //语法树节点类型
    public AllName.NodeKind nodeKind;
    //成员节点类型
    public AllName.memberKind memberKind;
    //节点个数
    public int idNum = 0;
    //节点中各个标识符的名字
    public List<String> name;
    //暂定
    public List<Integer> table;
    //暂时不知道有什么用，该怎么去使用
    public List<String> typeName;
    //记录语法树节点的其他属性
    public Attr attr;

    public TreeNode() {
        nodeKind = AllName.NodeKind.NodeKindDefault;
        memberKind = AllName.memberKind.memberKindDefault;
    }

    public Attr getAttr() {
        return attr;
    }

    public void setAttr(String type) {
        attr=new Attr(type);
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

    public void boolName() {
        if (this.name == null) {
            this.name = new ArrayList<>();
        }
    }

    public class ArrayAttr {
        public int low;
        public int up;
        //注意东西是用于声明阶段的,所以应该使用声明阶段的枚举类型
        public AllName.memberKind childType;

        public ArrayAttr() {
            childType = AllName.memberKind.memberKindDefault;
        }
    }

    public class procAttr {
        public AllName.LexType paramType;

        public procAttr() {
            paramType = AllName.LexType.LexTypeDefault;
        }
    }
    public class ExpAttr {
        public AllName.LexType op;
        public int val;
        public AllName.LexType varKind;
        public AllName.LexType type;

        public ExpAttr() {
            op = AllName.LexType.LexTypeDefault;
            varKind = AllName.LexType.LexTypeDefault;
            type = AllName.LexType.LexTypeDefault;
        }
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "lineno=" + lineno +
                ", nodeKind=" + nodeKind +
                ", memberKind=" + memberKind +
                ", idNum=" + idNum +
                ", name=" + name +
                ", table=" + table +
                ", typeName=" + typeName +
                ", attr=" + attr +
                '}';
    }
}
