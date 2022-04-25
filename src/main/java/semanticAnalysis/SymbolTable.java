package semanticAnalysis;

import Utils.AllName;
import Utils.MyError;
import grammer.TreeNode;

import java.util.*;

import static Utils.AllName.Types.*;
import static semanticAnalysis.TypeDetails.charPtr;
import static semanticAnalysis.TypeDetails.intPtr;

//todo record中可以有数组,需要去测试一下


public class SymbolTable {
    List<Map<String, SymbolAttribute>> symbolTables;
    int size = 0;
    TreeNode root;
    int currentLeve = 0;
    int currentOffset = 0;
    //表示的重复定义错误
    MyError error = null;

    public SymbolTable(TreeNode root) {
        this.root = root;
        symbolTables = new ArrayList<>();
        traverse(root);
    }

    Map<String, SymbolAttribute> getSymbolTable(int level) {
        return symbolTables.get(level);
    }

    SymbolAttribute getSymbolAttribute(String name, int level) {
        for (int i = level; i >= 0; i--) {
            Map<String, SymbolAttribute> map = symbolTables.get(i);
            if (map.containsKey(name)) {
                return map.get(name);
            }
        }
        return null;
    }

    SymbolAttribute getSymbolAttribute(String name) {
        return getSymbolAttribute(name, currentLeve);
    }

    void createCurrentTable() {
        if (size == 0) {
            symbolTables.add(new HashMap<>());
            size++;
            return;
        }
        if (currentLeve == size) {
            symbolTables.add(new HashMap<>());
            size++;
        }
    }

    public List<Map<String, SymbolAttribute>> getSymbolTables() {
        return symbolTables;
    }

    void traverse(TreeNode t) {
        if (error != null) {
            return;
        }
        if (t.nodeKind == AllName.NodeKind.TypeK) {
            for (TreeNode t1 : t.child) {
                traverseTypeK(t1);
            }
        } else if (t.nodeKind == AllName.NodeKind.VarK) {
            for (TreeNode t1 : t.child) {
                traverseVarK(t1);
            }
        } else if (t.nodeKind == AllName.NodeKind.ProcDecK) {
            traverseProcK(t);
        } else {
            if (t.child != null) {
                for (TreeNode t1 : t.child) {
                    traverse(t1);
                }
            }
        }
    }

    void traverseTypeK(TreeNode t) {
        createCurrentTable();
        String s = t.name.get(0);
        Map<String, SymbolAttribute> symbolTable = getSymbolTable(currentLeve);
        SymbolAttribute symbolAttribute = new SymbolAttribute();
        symbolAttribute.kind = typeKind;
        symbolAttribute.name = s;
        //判断是否有重复定义
        if (symbolTable.containsKey(s)) {
            error = new MyError();
            error.line = t.lineno;
            error.errorType = 0;
            return;
        }
        if (t.memberKind == AllName.memberKind.IntegerK) {

            symbolAttribute.typePtr = intPtr;

        } else if (t.memberKind == AllName.memberKind.CharK) {

            symbolAttribute.typePtr = charPtr;

        } else if (t.memberKind == AllName.memberKind.ArrayK) {

            TypeDetails typeDetails = new TypeDetails("array");

            //设置内部表示
            TreeNode.ArrayAttr arrayAttr = t.attr.arrayAttr;
            if (arrayAttr.childType == AllName.memberKind.IntegerK) {
                typeDetails.arrayAttr.elemTy = intTy;
            } else {
                typeDetails.arrayAttr.elemTy = charTy;
            }
            if (arrayAttr.low > arrayAttr.up) {
                MyError error = new MyError();
                error.line = t.lineno;
                error.errorType = 2;
                return;
            }
            typeDetails.arrayAttr.low = arrayAttr.low;
            typeDetails.arrayAttr.top = arrayAttr.up;

            typeDetails.kind = arrayTy;
            typeDetails.size = arrayAttr.up - arrayAttr.low + 1;
            symbolAttribute.typePtr = typeDetails;
        } else if (t.memberKind == AllName.memberKind.RecordK) {

            TypeDetails typeDetails = new TypeDetails();
            typeDetails.size = t.child.size();
            typeDetails.kind = recordTy;
            typeDetails.body = new ArrayList<>();
            int off = 0;
            Set<String> jud = new HashSet<>();
            for (TreeNode t1 : t.child) {
                if (jud.contains(t1.name.get(0))) {
                    MyError error = new MyError();
                    error.errorType = 3;
                    error.line = t1.getLineno();
                    return;
                }
                jud.add(t1.name.get(0));
                SymbolAttribute symbolAttribute1 = new SymbolAttribute("var");
                if (t1.memberKind == AllName.memberKind.IntegerK) {
                    symbolAttribute1.name = t1.name.get(0);
                    symbolAttribute1.typePtr = intPtr;
                    symbolAttribute1.varAttr.off = off;
                } else if (t1.memberKind == AllName.memberKind.CharK) {
                    symbolAttribute1.name = t1.name.get(0);
                    symbolAttribute1.typePtr = charPtr;
                    symbolAttribute1.varAttr.off = off;
                }
                off++;
            }
            symbolAttribute.typePtr = typeDetails;
        }
        symbolTable.put(s, symbolAttribute);
    }

