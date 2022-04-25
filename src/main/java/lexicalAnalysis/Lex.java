package lexicalAnalysis;

import Utils.IOUtils;
import Utils.AllName;

import java.util.ArrayList;
import java.util.List;

public class Lex {
    String sourcePath;
    IOUtils ioUtils;
    public List<LexToken> tokenList;

    //构造函数
    public Lex(String sourcePath) {
        this.sourcePath = sourcePath;
        ioUtils = new IOUtils();
        if (!ioUtils.getResourceFile(sourcePath)) {
            throw new RuntimeException("文件路径错误或文件无法打开");
        }
        tokenList = new ArrayList<>();
    }

    //词法分析主程序
    public void LexProcess() {
        //获取第一个字符
        if (ioUtils.ifEnd()) {
            //表示终结符
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.EOF, ""));
            return;
        }
        //运行主过程;
        startState();
    }

    void startState() {
        while (!ioUtils.ifEnd()) {
            char c = ioUtils.getNextChar();
            if ((c <= 'z' & c >= 'a') || (c <= 'Z' & c >= 'A')) {
                idState(new StringBuffer());
            } else if (c >= '0' && c <= '9') {
                numState(new StringBuffer());
            } else if (c == '+' || c == '-' ||
                    c == '*' || c == '/' ||
                    c == '(' || c == ')' ||
                    c == ';' || c == '[' ||
                    c == ']' || c == '=' ||
                    c == '<' || c == ' ' ||
                    c == ',' || c == '\t' || c == '\n') {
                //仔细思考了，似乎所有的\n都是在这个地方进行处理
                DivideDoneState();
            } else if (c == ':') {
                preAssignState();
            } else if (c == '{') {
                commentState();
            } else if (c == '.') {
                rangeState();
            } else if (c=='\'') {
                charState();
            } else {
                otherState();
            }
        }
        tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.EOF, ""));
    }

    /**
     * @param str
     * @return void
     * @Description //标识符与保留字的选取已经完成
     * //todo TEST
     * @Date 2022/4/14 10:00
     **/
    void idState(StringBuffer str) {
        str.append(ioUtils.getNowChar());
        char c = ioUtils.getNextChar();
        //似乎只可以这么去写，应该还有更好的解决方法，但是不想去写了
        if ((c <= 'z' & c >= 'a') || (c <= 'Z' & c >= 'A') || (c >= '0' && c <= '9')) {
            idState(str);
        } else {
            if (AllName.reservedWord.containsKey(str.toString())) {
                tokenList.add(new LexToken(ioUtils.nowLine, AllName.reservedWord.get(str.toString()), "保留字"));
            } else {
                tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.ID, str.toString()));
            }
            //因为多读了一个词语，想不到怎么可以完美的解决这个问题
            ioUtils.getPreChar();
            outState();
        }
    }

    /**
     * @param str
     * @Description //常数的处理,此处会出现一个错误
     * TODO Test
     * @Date 2022/4/14 10:06
     **/
    void numState(StringBuffer str) {
        str.append(ioUtils.getNowChar());
        char c = ioUtils.getNextChar();
        if (c >= '0' && c <= '9') {
            numState(str);
        } else if ((c <= 'z' & c >= 'a') || (c <= 'Z' & c >= 'A')) {
            //先将数字保存
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.INTC, str.toString()));
            //进行错误处理
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.ERROR, String.valueOf(c)));
            //往前进行处理，特殊错误处理
            ioUtils.getPreChar();
            outState();
        } else {
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.INTC, str.toString()));
            ioUtils.getPreChar();
            outState();
        }
    }

    /**
     * @Description //处理单分隔符
     * TODO Test
     * @Date 2022/4/14 11:21
     **/
    void DivideDoneState() {
        char c = ioUtils.getNowChar();
        //跳过空白与一般情况的处理
        if (!(c == '\t' || c == '\n' || c == ' ')) {
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.divideWord.get(String.valueOf(ioUtils.getNowChar())), String.valueOf(c)));
        }
        outState();
    }

    /**
     * @Description //处理赋值
     * TODO
     * @Date 2022/4/14 11:30
     **/
    void preAssignState() {
        char c = ioUtils.getNextChar();
        if (c == '=') {
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.divideWord.get(":="), ":="));
        } else {
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.ERROR, ":"));
            ioUtils.getPreChar();
        }
        outState();
    }

    /**
     * @Description //TODO 暂时先放弃这个给方法，等以后改进或者说需要怎加功能的时候再加上
     * @Date 2022/4/14 11:44
     **/
    void assignState() {

    }


    /**
     * @Description //进行注释的处理
     * TODO Test
     * @Date 2022/4/14 15:20
     **/
    void commentState() {
        //为了配合这个地方的错误处理
        int line = ioUtils.nowLine;
        //此处是没有终结继续
        while (!ioUtils.ifEnd() && ioUtils.getNextChar() != '}') {
        }
        if (ioUtils.ifEnd()) {
            //进行错误处理
            tokenList.add(new LexToken(line, AllName.LexType.ERROR, "{"));
        }
    }

    /**
     * @Description //处理的是数组下界
     * TODO TEST
     * @Date 2022/4/14 14:47
     **/
    void rangeState() {
        char c = ioUtils.getNextChar();
        if (c == '.') {
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.divideWord.get(".."), ".."));
        } else if (c == '\n') {
            //表示的是程序结束，但不在这个地方进行处理

            //此处需要加上点的处理
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.divideWord.get("."), "."));
            ioUtils.getPreChar();
        } else {
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.divideWord.get("."), "."));
            ioUtils.getPreChar();
        }
        outState();
    }


    /**
     * @Description //处理字符的开始阶段,错误处理为忽略点的存在
     * TODO
     * @Date 2022/4/14 14:51
     **/
    void charState() {
        char c = ioUtils.getNextChar();
        if ((c <= 'z' & c >= 'a') || (c <= 'Z' & c >= 'A') || (c >= '0' && c <= '9')) {
            charDoneState(c);
        } else {
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.ERROR, "\'"));
            ioUtils.getPreChar();
        }
        outState();
    }

    /**
     * @param now
     * @Description //单字符的收尾工作,错误处理还是之前的那个单引号的错误
     * TODO 此处的错误处理可能需要进行修改
     * TODO TEST
     * @Date 2022/4/14 14:55
     **/

    void charDoneState(char now) {
        char c = ioUtils.getNextChar();
        if (c == '\'') {
            tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.CHARC, String.valueOf(now)));
        } else {

            tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.ERROR, "\'"));
            ioUtils.getPreChar();
        }
        outState();
    }

    /**
     * @Description //
     * TODO 暂时还没有作用
     * @Date 2022/4/14 14:58
     **/
    void completeDoneState() {

    }


    void otherState() {
        tokenList.add(new LexToken(ioUtils.nowLine, AllName.LexType.ERROR, String.valueOf(ioUtils.getNowChar())));
        outState();
    }

    /**
     * @Description //
     * TODO 暂时还没有作用，之后可能进行添加
     * @Date 2022/4/14 14:57
     **/
    void outState() {
    }

}