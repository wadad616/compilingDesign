package Utils;

import lombok.Data;

@Data
//1、表示的
public class MyError {
    public int line;
    public int errorType;

    String getErrorDescription() {
        return switch (errorType) {
            case -1 -> "未知错误";
            case 0 -> "标识符重复定义";
            case 1 -> "标识符未定义使用";
            case 2 -> "记录内部标识符重复";
            case 3 -> "数组上下界问题";
            case 4 -> "不同类型计算错误";
            case 5 -> "类型标识进行错误操作";
            case 6 -> "数组下标不为数字";
            default -> "未知错误default";
        };
    }
}
