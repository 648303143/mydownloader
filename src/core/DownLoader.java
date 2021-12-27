package core;

import util.FileUtils;
import util.HTTPUtils;
import util.LogUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;

import static constant.Constant.*;

/**
 * @author zzzZqy
 * @Description
 * @create 2021-11-07 18:45
 */
public class DownLoader {
    /**
     * 实例化定时调度线程池
     */
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(THREAD_NUM));

    /**
     * 实例化线程计数器
     */
    private CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);

    public void download(String url) {
        //获取下载文件名
        String httpFileName = HTTPUtils.getHttpFileName(url);
        //获取下载路径
        String path = PATH + httpFileName;

        HttpURLConnection httpUrlConnection = null;

        //获取本地文件大小
        long existFileLength = FileUtils.getExistFileLength(path);
        try {
            //获取http下载文件大小
            long httpFileLength = HTTPUtils.getHttpFileLength(url);
            //如果本地文件大小等于要下载文件大小,直接退出
            if (existFileLength >= httpFileLength) {
                LogUtils.error("{}文件已存在,无需重新下载!", path);
                return;
            }
            //获取http链接对象
            httpUrlConnection = HTTPUtils.getHttpURLConnection(url);

            //实例化下载信息线程对象
            DownLoadInfoThread downLoadInfoThread = new DownLoadInfoThread(httpFileLength);
            //将实现了Runnable的线程投入线程池定时调度
            ses.scheduleAtFixedRate(downLoadInfoThread, 1, 1, TimeUnit.SECONDS);

            //实例化接受线程返回值的集合
            ArrayList<Future> list = new ArrayList<>();
            //切分任务下载
            split(url, list);

            //线程计数器,等待所有分块任务下载完成再合并
            countDownLatch.await();

            //合并临时文件,若合并完成再清理临时文件
            if (merge(path)) {
                clearTemp(path);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.print("\r");
            System.out.print("下载完成");

            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }

            ses.shutdownNow();
            threadPool.shutdown();
        }

    }

    /**
     * 切分下载
     *
     * @param url  下载地址
     * @param list  接受返回值的集合
     */
    private void split(String url, ArrayList<Future> list) {
        try {
            //获取下载文件大小
            long httpFileLength = HTTPUtils.getHttpFileLength(url);
            //下载文件分块后的大小
            long splitSize = httpFileLength / THREAD_NUM;

            //分块
            for (int i = 0; i < THREAD_NUM; i++) {
                //每块的开始位置
                long startPos = splitSize * i;
                //结束位置
                long endPos;
                //如果是最后一块,则结束位置一直到最后
                if (i == THREAD_NUM - 1) {
                    endPos = 0;
                } else {
                    endPos = startPos + splitSize;
                }

                //避免开始位置与结束位置重合
                if (i != 0) {
                    startPos++;
                }
                //实例化分块下载线程任务
                DownLoadTask downLoadTask = new DownLoadTask(url, startPos, endPos, i, countDownLatch);
                //将线程交给线程池
                Future<Boolean> future = threadPool.submit(downLoadTask);
                //接收返回值
                list.add(future);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 合并分块文件
     * @param path  下载文件路径
     * @return  true 合并成功  false 合并失败
     */
    private boolean merge(String path) {
        System.out.print("\r");
        LogUtils.info("开始合并文件{}", path);
        //为了合并成一个文件,需要按顺序写入到文件尾,所以用RandomAccessFile
        try (RandomAccessFile raf = new RandomAccessFile(path, "rw")) {

            for (int i = 0; i < THREAD_NUM; i++) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path + ".temp" + i))) {
                    int len = -1;
                    byte[] buffer = new byte[BYTE_SIZE];
                    while ((len = bis.read(buffer)) != -1) {
                        raf.write(buffer, 0, len);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LogUtils.error("文件不存在");
            return false;
        } catch (IOException e) {
            LogUtils.error("下载失败");
            return false;
        }
        LogUtils.info("文件合并完成{}", path);
        return true;

    }

    private boolean clearTemp(String path) {
        for (int i = 0; i < THREAD_NUM; i++) {
            File file = new File(path + ".temp" + i);
            file.delete();
        }
        return true;
    }
}
