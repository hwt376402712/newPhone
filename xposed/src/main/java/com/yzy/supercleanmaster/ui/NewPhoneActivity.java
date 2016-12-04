package com.yzy.supercleanmaster.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.utils.Base64Coder;
import com.utils.RandomUtil;
import com.yzy.supercleanmaster.R;
import com.yzy.supercleanmaster.adapter.NewPhoneAdapter;
import com.yzy.supercleanmaster.adapter.RublishMemoryAdapter;
import com.yzy.supercleanmaster.base.BaseSwipeBackActivity;
import com.yzy.supercleanmaster.dialogs.CustomDialog;
import com.yzy.supercleanmaster.model.CacheListItem;
import com.yzy.supercleanmaster.model.StorageSize;
import com.yzy.supercleanmaster.service.CleanerService;
import com.yzy.supercleanmaster.service.CoreService;
import com.yzy.supercleanmaster.utils.StorageUtil;
import com.yzy.supercleanmaster.utils.SystemBarTintManager;
import com.yzy.supercleanmaster.utils.UIElementsHelper;
import com.yzy.supercleanmaster.widget.textcounter.CounterView;
import com.yzy.supercleanmaster.widget.textcounter.formatters.DecimalFormatter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 37640_000 on 2016/11/23.
 */

public class NewPhoneActivity extends BaseSwipeBackActivity implements OnDismissCallback, CleanerService.OnActionListener {

    ActionBar ab;
    protected static final int SCANING = 5;

    protected static final int SCAN_FINIFSH = 6;
    protected static final int PROCESS_MAX = 8;
    protected static final int PROCESS_PROCESS = 9;

    private static final int INITIAL_DELAY_MILLIS = 300;
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    Resources res;
    int ptotal = 0;
    int pprocess = 0;

    String now;
    private CleanerService mCleanerService;

     private CustomDialog diaLog;



    private boolean mAlreadyScanned = false;
    private boolean mAlreadyCleaned = false;

    @InjectView(R.id.listview)
    ListView mListView;

    @InjectView(R.id.empty)
    TextView mEmptyView;

    @InjectView(R.id.header)
    RelativeLayout header;


    @InjectView(R.id.sufix)
    TextView sufix;

    @InjectView(R.id.progressBar)
    View mProgressBar;
    @InjectView(R.id.progressBarText)
    TextView mProgressBarText;

    NewPhoneAdapter rublishMemoryAdapter;

    List<CacheListItem> mCacheListItem = new ArrayList<>();

    @InjectView(R.id.bottom_lin)
    LinearLayout bottom_lin;

    @InjectView(R.id.clear_button)
    Button clearButton;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanerService = ((CleanerService.CleanerServiceBinder) service).getService();
            mCleanerService.setOnActionListener(NewPhoneActivity.this);

            //  updateStorageUsage();

            if (!mCleanerService.isScanning() && !mAlreadyScanned) {
                mCleanerService.scanCache();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCleanerService.setOnActionListener(null);
            mCleanerService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newphone);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //     applyKitKatTranslucency();

//        StikkyHeaderBuilder.stickTo(mListView).setHeader(header)
//                .minHeightHeaderPixel(0).build();
        res = getResources();


        int footerHeight = mContext.getResources().getDimensionPixelSize(R.dimen.footer_height);

