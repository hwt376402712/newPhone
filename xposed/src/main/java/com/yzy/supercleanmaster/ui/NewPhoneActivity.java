package com.yzy.supercleanmaster.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
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
import com.yzy.supercleanmaster.R;
import com.yzy.supercleanmaster.adapter.NewPhoneAdapter;
import com.yzy.supercleanmaster.adapter.RublishMemoryAdapter;
import com.yzy.supercleanmaster.base.BaseSwipeBackActivity;
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
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入此次备份名").setIcon(
                R.drawable.gray_drawable).setView(inputServer).setNegativeButton(
                "取消", null);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        now = inputServer.getText().toString();
                        //开始清除数据并备份选中数据
                        showProgressBar(true);
                        mProgressBarText.setText("正在备份数据...");
                        new NewPhoneTask().execute();

                    }
                });
        builder.show();


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
                    pre.apply();


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
}
