package Utils;

import java.util.List;

public class Expression {
    String leftExp;
    List<String> rightExp;

    public Expression() {
    }

    public Expression(String leftExp, List<String> rightExp) {
        this.leftExp = leftExp;
        this.rightExp = rightExp;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        if(rightExp.size()!=0){
            for (String s : rightExp) {
                str.append(" " + s);
            }
        }

        return leftExp + " ::=" + str.toString();
    }
}
