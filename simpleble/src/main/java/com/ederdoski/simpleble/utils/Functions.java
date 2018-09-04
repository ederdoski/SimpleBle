package com.ederdoski.simpleble.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

public class Functions {

    public static boolean isBleSupported(Activity act){
        return act.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static boolean getStatusGps(Activity act){
        LocationManager manager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
