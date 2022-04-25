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
            case 7 -> "非数组进行数组调用";
            case 8 -> "非记录进行记录调用";
            case 9 -> "数组越界";
            case 10 -> "记录中不存在此成员";
            case 11 -> "条件语句类型不匹配";
            case 12 -> "赋值语句不相容";
            case 13 -> "无法读取非变量标识符";
            case 14 -> "写语句表达式类型错误";
            case 15 -> "非过程调用";
            case 16 -> "参数数量错误";
            case 17 -> "过程调用参数未定义";
            case 18 -> "过程参数类型未匹配";
            default -> "未知错误default";
        };
    }
}
