package cc.moyuer.cipherbeatmapsync;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
            autoCreateDirs(file.getPath());
            if(!file.exists())file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(mContent.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断进程是否正在运行
     *
     * @param className
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String className, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            ComponentName service = runningServiceInfo.service;
            String name = service.getClassName();
            if (name.equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将Base64保存为Zip文件
     *
     * @param fileName
     * @param base64
     * @throws Exception
     */
    public static void saveZipToStorage(String fileName, String base64) throws Exception {
        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            String startStr = "data:application/zip;base64,";
            if (!base64.startsWith(startStr))
                throw new Exception("Base64 format error!");
            base64 = base64.substring(startStr.length());
            byte[] compressed = Base64.decode(base64, Base64.DEFAULT);
            File file = new File(Global.externalFilesPath + fileName);
            autoCreateDirs(file.getPath());
            if(!file.exists())file.createNewFile();
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

    public static boolean decompression(String targetFileName, String parent) throws Exception {
        ZipInputStream zIn = new ZipInputStream(new FileInputStream(targetFileName));
        ZipEntry entry;
        File file;
        while ((entry = zIn.getNextEntry()) != null && !entry.isDirectory()) {
            file = new File(parent, entry.getName());
            autoCreateDirs(file.getPath());
            System.out.println(file.getPath());
            if(!file.exists())file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = zIn.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            bos.close();
            System.out.println(file.getAbsolutePath() + " 解压成功");
        }
        return true;
    }

    /**
     * 传入文件路径，自动创建上级目录
     * @param filePath
     */
    public static void autoCreateDirs(String filePath){
        File file = new File(filePath);
        if(file.exists())return;
        String parentDir = file.getParent();
        if(parentDir == null)return;
        File parent = new File(parentDir);
        if(parent.exists())return;
        parent.mkdirs();
    }
}
