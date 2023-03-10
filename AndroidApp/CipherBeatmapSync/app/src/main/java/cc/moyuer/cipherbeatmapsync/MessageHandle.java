package cc.moyuer.cipherbeatmapsync;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MessageHandle extends Handler {
    private Activity activity;
    private TextView txtLog;
    private ScrollView scrollLog;

    public static final int MSG_LOG_ADD = 1000;
    public static final int MSG_LOG_CLEAR = 1001;

    public MessageHandle(Activity activity) {
        this.activity = activity;
    }

    public void bindLogView(TextView textView, ScrollView scrollView) {
        this.txtLog = textView;
        this.scrollLog = scrollView;
    }

    @Override
    public void handleMessage(final @NonNull Message msg) {
        activity.runOnUiThread(() -> {
            switch (msg.what) {
                case MSG_LOG_ADD:
                    log_add((String) msg.obj, msg.arg1 != 0);
                    break;
                case MSG_LOG_CLEAR:
                    log_clear();
                    break;
            }
        });
    }

    // ==================== LOG ====================
    private final List<String> logList = new ArrayList<>();

    public void log_add(String str, boolean noTime) {
        if (!noTime) {
            str = "[" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA).format(new Date())) + "]" + str;
        }
        if (logList.size() > 50) logList.remove(0);
        logList.add(str);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                scrollLog.post(() -> scrollLog.fullScroll(ScrollView.FOCUS_DOWN));
            }
        }, 10);

        String logStr = "";
        for (String _str : logList) {
            if (!logStr.isEmpty()) logStr += "\n";
            logStr += _str;
        }
        txtLog.setText(logStr);
    }

    public void log_clear() {
        logList.clear();
        txtLog.setText("");
    }

}
