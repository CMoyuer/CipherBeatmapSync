package cc.moyuer.cipherbeatmapsync;

import android.os.Environment;

import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;

import org.json.JSONObject;

@RestController
public class WebServerController {
    @PostMapping("/upload_beatmap")
    JSONObject upload_beatmap(@RequestParam("base64") String base64, @RequestParam("name") String fileName) {
        int code = 200;
        String message = "success";
        // 处理
        try {
            Utils.saveZipToStorage(fileName, base64);
            Global.addLog("从编辑器中获取到文件：" + fileName);
            Utils.decompression(Global.externalFilesPath + fileName, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/Download/");
        }catch (Exception e){
            Global.addLog("同步失败：" + e.getMessage());
            e.printStackTrace();
            code = 500;
            message = e.getMessage();
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
