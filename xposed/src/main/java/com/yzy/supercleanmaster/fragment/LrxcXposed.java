package com.yzy.supercleanmaster.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.utils.RandomUtil;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Lrxc on 2016/11/17.
 */

public class LrxcXposed implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //获得Sharedpreference保存的数据
        Log.d("pre", "初始化xposed成功");
        XposedBridge.log("初始化xposed成功");
      final  XSharedPreferences pre = new XSharedPreferences("com.yzy.supercleanmaster", "prefs");

        HookMethod(TelephonyManager.class, "getDeviceId",
                pre.getString("imei",RandomUtil.randomNum(20)));

        HookMethod(TelephonyManager.class, "getSubscriberId", pre.getString("subId",RandomUtil.randomNum(15)));
        HookMethod(TelephonyManager.class, "getLine1Number",pre.getString("lineId",RandomUtil.randomPhone()));
        HookMethod(TelephonyManager.class, "getSimSerialNumber",pre.getString("simId",RandomUtil.randomNum(20)));
        HookMethod(WifiInfo.class, "getMacAddress", pre.getString("macId",RandomUtil.randomMac()));
        HookMethod(BluetoothAdapter.class, "getAddress", pre.getString("blueId",RandomUtil.randomMac1()));
        XposedHelpers.findField(android.os.Build.class, "SERIAL").set(null,  RandomUtil.randomNum(19)+"a");
//            XposedHelpers.findField(android.os.Build.class, "BRAND").set(null,  randomNum(15));

        try
        {
            XposedHelpers.findAndHookMethod(
                    android.provider.Settings.Secure.class, "getString",
                    new Object[] { ContentResolver.class, String.class,
                            new XC_MethodHook()
                            {
                                protected void afterHookedMethod(
                                        MethodHookParam param) throws Throwable
                                {
                                    if (param.args[1] == "android_id")
                                    {
                                        param.setResult( pre.getString("androidId",RandomUtil.randomABC(16)));
                                    }

                                }

                            } });
        } catch (Throwable e)
        {
            Log.d("tt","修改androidId失败"+e.getMessage());
        }

    }

    private void HookMethod(final Class cl, final String method,
                            final String result) {
        try {
            XposedHelpers.findAndHookMethod(cl, method, new Object[]{new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    Log.d("pre", "被我劫持了");
                    param.setResult(result);
                }

            }});
        } catch (Throwable e) {
        }
    }





}
