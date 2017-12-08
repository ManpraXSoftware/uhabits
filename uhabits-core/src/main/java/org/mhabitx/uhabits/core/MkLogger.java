package org.mhabitx.uhabits.core;

/**
 * Created by Prateek on 08-12-2017.
 */

public class MkLogger {
    public static void m(String tag,String msg){
        System.out.print(""+tag+" : "+msg);
    }
    public static void m(String msg){
        m("MSG==> ",""+msg);
    }
}
