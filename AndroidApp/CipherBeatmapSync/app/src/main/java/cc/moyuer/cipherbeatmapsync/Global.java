package cc.moyuer.cipherbeatmapsync;

import android.content.Context;

public class Global {
    public static String externalFilesPath = "";

    public static void Init(Context context){
        externalFilesPath = context.getExternalFilesDir("/").getPath();
    }
}
