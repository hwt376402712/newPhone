package com.yzy.supercleanmaster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yzy.supercleanmaster.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 37640_000 on 2016/11/27.
 */

public class BackCopyActivity  extends Activity{
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backcopy);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        listview=(ListView)findViewById(R.id.log_list);
        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getData()));


    }


    public List<String> getData(){

        SharedPreferences sh = getBaseContext().getSharedPreferences("copy", Context.MODE_WORLD_READABLE);
        Set<String> set= sh.getStringSet("copyLog",new HashSet<String>());

        return new ArrayList<String>(set);

    }

}
