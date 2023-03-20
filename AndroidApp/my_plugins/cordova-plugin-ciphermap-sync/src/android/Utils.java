package cc.moyuer.ciphermap_sync_helper;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Region;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
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
     * @param filePath
     * @param base64
     * @throws Exception
     */
    public static void saveZipToStorage(String filePath, String base64) throws Exception {
        FileOutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
//            String startStr = "data:application/zip;base64,";
//            String startStr = "data:application/x-zip-compressed;base64,";
            Pattern pattern = Pattern.compile("^data:application/(x-)?zip(-compressed)?;base64,");
            if (!pattern.matcher(base64).find())
                throw new Exception("Base64 format error!");
            base64 = base64.substring(base64.indexOf(";base64,") + 8);
            byte[] compressed = Base64.decode(base64, Base64.DEFAULT);
            File file = new File(filePath);
            autoCreateDirs(file.getPath());
            if (!file.exists()) file.createNewFile();
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

    public static void decompression(String targetFileName, String parent) throws Exception {
        ZipInputStream zIn = new ZipInputStream(new FileInputStream(targetFileName));
        ZipEntry entry;
        File file;
        while ((entry = zIn.getNextEntry()) != null && !entry.isDirectory()) {
            file = new File(parent, entry.getName());
            autoCreateDirs(file.getPath());
            System.out.println(file.getPath());
            if (!file.exists()) file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = zIn.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
            }
            bos.close();
        }
    }

    /**
     * 传入文件路径，自动创建上级目录
     *
     * @param filePath
     */
    public static void autoCreateDirs(String filePath) {
        File file = new File(filePath);
        if (file.exists()) return;
        String parentDir = file.getParent();
        if (parentDir == null) return;
        File parent = new File(parentDir);
        if (parent.exists()) return;
        parent.mkdirs();
    }

    /***
     * 删除文件或者文件夹
     * @param f
     * @return 成功 返回true, 失败返回false
     */
    public static void delFileUnRoot(File f) throws Exception {
        if (null == f || !f.exists())
            return;
        Stack<File> tmpFileStack = new Stack<>();
        tmpFileStack.push(f);

        while (!tmpFileStack.isEmpty()) {
            File curFile = tmpFileStack.pop();
            if (null == curFile) {
                continue;
            }
            if (curFile.isFile()) {
                if (!curFile.delete()) {
                    throw new Exception("delete fail: " + curFile.getPath());
                }
            } else {
                File[] tmpSubFileList = curFile.listFiles();
                if (null == tmpSubFileList || 0 == tmpSubFileList.length) {
                    if (!curFile.delete()) {
                        throw new Exception("delete fail: " + curFile.getPath());
                    }
                } else {
                    tmpFileStack.push(curFile);
                    for (File item : tmpSubFileList)
                        tmpFileStack.push(item);
                }
            }
        }
    }
}
