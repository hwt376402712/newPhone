package com.yzy.supercleanmaster.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Lrxc on 2016/11/17.
 */

public class LrxcXposed implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //获得Sharedpreference保存的数据
        XSharedPreferences pre = new XSharedPreferences("com.yzy.supercleanmaster", "prefs");
        Log.d("pre", "初始化xposed成功");
        HookMethod(TelephonyManager.class, "getDeviceId",
                randomNum(20));

        HookMethod(TelephonyManager.class, "getSubscriberId", randomNum(15));
        HookMethod(TelephonyManager.class, "getLine1Number", randomPhone());
        HookMethod(TelephonyManager.class, "getSimSerialNumber",
                randomNum(20));
        HookMethod(WifiInfo.class, "getMacAddress", randomMac());
        HookMethod(BluetoothAdapter.class, "getAddress",  randomMac1());

           XposedHelpers.findField(android.os.Build.class, "SERIAL").set(null,  randomNum(19)+"a");
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
                                        param.setResult(randomABC(16));
                                    }

                                }

                            } });
        } catch (Throwable e)
        {
            Log.d("tt","修改androidId失败"+e.getMessage());
        }



        String imei = pre.getString("imei", null);
        Log.d("pre", "handleLoadPackage() returned: " + imei);
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


    private String randomNum(int n)
    {
        String res = "";
        Random rnd = new Random();
        for (int i = 0; i < n; i++)
        {
            res = res + rnd.nextInt(10);
        }
        return res;
    }


    private String randomPhone()
    {
        /** 前三为 */
        String head[] = { "+8613", "+8615", "+8617", "+8618", "+8616" };
        Random rnd = new Random();
        String res = head[rnd.nextInt(head.length)];
        for (int i = 0; i < 9; i++)
        {
            res = res + rnd.nextInt(10);
        }
        return res;
    }

    private String randomMac()
    {
        String chars = "abcde0123456789";
        String res = "";
        Random rnd = new Random();
        int leng = chars.length();
        for (int i = 0; i < 17; i++)
        {
            if (i % 3 == 2)
            {
                res = res + ":";
            } else
            {
                res = res + chars.charAt(rnd.nextInt(leng));
            }

        }
        return res;
    }

    private String randomMac1()
    {
        String chars = "ABCDE0123456789";
        String res = "";
        Random rnd = new Random();
        int leng = chars.length();
        for (int i = 0; i < 17; i++)
        {
            if (i % 3 == 2)
            {
                res = res + ":";
            } else
            {
                res = res + chars.charAt(rnd.nextInt(leng));
            }

        }
        return res;
    }

    private String randomABC(int n)
    {
        String chars = "abcde0123456789";
        String res = "";
        Random rnd = new Random();
        int leng = chars.length();
        for (int i = 0; i < n; i++)
        {
            res = res + chars.charAt(rnd.nextInt(leng));

        }
        return res;
    }



}
