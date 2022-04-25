package semanticAnalysis;

import Utils.AllName;
import lombok.Data;

import java.util.List;

@Data
public class TypeDetails {

    int size;
    AllName.Types kind;
    List<SymbolAttribute> body;
    ArrayAttr arrayAttr;


    public TypeDetails() {
    }

    public TypeDetails(String type) {
        if (type.equals("array")) {
            arrayAttr = new ArrayAttr();
        }
    }

    @Data
    class ArrayAttr {
        AllName.Types elemTy;
        int low;
        int top;
    }


    //两个基础类型
    public static TypeDetails intPtr;
    public static TypeDetails charPtr;
    public static TypeDetails boolPtr;

    static {
        intPtr = new TypeDetails();
        intPtr.size = 1;
        intPtr.kind = AllName.Types.intTy;

        charPtr = new TypeDetails();
        charPtr.size = 1;
        charPtr.kind = AllName.Types.charTy;

        boolPtr = new TypeDetails();
        boolPtr.size = 1;
        boolPtr.kind = AllName.Types.boolTy;
    }

}
