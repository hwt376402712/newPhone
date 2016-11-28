package com.yzy.supercleanmaster.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yzy.supercleanmaster.R;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.InjectView;

/**
 * Created by 37640_000 on 2016/11/27.
 */

public class BackCopyActivity extends Activity {
    private ListView listview;
    private String text;


    View mProgressBar;


    TextView mProgressBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backcopy);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("一键还原");
        mProgressBar=findViewById(R.id.backCopyBar);
        mProgressBarText=(TextView)findViewById(R.id.backCopyBarText);
        listview = (ListView) findViewById(R.id.log_list);
        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getData()));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              text = listview.getItemAtPosition(i).toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(BackCopyActivity.this);
                builder.setTitle("确定恢复" + text + "存档么？").setIcon(
                        R.drawable.gray_drawable).setNegativeButton(
                        "取消", null);
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                showProgressBar(true);
                                mProgressBarText.setText("正在恢复存档");
                                new BackTask().execute();



                            }
                        });
                builder.show();

            }
        });


    }


    public List<String> getData() {

        SharedPreferences sh = getBaseContext().getSharedPreferences("copy", Context.MODE_WORLD_READABLE);
        Set<String> set = sh.getStringSet("copyLog", new HashSet<String>());

        return new ArrayList<String>(set);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class BackTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            //恢复存档
            //给权限
            Process process = null;
            DataOutputStream os = null;
            try {
                String cmd = "chmod 777 " + "/data";
                process = Runtime.getRuntime().exec("su"); //切换到root帐号
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(cmd + "\n");
                os.writeBytes("chmod -R 777 " + "/data/newPhoneData" + "\n");

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

            //遍历备份的目录
            File file = new File("/data/newPhoneData/" + text);
            File[] ff = file.listFiles();


            try {

                process = Runtime.getRuntime().exec("su"); //切换到root帐号
                os = new DataOutputStream(process.getOutputStream());
                for (File f : ff) {
                    String filename = f.getName();
                    os.writeBytes("rm -rf /data/data/"+filename+"/*" + "\n");
                    os.writeBytes("cp -rf /data/newPhoneData/"+text+"/"+filename+"/*  /data/data/"+filename + "\n");

                    os.writeBytes("chmod -R 777 /data/data/"+filename + "\n");


                }

                os.writeBytes("exit\n");
                os.flush();
                process.waitFor();
            } catch (Exception e) {

            }finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    process.destroy();
                } catch (Exception e) {
                }
            }
            return 1;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
                showProgressBar(false);
                Toast.makeText(getBaseContext(),"恢复完毕，请点击内存加速！！",Toast.LENGTH_LONG).show();
                finish();
            }


        }


    }

    private boolean isProgressBarVisible() {
        return mProgressBar.getVisibility() == View.VISIBLE;
    }
    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.startAnimation(AnimationUtils.loadAnimation(
                    getBaseContext(), android.R.anim.fade_out));
            mProgressBar.setVisibility(View.GONE);
        }
    }


}
