import Utils.Expression;
import Utils.SelectedProcess;
import Utils.AllName;
import org.junit.Test;

import java.io.*;
import java.util.*;

public class TestFileReader {
    public enum asd {
        ASD
    }

    @Test
    public void testReader() throws IOException {
        InputStream inputStream = new FileInputStream("pom.xml");
        byte[] buff = new byte[1024];
        StringBuffer str = new StringBuffer();
        int index;
        while ((index = inputStream.read(buff)) != -1) {
            str.append(new String(buff, 0, index));
        }
        System.out.println(str);
    }

    @Test
    public void testReader1() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("pom.xml"));
        char[] buff = new char[1024];
        int index = 0;
        index = reader.read(buff);
        System.out.println(index);
    }

    @Test
    public void testScanner() throws FileNotFoundException {
        Scanner scanner;
        scanner = new Scanner(new FileInputStream("pom.xml"));
        StringBuffer str = new StringBuffer();
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            str.append(s);
        }
        System.out.println(str);
    }

    //测试等号, 结果表明等号的结果还是赋值表达式的左式
    @Test
    public void testEqual() {
        String xx = "123";
        String s = (xx = "1234");
        System.out.println(s);
        System.out.println(s == xx);
    }

    @Test
    public void testOut() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("test.txt"));
        writer.write("asd\t\tasd");
        writer.flush();
        writer.close();
    }

    //说明输出的是字符串
    @Test
    public void testEnum() {
        System.out.println(asd.ASD);
    }

    /**
     * @param
     * @return void
     * @Description //进行保留字的初始化
     * @Date 2022/4/14 8:34
     **/
    @Test
    public void getMapReservedWordInit() {
        String str = "PROGRAM,PROCEDURE,TYPE,VAR,IF,THEN,ELSE,FL,WHILE,DO,ENDWH,BEGIN,END,READ,WRITE,ARRAY,OF,RECORD,RETURN,INTEGER,CHAR";
        String[] split = str.split(",");
        String[] out = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            out[i] = "reservedWord.put(\"" + split[i].toLowerCase() + "\"," + "LexType." + split[i] + ");";
            System.out.println(out[i]);
        }
        System.out.println(out.length);

    }

    /**
     * @param
     * @return void
     * @Description //进行保留字的初始化
     * @Date 2022/4/14 8:34
     **/
    @Test
    public void getMapDivideWordInit() {
        String str = "ASSIGN, EQ, LT, PLUS, MINUS, TIMES, OVER, LPAREN, RPAREN, DOT, COLON, SEMI, COMMA, LMIDPAREN, RMIDPAREN, UNDERANGE";
        String str1 = ":= = < + - * / ( ) . : ; , [ ] ..";
        str = str.replace(" ", "");
        String[] split = str.split(",");
        String[] split1 = str1.split(" ");
        String[] out = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            out[i] = "divideWord.put(\"" + split1[i].toLowerCase() + "\"," + "LexType." + split[i] + ");";
            System.out.println(out[i]);
        }
        System.out.println(out.length);
    }

    @Test
    public void getMapDivideWords() {

        String str1 = "= < + - * / ( ) . : ; [ ]";

        String[] split1 = str1.split(" ");
        String[] out = new String[split1.length];
        System.out.println(out.length);
    }

    @Test
    public void createVTVN() throws FileNotFoundException {
        Scanner scanner;
        scanner = new Scanner(new FileInputStream("test.txt"));
        List<String> list = new ArrayList<>();
        while (scanner.hasNextLine()) {
            list.add(scanner.nextLine());
        }
        Set<String> VN = new HashSet<>();
        List<String[]> list1 = new ArrayList<>();
        for (String s : list) {
            String[] split = s.split(" ");
            for (int i = 0; i < split.length; i++) {
                split[i] = split[i].trim();
            }
            list1.add(split);
        }
        for (String[] s : list1) {
            VN.add(s[0]);
        }
        Set<String> VT = new HashSet<>();
        List<String> VTList = new ArrayList<>();
        for (String[] s : list1) {
            for (int i = 2; i < s.length; i++) {
                if (!VN.contains(s[i])) {
                    VT.add(s[i]);
                    VTList.add(s[i]);
                }
            }
        }

        System.out.println(VN);
        //输出获取
//        for(String s:VN){
//            System.out.print("\""+s+"\",");
//        }
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
//        for(String s:VT){
//            System.out.print("\""+s+"\",");
//        }

        //进行输出操作
        System.out.println(VT.size());
        Map<String, AllName.LexType> reservedWord = AllName.reservedWord;
        Map<String, AllName.LexType> divideWord = AllName.divideWord;
        for (String s : VT) {
            if (!(reservedWord.containsKey(s.toLowerCase()) || divideWord.containsKey(s.toLowerCase()))) {
                System.out.println(s);
            }
        }
    }

    @Test
    public void testSelectedProcess() throws Exception {
        SelectedProcess selectedProcess = new SelectedProcess("test.txt");
        System.out.println(selectedProcess);
        System.out.println(selectedProcess.VT);
        System.out.println(selectedProcess.VT.size());
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        System.out.println("-----------------------------------------------------");
        System.out.println(selectedProcess.VN);
        System.out.println(selectedProcess.VN.size());
        for (Expression e : selectedProcess.expressionList) {
            System.out.println(e);
        }

    }

    //获得函数
    @Test
    public void makeFunction() throws FileNotFoundException {
        Scanner scanner;
        scanner = new Scanner(new FileInputStream("1.txt"));
        List<String> list = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            char c = s.charAt(0);
            if ((c <= 'z' & c >= 'a') || (c <= 'Z' & c >= 'A')){
                StringBuffer str=new StringBuffer(s);
                str.replace(0,1,String.valueOf(Character.toLowerCase(str.charAt(0))));
                String s1="TreeNode "+ str+"(){return null;}";
                list.add(s1);
            }
        }
        for(String s:list){
            System.out.println(s);
        }
    }
    @Test
    public void testStack(){
        //注意就是使用pop  push   peek
        Stack<String>strings=new Stack<>();
    }
    @Test
    public void testProcessGenerate(){
        for(int i=1;i<=104;i++){
            System.out.println("void process"+i+"() {pushSymbol("+i+");}");
        }
    }
    @Test
    public void testGenerate1(){
        for(int i=1;i<=104;i++){
            System.out.println("case "+i+": process"+i+"();break;");
        }
    }
    @Test
    public void testLine(){
        ArrayList<String >strings=null;
        for(String s:strings){
            System.out.println(s);
        }
    }

}
