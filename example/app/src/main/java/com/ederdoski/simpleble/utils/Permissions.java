package com.ederdoski.simpleble.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class Permissions {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermisionStatus(Activity act, String permission){
            if(act.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                return false;
            }
    }
}
