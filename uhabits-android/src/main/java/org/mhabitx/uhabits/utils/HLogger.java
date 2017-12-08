package org.mhabitx.uhabits.utils;

import android.util.Log;

/**
 * Created by Prateek on 08-12-2017.
 */

public class HLogger {
    public static void m(String tag,String msg){
        //System.out.print(""+tag+" : "+msg);
        Log.w(""+tag,""+msg);
    }
    public static void m(String msg){
        m("MSG==> ",""+msg);
    }
}
