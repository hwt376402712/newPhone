//package com.example.app;
//
//import android.telephony.TelephonyManager;
//import android.util.Log;
//
//import de.robv.android.xposed.IXposedHookLoadPackage;
//import de.robv.android.xposed.XC_MethodHook;
//import de.robv.android.xposed.XSharedPreferences;
//import de.robv.android.xposed.XposedHelpers;
//import de.robv.android.xposed.callbacks.XC_LoadPackage;
//
///**
// * Created by Lrxc on 2016/11/17.
// */
//
//public class LrxcXposed implements IXposedHookLoadPackage {
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        //获得Sharedpreference保存的数据
//        XSharedPreferences pre = new XSharedPreferences("com.yzy.supercleanmaster.fragment", "prefs");
//        Log.d("pre", "初始化xposed成功");
//        HookMethod(TelephonyManager.class, "getDeviceId",
//                pre.getString("imei", null));
//        String imei = pre.getString("imei", null);
//        Log.d("pre", "handleLoadPackage() returned: " + imei);
//    }
//
//    private void HookMethod(final Class cl, final String method,
//                            final String result) {
//        try {
//            XposedHelpers.findAndHookMethod(cl, method, new Object[]{new XC_MethodHook() {
//                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param)
//                        throws Throwable {
//                    Log.d("pre", "被我劫持了");
//                    param.setResult(result);
//                }
//
//            }});
//        } catch (Throwable e) {
//        }
//    }
//}
