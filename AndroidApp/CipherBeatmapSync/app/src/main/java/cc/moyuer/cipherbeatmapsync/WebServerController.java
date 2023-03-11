package cc.moyuer.cipherbeatmapsync;

import android.os.Environment;
import android.os.Message;
import android.widget.Toast;

import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;

import org.json.JSONObject;

import java.io.File;

@RestController
public class WebServerController {
    @PostMapping("/upload_beatmap")
    JSONObject upload_beatmap(@RequestParam("base64") String base64, @RequestParam("name") String fileName) {
        int code = 200;
        String message = "success";
        // 处理文件
        String filePath = Global.externalFilesPath + fileName;
        String tarPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Android/data/com.bytedance.cipher/files/Ciphermap/CustomLevels/";
        try {
            // 保存
            Utils.saveZipToStorage(filePath, base64);
            // 删除旧文件夹
            File oldPath = new File(tarPath + fileName.replace(".zip", ""));
            if (oldPath.exists()) Utils.delFileUnRoot(oldPath);
            // 解压
            Utils.decompression(filePath, oldPath.getPath());
            // 成功
            Global.addLog("从编辑器中获取到文件：" + fileName);
        } catch (Exception e) {
            Global.addLog("获取文件失败：" + e.getMessage());
            e.printStackTrace();
            code = 500;
            message = e.getMessage();
        } finally {
            try {
                File file = new File(filePath);
                if (file.exists()) file.delete();
            } catch (Exception ignored) {
            }
        }
        // 回复
        JSONObject result = new JSONObject();
        try {
            result.put("code", code);
            result.put("message", message);
            result.put("data", new JSONObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
