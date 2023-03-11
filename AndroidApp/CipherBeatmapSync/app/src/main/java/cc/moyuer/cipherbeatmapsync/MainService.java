package cc.moyuer.cipherbeatmapsync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainService extends Service {
    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update_ddns();
            }
        }, 0, 60 * 1000);
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

    // ========================== LAN_DDNS ==========================
    public static final MediaType JSON = MediaType.get("application/json");

    private void update_ddns() {
        JSONObject data = new JSONObject();
        try {
            data.put("lan_ip", Utils.getLocalIpAddress(getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
            addLog("同步IP信息失败：" + e.getMessage());
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.moyuer.cc/lan_ddns")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String resultStr = responseBody.string();
            JSONObject result = new JSONObject(resultStr);
            int code = result.getInt("code");
            if (code != 200) throw new Exception("HTTP CODE: " + code);
            System.out.println("同步DDNS信息成功：" + resultStr);
        } catch (Exception e) {
            e.printStackTrace();
            addLog("同步IP信息失败：" + e.getMessage());
        }
    }

    // ========================== WebServer ==========================
    private Server webServer;

    private void createSocketServer() {
        webServerShutdown();
        try{
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null,null,null);
            webServer = AndServer.webServer(getApplicationContext())
                    .port(25521)
//                    .sslContext(sslContext)
                    .timeout(10, TimeUnit.SECONDS)
                    .listener(webServerListener)
                    .build();
            webServer.startup();
        }catch (Exception e){
            e.printStackTrace();
            addLog("错误：" + e.getMessage());
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
            String localIP = Utils.getLocalIpAddress(getApplicationContext());
            addLog("Service已启动：" + localIP + ":" + webServer.getPort());
        }

        @Override
        public void onStopped() {
            addLog("Service已停止");
        }

        @Override
        public void onException(Exception e) {
            e.printStackTrace();
            addLog("错误：" + e.getMessage());
        }
    };

    // ========================== MSG ==========================
    private void addLog(String str) {
        Message msg = new Message();
        msg.what = MessageHandle.MSG_LOG_ADD;
        msg.obj = str;
        MainActivity.messageHandle.sendMessage(msg);
    }
}