    //todo 实参与变参
    void traverseVarK(TreeNode t) {
        createCurrentTable();
        String s = t.name.get(0);
        Map<String, SymbolAttribute> symbolTable = getSymbolTable(currentLeve);
        SymbolAttribute symbolAttribute = new SymbolAttribute("var");
        symbolAttribute.kind = varKind;
        //判断是否有重复定义
        if (symbolTable.containsKey(s)) {
            error = new MyError();
            error.line = t.lineno;
            error.errorType = 0;
            return;
        }
        symbolAttribute.name = s;
        if (t.memberKind == AllName.memberKind.IntegerK) {
            symbolAttribute.typePtr = intPtr;
            symbolAttribute.varAttr.level = currentLeve;
            symbolAttribute.varAttr.off = currentOffset;
            symbolAttribute.varAttr.access = true;
            currentOffset++;
        } else if (t.memberKind == AllName.memberKind.CharK) {
            symbolAttribute.typePtr = charPtr;
            symbolAttribute.varAttr.level = currentLeve;
            symbolAttribute.varAttr.off = currentOffset;
            symbolAttribute.varAttr.access = true;
            currentOffset++;
        } else if (t.memberKind == AllName.memberKind.ArrayK) {
            TypeDetails typeDetails = new TypeDetails("array");
            //设置内部表示
            TreeNode.ArrayAttr arrayAttr = t.attr.arrayAttr;
            if (arrayAttr.childType == AllName.memberKind.IntegerK) {
                typeDetails.arrayAttr.elemTy = intTy;
            } else {
                typeDetails.arrayAttr.elemTy = charTy;
            }
            typeDetails.arrayAttr.low = arrayAttr.low;
            typeDetails.arrayAttr.top = arrayAttr.up;

            typeDetails.kind = arrayTy;
            typeDetails.size = arrayAttr.up - arrayAttr.low + 1;
            symbolAttribute.typePtr = typeDetails;

            symbolAttribute.varAttr.level = currentLeve;
            symbolAttribute.varAttr.off = currentOffset;
            symbolAttribute.varAttr.access = false;
            currentOffset += typeDetails.size;

        } else if (t.memberKind == AllName.memberKind.RecordK) {
            TypeDetails typeDetails = new TypeDetails();
            typeDetails.size = t.child.size();
            typeDetails.kind = recordTy;
            typeDetails.body = new ArrayList<>();
            Set<String> jud = new HashSet<>();
            int off = 0;
            for (TreeNode t1 : t.child) {
                if (jud.contains(t1.name.get(0))) {
                    MyError error = new MyError();
                    error.errorType = 3;
                    error.line = t1.getLineno();
                    return;
                }
                jud.add(t1.name.get(0));
                SymbolAttribute symbolAttribute1 = new SymbolAttribute("var");
                if (t1.memberKind == AllName.memberKind.IntegerK) {
                    symbolAttribute1.name = t1.name.get(0);
                    symbolAttribute1.typePtr = intPtr;
                    symbolAttribute1.varAttr.off = off;
                    off++;
                } else if (t1.memberKind == AllName.memberKind.CharK) {
                    symbolAttribute1.name = t1.name.get(0);
                    symbolAttribute1.typePtr = charPtr;
                    symbolAttribute1.varAttr.off = off;
                    off++;
                } else if (t1.memberKind == AllName.memberKind.ArrayK) {
                    symbolAttribute1.name = t1.name.get(0);
                    TypeDetails typeDetails1 = new TypeDetails("array");
                    symbolAttribute1.typePtr = typeDetails1;
                    symbolAttribute1.varAttr.off = off;

                    TreeNode.ArrayAttr arrayAttr = t1.attr.arrayAttr;
                    if (arrayAttr.low > arrayAttr.up) {
                        MyError error = new MyError();
                        error.line = t.lineno;
                        error.errorType = 2;
                        return;
                    }
                    if (arrayAttr.childType == AllName.memberKind.IntegerK) {
                        typeDetails1.arrayAttr.elemTy = intTy;
                    } else {
                        typeDetails1.arrayAttr.elemTy = charTy;
                    }
                    typeDetails1.kind = arrayTy;
                    typeDetails1.arrayAttr.low = arrayAttr.low;
                    typeDetails1.arrayAttr.top = arrayAttr.up;
                    typeDetails1.size = arrayAttr.up - arrayAttr.low + 1;
                    off+=typeDetails1.size;
                }

            }
            symbolAttribute.typePtr = typeDetails;


            symbolAttribute.varAttr.level = currentLeve;
            symbolAttribute.varAttr.off = currentOffset;
            symbolAttribute.varAttr.access = false;
            currentOffset += typeDetails.size;

        } else if (t.memberKind == AllName.memberKind.IdK) {
            SymbolAttribute symbolAttribute1 = symbolTable.get(t.name.get(0));
            if (symbolAttribute1.kind != typeKind || symbolAttribute1 == null) {
                MyError myError = new MyError();
                myError.line = t.lineno;
                myError.errorType = 1;
            } else {
                symbolAttribute.typePtr = symbolAttribute1.typePtr;
                if (symbolAttribute1.typePtr == intPtr || symbolAttribute1.typePtr == charPtr) {
                    symbolAttribute.varAttr.access = true;
                } else {
                    symbolAttribute.varAttr.access = false;
                }
                symbolAttribute.varAttr.level = currentLeve;
                symbolAttribute.varAttr.off = currentOffset;
                currentOffset += symbolAttribute.typePtr.size;
            }
        }

        currentOffset -= symbolAttribute.typePtr.size;
        for (String s1 : t.name) {
            SymbolAttribute symbolAttribute1 = new SymbolAttribute(symbolAttribute);
            symbolAttribute1.name = s1;
            symbolAttribute1.varAttr.off = currentOffset;
            currentOffset += symbolAttribute.typePtr.size;
            if (t.attr != null && t.attr.procAttr != null) {
                if (t.attr.procAttr.paramType == AllName.LexType.VarParaType)
                    symbolAttribute1.varAttr.access = false;
            }
            symbolTable.put(s1, symbolAttribute1);
            symbolAttribute = symbolAttribute1;
        }

    }

