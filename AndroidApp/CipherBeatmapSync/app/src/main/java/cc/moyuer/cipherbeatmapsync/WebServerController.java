package cc.moyuer.cipherbeatmapsync;

import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import org.json.JSONObject;

@RestController
public class WebServerController {
    @PostMapping("/upload_beatmap")
    JSONObject beatmap_sync(@RequestParam("file") MultipartFile file, @RequestParam("name") String fileName) {
        System.out.println(file);
        System.out.println(fileName);
        JSONObject result = new JSONObject();
        try {
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", new JSONObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
