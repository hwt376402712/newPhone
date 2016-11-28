package com.yzy.supercleanmaster.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.yzy.supercleanmaster.R;
import com.yzy.supercleanmaster.base.BaseFragment;
import com.yzy.supercleanmaster.model.SDCardInfo;
import com.yzy.supercleanmaster.ui.AutoStartManageActivity;
import com.yzy.supercleanmaster.ui.BackCopyActivity;
import com.yzy.supercleanmaster.ui.MemoryCleanActivity;
import com.yzy.supercleanmaster.ui.NewPhoneActivity;
import com.yzy.supercleanmaster.ui.RubbishCleanActivity;
import com.yzy.supercleanmaster.ui.SoftwareManageActivity;
import com.yzy.supercleanmaster.utils.AppUtil;
import com.yzy.supercleanmaster.utils.StorageUtil;
import com.yzy.supercleanmaster.widget.circleprogress.ArcProgress;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainFragment extends BaseFragment {

    @InjectView(R.id.arc_store)
    ArcProgress arcStore;

    @InjectView(R.id.arc_process)
    ArcProgress arcProcess;
    @InjectView(R.id.capacity)
    TextView capacity;

    Context mContext;

    private Timer timer;
    private Timer timer2;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);
        mContext = getActivity();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        fillData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        UmengUpdateAgent.update(getActivity());
    }

    private void fillData() {
        // TODO Auto-generated method stub
        timer = null;
        timer2 = null;
        timer = new Timer();
        timer2 = new Timer();


        long l = AppUtil.getAvailMemory(mContext);
        long y = AppUtil.getTotalMemory(mContext);
        final double x = (((y - l) / (double) y) * 100);
        //   arcProcess.setProgress((int) x);

        arcProcess.setProgress(0);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (arcProcess.getProgress() >= (int) x) {
                            timer.cancel();
                        } else {
                            arcProcess.setProgress(arcProcess.getProgress() + 1);
                        }

                    }
                });
            }
        }, 50, 20);

        SDCardInfo mSDCardInfo = StorageUtil.getSDCardInfo();
        SDCardInfo mSystemInfo = StorageUtil.getSystemSpaceInfo(mContext);

        long nAvailaBlock;
        long TotalBlocks;
        if (mSDCardInfo != null) {
            nAvailaBlock = mSDCardInfo.free + mSystemInfo.free;
            TotalBlocks = mSDCardInfo.total + mSystemInfo.total;
        } else {
            nAvailaBlock = mSystemInfo.free;
            TotalBlocks = mSystemInfo.total;
        }

        final double percentStore = (((TotalBlocks - nAvailaBlock) / (double) TotalBlocks) * 100);

        capacity.setText(StorageUtil.convertStorage(TotalBlocks - nAvailaBlock) + "/" + StorageUtil.convertStorage(TotalBlocks));
        arcStore.setProgress(0);

        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (arcStore.getProgress() >= (int) percentStore) {
                            timer2.cancel();
                        } else {
                            arcStore.setProgress(arcStore.getProgress() + 1);
                        }

                    }
                });
            }
        }, 50, 20);


    }

    @OnClick(R.id.card1)
    void speedUp() {
        startActivity(MemoryCleanActivity.class);
    }


    @OnClick(R.id.card2)
    void rubbishClean() {
        startActivity(RubbishCleanActivity.class);
    }


    @OnClick(R.id.card3)
    void AutoStartManage() {
        startActivity(AutoStartManageActivity.class);
    }

    @OnClick(R.id.card4)
    void SoftwareManage() {
        startActivity(SoftwareManageActivity.class);
    }

    @OnClick(R.id.backcopy)
    void backcopy() {
        startActivity(BackCopyActivity.class);
    }


    @OnClick(R.id.clearBtn)
    void clear() {


        startActivity(NewPhoneActivity.class);

    }

    @OnClick(R.id.arc_process)
    void showTest() {
        String imei=((TelephonyManager)MainFragment.this.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        Toast.makeText(MainFragment.this.getContext(),imei,Toast.LENGTH_SHORT).show();

    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onDestroy() {
        timer.cancel();
        timer2.cancel();
        super.onDestroy();
    }

    /**
     * 随机生成n位数字
     *
     * @param n
     * @return
     */
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

    /**
     *
     * @return
     */
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
