package util;

import java.io.File;

/**
 * @author zzzZqy
 * @Description
 * @create 2021-11-07 18:58
 */
public class FileUtils {

    public static long getExistFileLength(String path){
        File file = new File(path);
        return file.isFile() && file.exists() ? file.length() : -1;
    }
}
