package com.utils;

import java.util.Random;

/**
 * Created by admin on 2016/11/28.
 */

public class RandomUtil {


    public static String randomNum(int n)
    {
        String res = "";
        Random rnd = new Random();
        for (int i = 0; i < n; i++)
        {
            res = res + rnd.nextInt(10);
        }
        return res;
    }


    public static String randomPhone()
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

    public  static String randomMac()
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

    public static String randomMac1()
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

    public static String randomABC(int n)
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
