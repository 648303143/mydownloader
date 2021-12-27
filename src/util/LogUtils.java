package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zzzZqy
 * @Description
 * @create 2021-11-07 17:04
 */
public class LogUtils {

    /**
     * 错误日志
     *
     * @param msg
     * @param args
     */
    public static void error(String msg, Object... args) {
        print(msg, "error", args);
    }

    /**
     * 提示类日志
     *
     * @param msg
     * @param args
     */
    public static void info(String msg, Object... args) {
        print(msg, "info", args);
    }

    /**
     * 打印信息
     *
     * @param msg  需要打印的信息
     * @param kind 日志类型
     * @param args 格式化参数
     */
    public static void print(String msg, String kind, Object... args) {
        msg = String.format(msg.replace("{}", "%s"), args);
        String name = Thread.currentThread().getName();

        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh-mm-ss")) + "-" + name + "-" + kind + "-" + msg);

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
