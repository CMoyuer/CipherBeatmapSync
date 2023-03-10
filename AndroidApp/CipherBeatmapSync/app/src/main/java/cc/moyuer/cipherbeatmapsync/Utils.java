package cc.moyuer.cipherbeatmapsync;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Utils {
    /**
     * 获取自己应用内部的版本名
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 获取WIFI的IP地址
     */
    public static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();
            return (ipInt & 0xFF) + "." +
                    ((ipInt >> 8) & 0xFF) + "." +
                    ((ipInt >> 16) & 0xFF) + "." +
                    ((ipInt >> 24) & 0xFF);
        } catch (Exception ex) {
            return "127.0.0.1";
        }
    }

    /**
     * 写TXT文件
     *
     * @return 是否成功
     */
    public static boolean writeTxtToStorage(String mFileName, String mContent) {
        File file = new File(Global.externalFilesPath + mFileName);
        FileOutputStream fos;
        try {
            if (!file.exists()) {
                String parentPath = file.getParent();
                if (parentPath != null) {
                    File parentDir = new File(parentPath);
                    if (!parentDir.exists())
                        parentDir.mkdirs();
                }
            }
            fos = new FileOutputStream(file);
            fos.write(mContent.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveZipToStorage(String fileName, String base64) throws Exception{
        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            String startStr = "data:application/zip;base64,";
            if (!base64.startsWith(startStr))
                throw new Exception("Base64 format error!");
            base64 = base64.substring(startStr.length());
            byte[] compressed = Base64.decode(base64, Base64.DEFAULT);
            File file = new File(Global.externalFilesPath + fileName);
            if (!file.exists()) {
                String parentPath = file.getParent();
                if (parentPath != null) {
                    File parentDir = new File(parentPath);
                    if (!parentDir.exists())
                        parentDir.mkdirs();
                }
            }
            outputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(compressed);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            outputStream.close();
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception ignored) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
