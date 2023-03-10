package cc.moyuer.cipherbeatmapsync;

import android.util.Base64;

import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

@RestController
public class WebServerController {
    @PostMapping("/upload_beatmap")
    JSONObject beatmap_sync(@RequestParam("base64") String base64, @RequestParam("name") String fileName) {
        int code = 200;
        String message = "success";
        // 处理
        try {
            Utils.saveZipToStorage(fileName, base64);
        }catch (Exception e){
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
