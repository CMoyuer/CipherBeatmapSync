package cc.moyuer.cipherbeatmapsync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.KeepLiveService;

public class MainActivity extends AppCompatActivity {
    public static MessageHandle messageHandle;

    private final static String NAME_MAIN_SERVER = "cc.moyuer.cipherbeatmapsync.MainService";

    private Button btnStart, btnStop;
    private TextView txtLog;
    private ScrollView scrollLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        // 横屏
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 绑定组件
        btnStart = this.findViewById(R.id.btnStart);
        btnStop = this.findViewById(R.id.btnStop);
        txtLog = this.findViewById(R.id.txtLog);
        scrollLog = this.findViewById(R.id.scrollLog);
        // 初始化
        messageHandle = new MessageHandle(this);
        messageHandle.bindLogView(txtLog, scrollLog);
        Global.Init(this);
        // 输出启动信息
        Global.clearLog();
        Global.addLog("欢迎使用Cipher谱面同步助手", true);
        Global.addLog("软件版本：" + Utils.getVersionName(this), true);
        Global.addLog("推荐使用与Chrome插件一致的版本，以避免奇怪的问题！", true);
        Global.addLog("祝您玩得愉快~", true);
        // 保活
        startKeepAlive();
    }

    // ================================ Button ================================
    public void btnStartClick(View v) {
        if (!checkPermission()) return;
        btnStart.setEnabled(false);
        Global.running = true;
        startService();
        btnStop.setEnabled(true);
    }

    public void btnStopClick(View v) {
        btnStop.setEnabled(false);
        Global.running = false;
        stopService();
        btnStart.setEnabled(true);
    }

    public void btnCheckUpdate(View v) {
        Uri uri = Uri.parse("https://github.com/CMoyuer/CipherBeatmapSync/releases");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    // ================================ Service ================================

    private void startService() {
        if (!Utils.isServiceRunning(NAME_MAIN_SERVER, this)) {
            Global.addLog("正在启动Service...");
            Intent intent = new Intent(MainActivity.this, MainService.class);
            startService(intent);
        }
    }

    private void stopService() {
        if (Utils.isServiceRunning(NAME_MAIN_SERVER, this)) {
            Global.addLog("正在停止Service...");
            Intent intent = new Intent(MainActivity.this, MainService.class);
            stopService(intent);
        }
    }

    // ================================ Permission ================================
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
            return false;
        }
        return true;
    }

    // ================================ Service ================================
    private void startKeepAlive() {
        //定义前台服务的默认样式。即标题、描述和图标
        ForegroundNotification foregroundNotification = new ForegroundNotification("闪韵谱面同步助手", "正在后台运行", R.mipmap.ic_launcher,
                //定义前台服务的通知点击事件
                (context, intent) -> {
                    System.out.println("用户点击了通知栏");
                });
        //启动保活服务
        KeepLive.startWork(getApplication(), KeepLive.RunMode.ENERGY, foregroundNotification,
                new KeepLiveService() {
                    @Override
                    public void onWorking() {
                        if (Global.running)
                            startService();
                    }

                    @Override
                    public void onStop() {

                    }
                }
        );
    }
}