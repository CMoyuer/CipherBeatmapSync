package cc.moyuer.ciphermap_sync_helper;

import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

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
                start(args, cb);
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

    private void start(JSONArray args, CallbackContext cb) throws JSONException {
        if (Utils.isServiceRunning(NAME_MAIN_SERVER, cordova.getActivity())) {
            cb.error("Server already running!");
            return;
        }
        try {
            Intent intent = new Intent(cordova.getActivity(), MainService.class);
            cordova.getActivity().startService(intent);
            cb.success();
        } catch (Exception e) {
            cb.error(e.getMessage());
        }
    }

    private void stop(CallbackContext cb) {
        if (Utils.isServiceRunning(NAME_MAIN_SERVER, cordova.getActivity())) {
            Intent intent = new Intent(cordova.getActivity(), MainService.class);
            cordova.getActivity().stopService(intent);
        }
        cb.success();
    }

    private void bindCallback(CallbackContext cb) {
        Global.callbackContext = cb;
    }

    /*===================================*/
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
}
