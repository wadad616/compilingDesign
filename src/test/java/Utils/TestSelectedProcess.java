package Utils;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.text.Format;
import java.util.*;

public class TestSelectedProcess {
    @Test
    public void testTraverseVN() {
        SelectedProcess selectedProcess = new SelectedProcess("test.txt");
        List<String> vnList = selectedProcess.VNList;
        selectedProcess.traverseVN();
        Map<Expression, Set<String>> expressionFirst = selectedProcess.expressionFirst;
        List<Expression> expressionList = selectedProcess.expressionList;
        for (Expression s : expressionList) {
            Set<String> set = selectedProcess.predictSet.get(s);
            Set<String> newSet = new HashSet<>();
            for (String ss : set) {
                if (AllName.divideWord.containsKey(ss)) {
                    AllName.LexType lexType = AllName.divideWord.get(ss);
                    ss = lexType.toString();
                }
                newSet.add(ss);
            }
            System.out.println(String.format("%-90s", s) + JSON.toJSONString(newSet));
        }
    }

    @Test
    public void follow() {
        SelectedProcess selectedProcess = new SelectedProcess("test.txt");
        List<String> vnList = selectedProcess.VNList;
        selectedProcess.traverseVN();
        Map<Expression, Set<String>> expressionFirst = selectedProcess.expressionFirst;
        List<Expression> expressionList = selectedProcess.expressionList;
        for (String s : vnList) {
            System.out.println(s + "  :  " + selectedProcess.followSet.get(s));
        }
    }

    @Test
    public void TestLL1Table() {
        SelectedProcess selectedProcess = new SelectedProcess("test.txt");
        selectedProcess.traverseVN();
        List<Expression> expressionList = selectedProcess.expressionList;
        Map<String, Map<String, Integer>> LL1Table = new HashMap<>();
        int index = 1;
        for (Expression s : expressionList) {
            Set<String> set = selectedProcess.predictSet.get(s);
            //初始化值
            Map<String, Integer> nowMap = null;
            if (!LL1Table.containsKey(s.leftExp)) {
                LL1Table.put(s.leftExp, new HashMap<>());
            }
            nowMap = LL1Table.get(s.leftExp);
            for (String ss : set) {
                if (AllName.divideWord.containsKey(ss)) {
                    AllName.LexType lexType = AllName.divideWord.get(ss);
                    ss = lexType.toString();
                }
                nowMap.put(ss, index);
            }
            index++;
        }
        List<String> vnList = selectedProcess.VNList;
        Set<String> uniqueList = new HashSet<>();
        for (String s : vnList) {
            if (!uniqueList.contains(s)) {
                System.out.print(String.format("%-20s", s));
                System.out.print(LL1Table.get(s));
                System.out.println();
                uniqueList.add(s);
            }
        }


    }

}
