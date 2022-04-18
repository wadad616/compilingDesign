package Utils;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            Set<String>newSet=new HashSet<>();
            for(String ss:set){
                if(AllName.divideWord.containsKey(ss)){
                    AllName.LexType lexType = AllName.divideWord.get(ss);
                    ss=lexType.toString();
                }
                newSet.add(ss);
            }
            System.out.println(String.format("%-90s", s)+JSON.toJSONString(newSet));
        }
    }
    @Test
    public void follow(){
        SelectedProcess selectedProcess = new SelectedProcess("test.txt");
        List<String> vnList = selectedProcess.VNList;
        selectedProcess.traverseVN();
        Map<Expression, Set<String>> expressionFirst = selectedProcess.expressionFirst;
        List<Expression> expressionList = selectedProcess.expressionList;
        for (String s:vnList) {
            System.out.println(s+"  :  "+selectedProcess.followSet.get(s));
        }
    }
}
