package cc.moyuer.cipherbeatmapsync;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import org.json.JSONObject;

import java.io.IOException;
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
        new Thread() {
            @Override
            public void run() {
                createSslContext();
            }
        }.start();
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
            if (responseBody != null) {
                String resultStr = responseBody.string();
                JSONObject result = new JSONObject(resultStr);
                int code = result.getInt("code");
                if (code != 200) throw new Exception("HTTP CODE: " + code);
                System.out.println("同步DDNS信息成功：" + resultStr);
            } else {
                throw new Exception("No Data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            addLog("同步IP信息失败：" + e.getMessage());
        }
    }

    // ========================== SslContext ==========================

    private void createSslContext() {
        // 通过在线接口生成证书
        JSONObject data;
        try {
            data = new JSONObject("{\"inputDomain\":\"" +
                    Utils.getLocalIpAddress(getApplicationContext())
                    + "\",\"algorithm\":\"rsa\",\"keyLength\":\"2048\",\"hash\":\"SHA256WITHRSAENCRYPTION\",\"storeType\":\"PKCS12\",\"storePassword\":\"moyuer\",\"alias\":\"ssl-demo\",\"company\":\"\",\"department\":\"\",\"email\":\"\",\"country\":\"CN\",\"province\":\"\",\"city\":\"\",\"useMore\":false}");
        } catch (Exception e) {
            e.printStackTrace();
            addLog("获取SSL证书失败：" + e.getMessage());
            return;
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://www.lddgo.net/api/SslGenerate")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String resultStr = responseBody.string();
                JSONObject result = new JSONObject(resultStr);
                int code = result.getInt("code");
                if (code != 0) throw new Exception(result.getString("msg"));
                result = result.getJSONObject("data");
                Utils.writeTxtToStorage(this, "ssl/cert.pem", result.getString("fullChain"));
                Utils.writeTxtToStorage(this, "ssl/private.key", result.getString("key"));
                System.out.println("获取SSL证书成功：" + result);
            } else {
                throw new Exception("No Data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            addLog("获取SSL证书失败：" + e.getMessage());
        }
    }

    // ========================== WebServer ==========================
    private Server webServer;

    private void createSocketServer() {
        webServerShutdown();

        webServer = AndServer.webServer(getApplicationContext())
                .port(25521)
                // .sslContext(sslContext)
                .timeout(10, TimeUnit.SECONDS)
                .listener(webServerListener)
                .build();
        webServer.startup();
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