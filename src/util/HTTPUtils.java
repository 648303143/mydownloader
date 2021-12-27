package util;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author zzzZqy
 * @Description
 * @create 2021-11-07 18:48
 */
public class HTTPUtils {
    /**
     * 获取分块HttpURLConnection链接对象
     *
     * @param url 下载地址
     * @return 分块HttpURLConnection链接对象
     * @throws IOException
     */
    public static HttpURLConnection getHttpURLConnection(String url, long startPos, long endPos) throws IOException {
        HttpURLConnection httpURLConnection = getHttpURLConnection(url);

        LogUtils.info("下载的区间是{} - {}", startPos, endPos);
        if (endPos != 0) {
            httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);
        } else {
            httpURLConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-");
        }
        return httpURLConnection;
    }

    /**
     * 获取下载文件大小
     * @param url 下载地址
     * @return 下载文件大小
     * @throws IOException
     */
    public static long getHttpFileLength(String url) throws IOException {
        HttpURLConnection httpURLConnection = null;
        int contentLength = 0;
        try {
            httpURLConnection = getHttpURLConnection(url);
            contentLength = httpURLConnection.getContentLength();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return contentLength;
    }

    /**
     * 获取HttpURLConnection链接对象
     *
     * @param url 下载地址
     * @return HttpURLConnection链接对象
     * @throws IOException
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL httpUrl = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
        return urlConnection;
    }

    /**
     * 获取下载文件名
     *
     * @param url 下载地址
     * @return 下载文件名
     */
    public static String getHttpFileName(String url) {
        int index = url.lastIndexOf("/");
        return url.substring(index + 1);
    }
}
