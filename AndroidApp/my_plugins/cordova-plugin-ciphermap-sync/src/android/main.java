package cc.moyuer.ciphermap_sync_helper;

import android.content.Intent;

import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.KeepLiveService;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import cc.moyuer.ciphermapsync.R;

public class main extends CordovaPlugin {
    static main self;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Global.Init(cordova.getContext().getApplicationContext());
        self = this;
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext cb) throws JSONException {
        switch (action) {
            case "start":
                start(cb);
                break;
            case "stop":
                stop(cb);
                break;
            case "bindCallback":
                bindCallback(cb);
                break;
            default:
                return false;
        }
        return true;
    }

    private final String NAME_MAIN_SERVER = "cc.moyuer.ciphermap_sync_helper.MainService";

    private void start(CallbackContext cb) {
        Global.running = true;
        if (Utils.isServiceRunning(NAME_MAIN_SERVER, cordova.getActivity())) {
            if (cb != null) cb.error("Server already running!");
            return;
        }
        startKeepAlive();
        try {
            Intent intent = new Intent(cordova.getActivity(), MainService.class);
            cordova.getActivity().startService(intent);
            if (cb != null) cb.success();
        } catch (Exception e) {
            if (cb != null) cb.error(e.getMessage());
        }
    }

    private void stop(CallbackContext cb) {
        Global.running = false;
        if (Utils.isServiceRunning(NAME_MAIN_SERVER, cordova.getActivity())) {
            Intent intent = new Intent(cordova.getActivity(), MainService.class);
            cordova.getActivity().stopService(intent);
        }
        cb.success();
    }

    private void bindCallback(CallbackContext cb) {
        Global.callbackContext = cb;
    }

    // ================================ Event ================================

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // ================================ KeepAlive ================================

    private void startKeepAlive() {
        //定义前台服务的默认样式。即标题、描述和图标
        ForegroundNotification foregroundNotification = new ForegroundNotification("闪韵谱面同步助手", "正在后台运行", R.mipmap.ic_launcher,
                //定义前台服务的通知点击事件
                (context, intent) -> {
//                    System.out.println("用户点击了通知栏");
                });
        //启动保活服务
        KeepLive.startWork(cordova.getActivity().getApplication(), KeepLive.RunMode.ENERGY, foregroundNotification,
                new KeepLiveService() {
                    @Override
                    public void onWorking() {
                        if (!Global.running) return;
                        Global.sendCallback("server_reboot");
                        start(null);
                    }

                    @Override
                    public void onStop() {

                    }
                }
        );
    }
}