        mListView.setEmptyView(mEmptyView);
        rublishMemoryAdapter = new NewPhoneAdapter(mContext, mCacheListItem);
        mListView.setAdapter(rublishMemoryAdapter);
        mListView.setOnItemClickListener(rublishMemoryAdapter);
        mListView.setOnScrollListener(new QuickReturnListViewOnScrollListener(QuickReturnType.FOOTER, null, 0, bottom_lin, footerHeight));
        bindService(new Intent(mContext, CleanerService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDismiss(@NonNull ViewGroup viewGroup, @NonNull int[] ints) {

    }

    @Override
    public void onScanStarted(Context context) {
        mProgressBarText.setText(R.string.scanning);
        showProgressBar(true);
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {
        mProgressBarText.setText(getString(R.string.scanning_m_of_n, current, max));

    }

    @Override
    public void onScanCompleted(Context context, List<CacheListItem> apps) {
        showProgressBar(false);
        mCacheListItem.clear();
        mCacheListItem.addAll(apps);
        rublishMemoryAdapter.notifyDataSetChanged();
        header.setVisibility(View.GONE);
        if (apps.size() > 0) {
            header.setVisibility(View.VISIBLE);
            bottom_lin.setVisibility(View.VISIBLE);

            long medMemory = mCleanerService != null ? mCleanerService.getCacheSize() : 0;

            StorageSize mStorageSize = StorageUtil.convertStorageSize(medMemory);

        } else {
            header.setVisibility(View.GONE);
            bottom_lin.setVisibility(View.GONE);
        }

        if (!mAlreadyScanned) {
            mAlreadyScanned = true;

        }


    }

    @Override
    public void onCleanStarted(Context context) {
        if (isProgressBarVisible()) {
            showProgressBar(false);
        }

        if (!NewPhoneActivity.this.isFinishing()) {
            showDialogLoading();
        }
    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {
        dismissDialogLoading();
        Toast.makeText(context, context.getString(R.string.cleaned, Formatter.formatShortFileSize(
                mContext, cacheSize)), Toast.LENGTH_LONG).show();
        header.setVisibility(View.GONE);
        bottom_lin.setVisibility(View.GONE);
        mCacheListItem.clear();
        rublishMemoryAdapter.notifyDataSetChanged();
    }


    /**
     * Apply KitKat specific translucency.
     */
    private void applyKitKatTranslucency() {

        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setNavigationBarTintEnabled(true);
            // mTintManager.setTintColor(0xF00099CC);

            mTintManager.setTintDrawable(UIElementsHelper
                    .getGeneralActionBarBackground(this));

            getActionBar().setBackgroundDrawable(
                    UIElementsHelper.getGeneralActionBarBackground(this));

        }

    }


    @OnClick(R.id.clear_button)
    public void onClickClear() {
//只给使用到12月9号

       new GetOnlineTime().execute();

        CustomDialog.Builder customBuilder = new
                CustomDialog.Builder(this);
        customBuilder.setTitle("请输入备份名")
                .setMessage("提示内容")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setOnclickPositiveButn("确定", new CustomDialog.Builder.OnclickPositiveButn() {
                    @Override
                    public void clickPositiveBtn(String editText) {
                        now = editText+new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        //开始清除数据并备份选中数据
                        showProgressBar(true);
                        mProgressBarText.setText("正在备份数据...");
                        new NewPhoneTask().execute();
                    }
                });
        diaLog = customBuilder.create();
        diaLog.show();










    }


    private class GetOnlineTime extends AsyncTask<Void, Void, Integer> {


        @Override
        protected Integer doInBackground(Void... voids) {
            try{
                String webUrl1 = "http://www.baidu.com";//bjTime
                String getTime=getWebsiteDatetime(webUrl1);
                if("2016-12-09".compareTo(getTime)<0){
                    return 0;

                }
            }catch(Exception e){
                return 3;


            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if(result == 0){
                Toast.makeText(getBaseContext(),"已经到期",Toast.LENGTH_SHORT).show();
                System.exit(0);

            }
            else if(result==3){
                Toast.makeText(getBaseContext(),"请链接网络",Toast.LENGTH_SHORT).show();
                System.exit(0);

            }



        }


    }

    private class NewPhoneTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(Void... params) {

            for (int i = mCacheListItem.size() - 1; i >= 0; i--) {
                if (mCacheListItem.get(i).checked) {
                    SharedPreferences sh = getBaseContext().getSharedPreferences("copy", Context.MODE_WORLD_READABLE);
                    SharedPreferences.Editor pre = sh.edit();
                    Set<String> s=sh.getStringSet("copyLog",new HashSet<String>());
                    s.add(now);
                    pre.putStringSet("copyLog",s);
                    //记录设备当前标识

                    TelephonyManager telManager=    ((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
                    WifiManager wifi = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
                    BluetoothAdapter blue=  BluetoothAdapter.getDefaultAdapter();
                    WifiInfo info = wifi.getConnectionInfo();
                    String imei=telManager.getDeviceId();
                    String subId=telManager.getSubscriberId();
                    String lineId=telManager.getLine1Number();
                    String simId=telManager.getSimSerialNumber();
                    String macId=info.getMacAddress();
                    String blueId= blue.getAddress();
                    String androidId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);


                    Map map=new HashMap();
                    map.put("imei",imei);
                    map.put("subId",subId);
                    map.put("lineId",lineId);
                    map.put("simId",simId);
                    map.put("macId",macId);
                    map.put("blueId",blueId);
                    map.put("androidId",androidId);

                    try {
                        //将map转换为byte[]
                        ByteArrayOutputStream toByte = new ByteArrayOutputStream();
                        ObjectOutputStream oos = null;
                        oos = new ObjectOutputStream(toByte);
                        oos.writeObject(map);
//对byte[]进行Base64编码
                        String payCityMapBase64 = new String(Base64Coder.encode(toByte.toByteArray()));
                        pre.putString(now, payCityMapBase64);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pre.apply();




                    //设备授权
                    Process process = null;
                    DataOutputStream os = null;
                    try {
                        String cmd = "chmod 777 " + "/data";
                        process = Runtime.getRuntime().exec("su"); //切换到root帐号
                        os = new DataOutputStream(process.getOutputStream());
                        os.writeBytes(cmd + "\n");
                        os.writeBytes("chmod 777 " + "/data/data" + "\n");
                        os.writeBytes("chmod -R 777 " + "/data/data/" + mCacheListItem.get(i).getPackageName() + "\n");

                        //删除前做备份操作


                        os.writeBytes("cd  /data" + "\n");
                        os.writeBytes("mkdir  newPhoneData " + "\n");


                        os.writeBytes("cd /data/newPhoneData" + "\n");
                        os.writeBytes("mkdir " + now + "\n");

                        os.writeBytes("cp -r /data/data/" + mCacheListItem.get(i).getPackageName() + "  /data/newPhoneData/" + now + "\n");
                        os.writeBytes("rm -rf /data/data/"+ mCacheListItem.get(i).getPackageName()+"/*"+ "\n");

                        os.writeBytes("exit\n");
                        os.flush();
                        process.waitFor();
                    } catch (Exception e) {

                    } finally {
                        try {
                            if (os != null) {
                                os.close();
                            }
                            process.destroy();

                            //设置新的设备标识
                            SharedPreferences sp = getBaseContext().getSharedPreferences("prefs", Context.MODE_WORLD_READABLE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("imei",RandomUtil.randomNum(20));
                            edit.putString("subId",RandomUtil.randomNum(15));
                            edit.putString("lineId",RandomUtil.randomPhone());
                            edit.putString("simId",RandomUtil.randomNum(20));
                            edit.putString("macId",RandomUtil.randomMac());
                            edit.putString("blueId",RandomUtil.randomMac1());
                            edit.putString("androidId",RandomUtil.randomABC(16));
                            edit.apply();


                        } catch (Exception e) {
                        }
                    }
//
//
////
//                    File file = new File("/data/data/" + mCacheListItem.get(i).getPackageName());
//                    Log.d("tt", file.getPath());
//
//
//                    deleteAll(file, process, file.getPath());


                }
            }


            return 1;

        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
                showProgressBar(false);
                Toast.makeText(NewPhoneActivity.this.getBaseContext(), "手机已经重置,请记得点击内存加速！！", Toast.LENGTH_LONG).show();

            }


        }
    }


    public void deleteAll(File file, Process process, String gen) {


        if (file.isFile() || file.list().length == 0) {
            file.delete();

        } else {


                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {

                    deleteAll(files[i], process, gen);
                    files[i].delete();
                }


                if (file.exists() && !file.getPath().equals(gen)) {
                    file.delete();

                }




        }
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);


    }


    private boolean isProgressBarVisible() {
        return mProgressBar.getVisibility() == View.VISIBLE;
    }

    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.startAnimation(AnimationUtils.loadAnimation(
                    mContext, android.R.anim.fade_out));
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }


    private static String getWebsiteDatetime(String webUrl){
        try {
            URL url = new URL(webUrl);// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间
            Date date = new Date(ld);// 转换为标准时间对象
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);// 输出北京时间
            return sdf.format(date);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
