import Utils.IOUtils;
import org.junit.Test;

public class TestIOUtils {
    @Test
    public void test1(){
        IOUtils ioUtils = new IOUtils();
        ioUtils.getResourceFile("test.txt");
        //这么傻逼的逻辑错误没有发现
        while (!ioUtils.ifEnd()){
            System.out.print(ioUtils.getNextChar());
        }
    }
}