    void traverseProcK(TreeNode t1) {
        createCurrentTable();
        Map<String, SymbolAttribute> symbolTable = getSymbolTable(currentLeve);
        String s = t1.name.get(0);
        if (symbolTable.containsKey(s)) {
            error = new MyError();
            error.line = t1.lineno;
            error.errorType = 0;
            return;
        }
        SymbolAttribute proc = new SymbolAttribute("proc");
        proc.kind = procKind;
        proc.procAttr.level = currentLeve;
        proc.procAttr.param = new ArrayList<>();
        proc.name = s;
        proc.typePtr = null;

        //参数的处理
        currentLeve++;
        int swap = currentOffset;
        currentOffset = 0;
        TreeNode another = null;
        for (TreeNode t : t1.child) {
            if (error != null) {
                return;
            }
            if (t.nodeKind == AllName.NodeKind.DecK) {
                traverseVarK(t);
                proc.procAttr.param.add(t.name.get(0));
            } else {
                another = t;
                break;
            }
        }

        //声明部分的处理
        if (another != null && another.nodeKind == AllName.NodeKind.TotalDecK) {
            traverse(another);
        }

        symbolTable.put(s, proc);
        currentOffset = swap;
        currentLeve--;
    }

    void traverseAll(TreeNode t) {
        if (error != null) {
            return;
        }
        for (TreeNode t1 : t.child) {
            switch (t1.nodeKind) {
                case TotalDecK -> traverseAll(t1);
                case StmLK -> traverseBody(t1);
                case ProcDecK -> {
                    currentLeve++;
                    traverseAll(t1);
                    currentLeve--;
                }
            }
        }

    }

