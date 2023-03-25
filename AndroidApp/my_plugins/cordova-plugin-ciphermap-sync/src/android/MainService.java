package cc.moyuer.ciphermap_sync_helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class MainService extends Service {
    private static final String TAG = "ciphermap_sync_service";
    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        timer = new Timer();
        createSocketServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer = null;
        webServerShutdown();
        super.onDestroy();
    }

    // ========================== WebServer ==========================
    private Server webServer;

    private void createSocketServer() {
        webServerShutdown();
        try {
            webServer = AndServer.webServer(getApplicationContext())
                    .inetAddress(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}))
                    .port(Config.LISTEN_PORT)
                    .timeout(10, TimeUnit.SECONDS)
                    .listener(webServerListener)
                    .build();
            webServer.startup();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "createSocketServer: ", e);
        }
    }

    private void webServerShutdown() {
        if (webServer == null || !webServer.isRunning()) return;
        webServer.shutdown();
        webServer = null;
    }

    private final Server.ServerListener webServerListener = new Server.ServerListener() {

        @Override
        public void onStarted() {
            Global.sendCallback("server_started");
            Log.i(TAG, "onStarted");
        }

        @Override
        public void onStopped() {
            Global.sendCallback("server_stopped");
            Log.i(TAG, "onStopped");
        }

        @Override
        public void onException(Exception e) {
            e.printStackTrace();
            Global.sendCallback("server_exception", e.getMessage());
        }
    };
}