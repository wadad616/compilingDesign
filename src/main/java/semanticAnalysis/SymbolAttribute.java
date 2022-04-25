package semanticAnalysis;

import Utils.AllName;
import lombok.Data;

import java.util.List;

@Data
public class SymbolAttribute {
    //必须
    TypeDetails typePtr;
    AllName.Types kind;
    String name;


    VarAttr varAttr;
    ProcAttr procAttr;

    public SymbolAttribute() {
    }
    public SymbolAttribute(SymbolAttribute s) {
        this.procAttr=s.procAttr;
        this.varAttr=new VarAttr();
        this.varAttr.access=s.varAttr.access;
        this.varAttr.level=s.varAttr.level;
        this.kind=s.kind;
        this.typePtr=s.typePtr;
    }

    public SymbolAttribute(String type) {
        if (type.equals("var")) {
            varAttr = new VarAttr();
        } else if(type.equals("proc")) {
            procAttr = new ProcAttr();
        }
    }
    @Data
    public class VarAttr {
        boolean access;
        int level;
        int off;
    }
    @Data
    public class ProcAttr {
        int level;
        List<TypeDetails> param;
        int code;
        int size;
    }

}

