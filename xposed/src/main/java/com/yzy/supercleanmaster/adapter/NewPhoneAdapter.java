package com.yzy.supercleanmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yzy.supercleanmaster.R;
import com.yzy.supercleanmaster.bean.AppProcessInfo;
import com.yzy.supercleanmaster.model.CacheListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 37640_000 on 2016/11/23.
 */

public class NewPhoneAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{

    public List<CacheListItem> mlistAppInfo;
    LayoutInflater infater = null;
    private Context mContext;
    public static List<Integer> clearIds;

    public NewPhoneAdapter(Context context, List<CacheListItem> apps) {
        infater = LayoutInflater.from(context);
        mContext = context;
        clearIds = new ArrayList<Integer>();
        this.mlistAppInfo = apps;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mlistAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlistAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewPhoneAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = infater.inflate(R.layout.listview_memory_clean,
                    parent, false);
            holder = new NewPhoneAdapter.ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.name);

            holder.cb = (RadioButton) convertView
                    .findViewById(R.id.choice_radio);

            convertView.setTag(holder);
        } else {
            holder = (NewPhoneAdapter.ViewHolder) convertView.getTag();
        }
        final CacheListItem appInfo = (CacheListItem) getItem(position);
        holder.appIcon.setImageDrawable(appInfo.getApplicationIcon());
        holder.appName.setText(appInfo.getApplicationName());
        if (appInfo.checked) {
            holder.cb.setChecked(true);
        } else {
            holder.cb.setChecked(false);
        }
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInfo.checked) {
                    appInfo.checked = false;
                } else {
                    appInfo.checked = true;
                }
                notifyDataSetChanged();
            }
        });

        holder.appName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInfo.checked) {
                    appInfo.checked = false;
                } else {
                    appInfo.checked = true;
                }
                notifyDataSetChanged();
            }
        });




        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewPhoneAdapter.ViewHolder viewHolder = (NewPhoneAdapter.ViewHolder) view.getTag();

//        if (viewHolder != null && viewHolder.packageName != null) {
//            Intent intent = new Intent();
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.setData(Uri.parse("package:" + viewHolder.packageName));
//
//            mContext.startActivity(intent);
//        }
    }

    class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView size;
        RadioButton cb;

        String packageName;
    }

}
