package Utils;

import lombok.Data;

import java.util.List;

@Data
public class TestVisual {
    String name;
    String title;
    List<TestVisual> children;

    public TestVisual(String name, String title, List<TestVisual> children) {
        this.name = name;
        this.title = title;
        this.children = children;
    }
}
