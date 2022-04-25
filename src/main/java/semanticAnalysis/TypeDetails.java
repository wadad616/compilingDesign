package semanticAnalysis;

import Utils.AllName;
import lombok.Data;

import java.util.List;
import java.util.Objects;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArrayAttr arrayAttr = (ArrayAttr) o;
            return low == arrayAttr.low && top == arrayAttr.top && elemTy == arrayAttr.elemTy;
        }

        @Override
        public int hashCode() {
            return Objects.hash(elemTy, low, top);
        }
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDetails that = (TypeDetails) o;
        if (this.body.size() != that.body.size()) {
            return false;
        } else {
            for (int i = 0; i < body.size(); i++) {
                SymbolAttribute s1 = body.get(i);
                SymbolAttribute s2 = that.body.get(i);
                if (!s1.name.equals(s2.name)) {
                    return false;
                }
                if (!s1.typePtr.equals(s2.typePtr)) {
                    return false;
                }
            }
        }
        return size == that.size && kind == that.kind && Objects.equals(arrayAttr, that.arrayAttr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, kind, body, arrayAttr);
    }
}
