package core;


import constant.Constant;

import java.util.concurrent.atomic.LongAdder;

import static constant.Constant.*;

/**
 * @author zzzZqy
 * @Description
 * @create 2021-11-09 10:39
 */

public class DownLoadInfoThread implements Runnable {

    private double httpFileLength;

    static LongAdder loadedSize = new LongAdder();

    private double preLoadedSize;

    public DownLoadInfoThread(long httpFileLength) {
        this.httpFileLength = httpFileLength;
    }

    @Override
    public void run() {
        String httpFileSize = String.format("%.2f",httpFileLength / MB);

        String finishedSize = String.format("%.2f",loadedSize.doubleValue() / MB);

        int speed = (int) ((loadedSize.doubleValue() - preLoadedSize) / 1024);
        preLoadedSize = loadedSize.doubleValue();

        double remainSize = (httpFileLength - loadedSize.doubleValue()) / 1024;
        String remainTime = String.format("%.2f",remainSize / speed);

        String downloadInfo = String.format("已下载: %sMb/%sMb 下载速度: %skb 剩余时间: %ss",
                finishedSize,httpFileSize,speed,remainTime);

        System.out.print("\r");
        System.out.print(downloadInfo);
    }
}
