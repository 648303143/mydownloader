import core.DownLoader;
import util.LogUtils;

import java.util.Scanner;

/**
 * @author zzzZqy
 * @Description
 * @create 2021-11-07 17:01
 */
public class Main {
    public static void main(String[] args) {
        String url = null;
        //从命令行参数获得地址, 若没有则从控制台获得
        if (args == null || args.length == 0) {
            LogUtils.info("请输入下载地址:");
            while (true){
                Scanner scan = new Scanner(System.in);
                url = scan.next();
                if (url != null && url.matches("^https://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$")){
                    break;
                }else {
                    LogUtils.info("输入格式不正确,请重新输入:");
                }
            }
        }else {
            url = args[0];
        }

        //调用DownLoader类的download方法实现下载功能
        DownLoader downLoader = new DownLoader();
        downLoader.download(url);
    }

}
