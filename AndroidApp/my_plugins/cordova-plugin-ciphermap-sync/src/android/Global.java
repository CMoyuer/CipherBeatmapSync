package cc.moyuer.ciphermap_sync_helper;

import android.content.Context;
import android.os.Message;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

public class Global {
    public static String externalFilesPath = "";
    public static boolean running = false;
    public static CallbackContext callbackContext = null;

    public static void Init(Context context) {
        externalFilesPath = context.getExternalFilesDir("").getPath() + "/";
    }

    public static void sendCallback(String event) {
        sendCallback(event,null);
    }
    public static void sendCallback(String event,String message) {
        if (callbackContext == null) return;
        try{
            JSONObject json = new JSONObject();
            json.put("event",event);
            json.put("message",message);

            PluginResult pr = new PluginResult(PluginResult.Status.OK, json);
            pr.setKeepCallback(true);
            callbackContext.sendPluginResult(pr);
        }catch (Exception ignored){}
    }
}
