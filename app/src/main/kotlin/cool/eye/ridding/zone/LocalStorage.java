package cool.eye.ridding.zone;

import java.io.File;

/**
 * @author ycb
 * @version 1.0
 * @date 2014-9-20
 * @category 文件夹构建类
 */
public class LocalStorage {


    /**
     * 文件夹根目录
     */

    protected static final String IMAGE = "image"; // 大图
    protected static final String LOCAL_DIR = "riding";

    protected static final String FILE = "file";
    protected static final String IMAGE_SUFF = ".jpg";

    // create file storage path
    public static String composeFile() {
        StringBuilder sb = new StringBuilder();
        sb.append(composeFileDir()).append(File.separator).append("loction")
                .append(".txt");
        return sb.toString();
    }

    // create file storage dir
    public static String composeFileDir() {
        StringBuilder sb = new StringBuilder();
        sb.append(android.os.Environment.getExternalStorageDirectory());
        sb.append(File.separator);
        sb.append(LOCAL_DIR);
        sb.append(File.separator);
        sb.append(FILE);
        return sb.toString();
    }

    // create image storage path
    public static String composeImage(String res) {
        StringBuilder sb = new StringBuilder();
        sb.append(android.os.Environment.getExternalStorageDirectory())
                .append(File.separator).append(LOCAL_DIR)
                .append(File.separator).append(IMAGE).append(File.separator)
                .append(res).append(IMAGE_SUFF);
        return sb.toString();
    }

    // create image storage dir
    public static String composeImageDir() {
        StringBuilder sb = new StringBuilder();
        sb.append(android.os.Environment.getExternalStorageDirectory());
        sb.append(File.separator);
        sb.append(LOCAL_DIR);
        sb.append(File.separator);
        sb.append(IMAGE);
        return sb.toString();
    }
}
