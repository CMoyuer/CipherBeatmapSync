package cc.moyuer.cipherbeatmapsync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button btnStart, btnStop;
    private TextView txtLog;
    private ScrollView scrollLog;

    public static MessageHandle messageHandle;

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

        messageHandle = new MessageHandle(this);
        messageHandle.bindLogView(txtLog, scrollLog);
        // 输出启动信息
        clearLog();
        addLog("欢迎使用Cipher谱面同步助手", true);
        addLog("软件版本：" + Utils.getVersionName(this), true);
        addLog("推荐使用与Chrome插件一致的版本，以避免奇怪的问题！", true);
        addLog("祝您玩得愉快~", true);
    }

    // ================================ Event ================================
    public void btnStartClick(View v) {
        btnStart.setEnabled(false);
        addLog("正在启动Service...");
        Intent intent = new Intent(MainActivity.this, MainService.class);
        startService(intent);
        btnStop.setEnabled(true);
    }

    public void btnStopClick(View v) {
        btnStop.setEnabled(false);
        addLog("正在停止Service...");
        Intent intent = new Intent(MainActivity.this, MainService.class);
        stopService(intent);
        btnStart.setEnabled(true);
    }

    public void btnCheckUpdate(View v) {
        Uri uri = Uri.parse("https://github.com/CMoyuer/CipherBeatmapSync/releases");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    // ================================= Log =================================
    private void clearLog() {
        Message msg = new Message();
        msg.what = MessageHandle.MSG_LOG_CLEAR;
        messageHandle.sendMessage(msg);
    }

    private void addLog(String str) {
        addLog(str, false);
    }

    private void addLog(String str, boolean noTime) {
        Message msg = new Message();
        msg.what = MessageHandle.MSG_LOG_ADD;
        msg.obj = str;
        msg.arg1 = noTime ? 1 : 0;
        messageHandle.sendMessage(msg);
    }
}