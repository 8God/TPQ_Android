package com.zcmedical.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

public class FileUtil {

    private static final String cacheBaseDirName = "core";
    private static File cacheBaseDir;

    public static File getCacheBaseDir(final Context context) {
        if (null == cacheBaseDir) {
            File baseDir = null;
            if (Build.VERSION.SDK_INT >= 8) {
                baseDir = context.getExternalCacheDir();
            } else {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    baseDir = Environment.getExternalStorageDirectory();
                }
            }

            if (null == baseDir) {
                baseDir = context.getCacheDir();
            }

            if (null != baseDir) {
                cacheBaseDir = new File(baseDir, cacheBaseDirName);
                if (!cacheBaseDir.exists()) {
                    cacheBaseDir.mkdirs();
                }
            } // if (null != baseDir)

        } // if (null == cacheBaseDir) 

        return cacheBaseDir;
    }

    public static File getCacheDir(final Context context, final String cacheDirName) {
        File cacheDir = null;

        cacheBaseDir = getCacheBaseDir(context);
        if (null != cacheBaseDir) {
            if (null != cacheDirName && 0 != cacheDirName.trim().length()) {
                cacheDir = new File(cacheBaseDir, cacheDirName);
            } else {
                cacheDir = cacheBaseDir;
            }
        }

        if (null != cacheDir && !cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                cacheDir = null;
            }
        }

        if (null == cacheDir) {
            cacheDir = cacheBaseDir;
        }

        return cacheDir;
    }

    public static File getCacheFile(final Context context, final String cacheDirName, final String cacheFileName) {
        File cacheFile = null;

        File cacheDir = getCacheDir(context, cacheDirName);
        if (null != cacheDir && null != cacheFileName && 0 != cacheFileName.trim().length()) {
            cacheFile = new File(cacheDir, cacheFileName);
        }

        return cacheFile;
    }

    public static boolean deleteFile(String filePath) {
        File deleteFile = new File(filePath);
        return deleteFile.delete();
    }

    /**
     * 获取文件夹所占空间大小
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 如果遇到目录则通过递归调用继续统计  
            }
        }
        return dirSize;
    }

    public static void deleteFolder(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteFolder(files[i]);
        }
        //        path.delete();  
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (null != inBuff) {
                try {
                    inBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != outBuff) {
                try {
                    outBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件的MineType类型
     * @param fileUrl
     * @return
     */
    public static String getMimeType(String fileUrl) {
        String type = null;
        URL u = null;
        try {
            u = new URL(fileUrl);
            URLConnection uc = null;
            uc = u.openConnection();
            type = uc.getContentType();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return type;
    }
}
