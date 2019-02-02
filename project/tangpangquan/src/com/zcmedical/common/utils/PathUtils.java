package com.zcmedical.common.utils;

import java.io.File;

import android.os.Environment;
import android.text.TextUtils;

import com.zcmedical.common.base.TpqApplication;

/**
 * <b>ClassName:</b> PathUtils.java </br>
 * <b>Description:</b> 应用路径辅助类 </br>
 * <b>Usage:</b> </br>
 * <b>Create Date:</b> 2014-6-26 </br>
 * <b>Update Date:</b> 2014-6-26 </br>
 * <b>Creator:</b> issaclam </br>
 * <b>Updator:</b> issaclam </br>
 */
public class PathUtils {

    /** 程序名称 */
    public static String APPNAME = "tangpangquan";
    /** html目录 */
    public static final String HTML = "/html";
    /** 图片目录 */
    public static final String IMAGE = "/image";
    /** 音乐目录 */
    public static final String MUSIC = "/music";
    /** 下载目录 */
    public static final String DOWNLOAD = "/download";

    /** 获取应用程序目录 */
    public static File getAppDirectory() {
        if (TextUtils.isEmpty(APPNAME)) {
            return null;
        }
        File rootDirectory = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            rootDirectory = new File(Environment.getExternalStorageDirectory(), APPNAME);
            rootDirectory.mkdirs();
        }

        if (rootDirectory == null) {
            rootDirectory = TpqApplication.getInstance().getFilesDir();
        }

        return rootDirectory;
    }

    /** 获取图片目录 */
    public static File getImageDirectory() {
        File file = new File(getAppDirectory(), IMAGE);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /** 获取下载目录 */
    public static File getDownloadDirectory() {
        File file = new File(getAppDirectory(), DOWNLOAD);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 查询，如果不存在就创建目录
     * @param parent
     *          {@link File} 父目录,可由方法 {@code PathUtils}.getAppDirectory()获取应用程序目录
     * @param dirName
     *          {@link File} 要创建的目录名
     * @return
     *          {@link File}
     */
    public static File findOrCreateDir(File parent, String dirName) {
        File directory = new File(parent, dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }
}
