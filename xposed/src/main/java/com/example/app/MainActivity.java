//package com.example.app;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//public class MainActivity extends AppCompatActivity {
//    private TelephonyManager phone;
//    private Button btn;
//    private EditText edt;
//
//    private Button btnshow;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        phone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        edt = (EditText) findViewById(R.id.edt);
//        btn = (Button) findViewById(R.id.btn);
//
//        btnshow=(Button) findViewById(R.id.btn_show);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                saveData();
//            }
//        });
//
//        btnshow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Toast.makeText(MainActivity.this,phone.getDeviceId(),Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //读取当前设备的IMEI
//        try {
//            edt.setText(phone.getDeviceId().toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void saveData() {
//        try {
//            SharedPreferences sh = this.getSharedPreferences("prefs", Context.MODE_WORLD_READABLE);
//            SharedPreferences.Editor pre = sh.edit();
//            pre.putString("imei", edt.getText().toString());
//            pre.apply();
//            Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
//}
