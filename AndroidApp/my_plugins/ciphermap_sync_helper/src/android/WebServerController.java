package cc.moyuer.ciphermap_sync_helper;

import android.os.Environment;
import android.util.Log;

import com.yanzhenjie.andserver.annotation.CrossOrigin;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.Random;

@RestController
public class WebServerController {
    private static final String TAG = "ciphermap_sync_server";
    private static final String AGREEMENT_VERSION = "2.0.0";

    @CrossOrigin()
    @GetMapping("/version")
    JSONObject version() {
        JSONObject data = new JSONObject();
        try {
            data.put("agreement", AGREEMENT_VERSION);
        } catch (Exception e) {
        }
        return getMessage(200, "success", data);
    }

    @CrossOrigin()
    @PostMapping("/upload_beatmap")
    JSONObject upload_beatmap(@RequestParam("base64") String base64, @RequestParam("name") String fileName) {
        int code = 200;
        String message = "success";
        // 处理文件
        String zipPath = Global.externalFilesPath + new Date().getTime() + "-" + new Random().nextInt(10000) + ".zip";
        String tarDir = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Android/data/com.bytedance.cipher/files/Ciphermap/CustomLevels/";
        try {
            // 保存
            Utils.saveZipToStorage(zipPath, base64);
            // 删除旧文件夹
            File oldPath = new File(tarDir + fileName);
            if (oldPath.exists()) Utils.delFileUnRoot(oldPath);
            // 解压
            Utils.decompression(zipPath, oldPath.getPath());
            // 成功
            Global.sendCallback("received_ciphermap", fileName);
        } catch (Exception e) {
            e.printStackTrace();
            code = 500;
            message = e.getMessage();
        } finally {
            try {
                File file = new File(zipPath);
                if (file.exists()) file.delete();
            } catch (Exception ignored) {
            }
        }

        return getMessage(code, message, new JSONObject());
    }

    // ============================= Pack =============================
    private JSONObject getMessage(int code, String message, JSONObject data) {
        // 回复
        JSONObject result = new JSONObject();
        try {
            result.put("code", code);
            result.put("message", message);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}