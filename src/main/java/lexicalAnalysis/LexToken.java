package lexicalAnalysis;

import Utils.AllName;

public class LexToken {
    int LineShow;
    AllName.LexType type;
    String sem;

    public int getLineShow() {
        return LineShow;
    }

    public void setLineShow(int lineShow) {
        LineShow = lineShow;
    }

    public AllName.LexType getType() {
        return type;
    }

    public void setType(AllName.LexType type) {
        this.type = type;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public LexToken() {
    }

    public LexToken(int lineShow, AllName.LexType type, String sem) {
        LineShow = lineShow;
        this.type = type;
        this.sem = sem;
    }

    @Override
    public String toString() {
        return "LexToken{" +
                "LineShow=" + LineShow +
                ", type=" + type +
                ", sem='" + sem + '\'' +
                '}';
    }
}
