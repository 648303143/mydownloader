package core;


import util.HTTPUtils;
import util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static constant.Constant.*;

/**
 * @author zzzZqy
 * @Description
 * @create 2021-11-07 19:08
 */
public class DownLoadTask implements Callable<Boolean> {
    /**
     * 下载链接
     */
    private String url;

    private long startPos;

    private long endPos;

    /**
     * 分块标号
     */
    private int part;

    private CountDownLatch countDownLatch;

    public DownLoadTask(String url, long startPos, long endPos, int part, CountDownLatch countDownLatch) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws IOException {
        //获取下载文件名
        String httpFileName = HTTPUtils.getHttpFileName(url);
        //分块文件名
        httpFileName = httpFileName + ".temp" + part;
        //下载路径
        String path = PATH + httpFileName;
        //获取下载链接对象
        HttpURLConnection httpUrlConnection = HTTPUtils.getHttpURLConnection(url, startPos, endPos);
        try (
                InputStream inputStream = httpUrlConnection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                RandomAccessFile raf = new RandomAccessFile(path, "rw");

        ) {
            int len = -1;
            byte[] buffer = new byte[BYTE_SIZE];
            while ((len = bis.read(buffer)) != -1) {
                //记录下载的大小
                DownLoadInfoThread.loadedSize.add(len);
                raf.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            LogUtils.error("文件不存在!");
            return false;
        } catch (IOException e) {
            LogUtils.error("下载失败");
            return false;
        } finally {
            if (httpUrlConnection != null){
                httpUrlConnection.disconnect();
            }

            //分块下载结束后线程数减一
            countDownLatch.countDown();
        }


        return true;
    }
}
