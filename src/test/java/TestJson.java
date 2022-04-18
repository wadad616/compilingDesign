import Utils.TestVisual;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

//OK 可视化操作完成
public class TestJson {
    @Test
    public void basicTest() {
        List<Object> objects = new ArrayList<>();
        objects.add("asdas");
        objects.add("asdas");
        objects.add("asdas");
        objects.add("asdas");
        objects.add("asdas");
        System.out.println(JSON.toJSONString(objects));
    }

    @Test
    public void testVisual() {
        String xx = "{\n" +
                "\t\t\t\t\t'name': \"adfsa\",\n" +
                "\t\t\t\t\t'title': 'general manager',\n" +
                "\t\t\t\t\t'children': [{\n" +
                "\t\t\t\t\t\t\t'name': 'Bo Miao',\n" +
                "\t\t\t\t\t\t\t'title': 'department manager'\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t'name': 'Su Miao',\n" +
                "\t\t\t\t\t\t\t'title': 'department manager',\n" +
                "\t\t\t\t\t\t\t'children': [{\n" +
                "\t\t\t\t\t\t\t\t\t'name': 'Tie Hua',\n" +
                "\t\t\t\t\t\t\t\t\t'title': 'senior engineer',\n" +
                "\t\t\t\t\t\t\t\t\t'children':'',\n" +
                "\t\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\t'name': 'Hei Hei',\n" +
                "\t\t\t\t\t\t\t\t\t'title': 'senior engineer',\n" +
                "\t\t\t\t\t\t\t\t\t'children': [{\n" +
                "\t\t\t\t\t\t\t\t\t\t'name': 'Dan Dan',\n" +
                "\t\t\t\t\t\t\t\t\t\t'title': 'engineer'\n" +
                "\t\t\t\t\t\t\t\t\t}]\n" +
                "\t\t\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t\t\t'name': 'Pang Pang',\n" +
                "\t\t\t\t\t\t\t\t\t'title': 'senior engineer'\n" +
                "\t\t\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t\t\t]\n" +
                "\t\t\t\t\t\t},\n" +
                "\t\t\t\t\t\t{\n" +
                "\t\t\t\t\t\t\t'name': 'Hong Miao',\n" +
                "\t\t\t\t\t\t\t'title': 'department manager'\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t\t}";
        TestVisual testVisual1 = JSON.parseObject(xx, TestVisual.class);
        String s=JSON.toJSONString(testVisual1);
        System.out.println(s);
    }
}
