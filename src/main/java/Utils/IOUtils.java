package Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * @Description //TODO 还没有进行测试
 * @Date 2022/4/13 21:09
 * @ 工具类，读取文件和进行每个字符的读取，自动去除多余的空行，与去除每一行的前后空格方便文件的读取
 **/
public class IOUtils {
    Scanner reader;
    public String nowLineStr;
    public int nowLine = 0;
    public int lineIndex = 0;
    public int totalLineCount = 0;
    boolean end = true;
    char nowChar;
    //文件的读取并且创建获取reader

    public boolean getResourceFile(String path) {
        try {
            reader = new Scanner(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        nextLine();
        return true;
    }

    //注意使用完ifEnd的时候nowLine对应不正确
    public boolean ifEnd() {
        if (lineIndex == totalLineCount) {
            nextLine();
        }
        return lineIndex >= totalLineCount;
    }

    //判断是否有上一个字符
    public boolean hasPre() {
        return lineIndex > 0;
    }

    //获得上一个字符
    public char getPreChar() {
        nowChar = nowLineStr.charAt(--lineIndex);
        return nowChar;
    }

    //获得下一个字符
    public char getNextChar() {
        nowChar = nowLineStr.charAt(lineIndex);
        lineIndex++;
        return nowChar;
    }
    //获得当前字符
    public char getNowChar(){
        return nowChar;
    }

    //获取当前字符所在的行
    public int getNowLine() {
        return nowLine;
    }

    //获得下一行的信息,并且去除掉完全为空格的行
    //去除了每一行的前后的空白
    public void nextLine() {
        while (reader.hasNextLine()) {
            String s = reader.nextLine().trim();
            if (!s.trim().equals("")) {
                nowLineStr = s+"\n";
                nowLine++;
                lineIndex = 0;
                totalLineCount = nowLineStr.length();
                end = false;
                return;
            }
            nowLine++;
        }
        end = true;
    }

}
