package cc.moyuer.cipherbeatmapsync;

import android.content.Context;
import android.os.Message;

public class Global {
    public static String externalFilesPath = "";
    public static boolean running = false;

    public static void Init(Context context){
        externalFilesPath = context.getExternalFilesDir("/").getPath();
    }

    // ================================= Log =================================
    public static void clearLog() {
        Message msg = new Message();
        msg.what = MessageHandle.MSG_LOG_CLEAR;
        MainActivity.messageHandle.sendMessage(msg);
    }

    public static void addLog(String str) {
        addLog(str, false);
    }

    public static void addLog(String str, boolean noTime) {
        Message msg = new Message();
        msg.what = MessageHandle.MSG_LOG_ADD;
        msg.obj = str;
        msg.arg1 = noTime ? 1 : 0;
        MainActivity.messageHandle.sendMessage(msg);
    }

}