    //todo 需要注意写个关于层数的函数
    void traverseBody(TreeNode t) {
        if (error != null) {
            return;
        }
        if (t.memberKind == AllName.memberKind.IfK) {
            traverseIf(t);
        } else if (t.memberKind == AllName.memberKind.ThenK) {
            for (TreeNode t1 : t.child) {
                traverseBody(t1);
            }
        } else if (t.memberKind == AllName.memberKind.ElseK) {
            for (TreeNode t1 : t.child) {
                traverseBody(t1);
            }
        } else if (t.memberKind == AllName.memberKind.WhileK) {
            traverseWhile(t);
        } else if (t.memberKind == AllName.memberKind.DoK) {
            for (TreeNode t1 : t.child) {
                traverseBody(t1);
            }
        } else if (t.memberKind == AllName.memberKind.AssignK) {
            traverseAssign(t);
        } else if (t.memberKind == AllName.memberKind.ReadK) {
            traverseRead(t);
        } else if (t.memberKind == AllName.memberKind.WriteK) {
            traverseWrite(t);
        } else if (t.memberKind == AllName.memberKind.CallK) {
            traverseCall(t);
        } else if (t.memberKind == AllName.memberKind.ReturnK) {
            traverseReturn(t);
        }
    }

    //写嵌套好了
    void traverseIf(TreeNode t) {
        if (error != null) {
            return;
        }
        //Exp
        TreeNode treeNode = t.child.get(0);
        AllName.Types types = processExp(treeNode);
        if (types != boolTy) {
            //错误处理
        }
        TreeNode treeNode1 = t.child.get(1);
        traverseBody(treeNode1);
        if (error != null) {
            return;
        }
        TreeNode treeNode2 = t.child.get(2);
        traverseBody(treeNode1);
        if (error != null) {
            return;
        }

    }

    void traverseWhile(TreeNode t) {

    }

    void traverseAssign(TreeNode t) {

    }

    void traverseRead(TreeNode t) {

    }

    void traverseWrite(TreeNode t) {

    }

    void traverseCall(TreeNode t) {

    }

    void traverseReturn(TreeNode t) {

    }


    //todo 默认If后必须接上表达式
    //注意每次使用之后需要加上 一个错误判断
    AllName.Types processExp(TreeNode t) {
        if (error != null) {
            return TypesDefault;
        }
        switch (t.memberKind) {
            case OpK: {
                TreeNode t1 = t.child.get(0);
                AllName.Types types = processExp(t1);
                if (error != null) {
                    return TypesDefault;
                }

                TreeNode t2 = t.child.get(1);
                AllName.Types types1 = processExp(t2);
                if (error != null) {
                    return TypesDefault;
                }

                if (types == types1) {
                    return boolTy;
                } else {
                    MyError error = new MyError();
                    error.errorType = 4;
                    error.line = t.lineno;
                    return TypesDefault;
                }
            }
            break;
            case IdK: {
                if (t.attr.expAttr.varKind == AllName.LexType.idV) {
                    String s = t.name.get(0);
                    //写完了 todo 这里需要写一个获取标识符的函数
                    SymbolAttribute symbolAttribute = getSymbolAttribute(s);
                    if (symbolAttribute == null) {
                        MyError error = new MyError();
                        error.line = t.getLineno();
                        error.errorType = 1;
                        return TypesDefault;
                    }
                    if (symbolAttribute.kind == typeKind) {
                        MyError error = new MyError();
                        error.line = t.getLineno();
                        error.errorType = 5;
                        return TypesDefault;
                    }
                    return symbolAttribute.typePtr.kind;

                } else if (t.attr.expAttr.varKind == AllName.LexType.ArrayMembV) {
                    //这里也需要一个递归
                    TreeNode tNow = t.child.get(0);
                    AllName.Types types = processExp(tNow);
                    if (error != null) {
                        return TypesDefault;
                    }
                    if (types != intTy) {
                        MyError error = new MyError();
                        error.line = t.getLineno();
                        error.errorType = 6;
                        return TypesDefault;
                    }
                    if (t.attr.arrayAttr.childType == AllName.memberKind.IntegerK) {
                        return intTy;
                    } else {
                        return charTy;
                    }

                } else if (t.attr.expAttr.varKind == AllName.LexType.FieldMembV) {
                    //这里需要一个递归
                    TreeNode t1 = t.child.get(0);


                }

            }
            break;
            case ConstK: {
                if (t.attr.expAttr.type == AllName.LexType.Integer) {
                    return intTy;
                } else {
                    //表示其他情况统一为字符形
                    return charTy;
                }
            }
            break;
            default: {
                MyError error = new MyError();
                error.line = t.lineno;
                error.errorType = -1;
                return TypesDefault;
            }
        }

    }

}
