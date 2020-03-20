package com.zk.rfidreaderdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zk.rfid.bean.DeviceInformation;
import com.zk.rfidreaderdemo.R;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {
    private List<DeviceInformation> list;
    private Context mContext;
    private LayoutInflater inflater;

    public DeviceAdapter(Context mContext, List<DeviceInformation> list) {
        this.mContext = mContext;
        this.list = list;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return (list == null ? 0 : list.size());
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        DeviceInformation deviceInformation = list.get(i);
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_device_item, null);
            viewHolder = new ViewHolder(
                    (TextView) view.findViewById(R.id.adapter_device_id_tv),
                    (TextView) view.findViewById(R.id.adapter_device_software_version_tv));
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.adapter_device_id_tv.setText(deviceInformation.getDeviceID());
        if (!TextUtils.isEmpty(deviceInformation.getDeviceVersionNumber())) {
            viewHolder.adapter_device_software_version_tv.setText(deviceInformation.getDeviceVersionNumber());
        } else {
            viewHolder.adapter_device_software_version_tv.setText("点击获取");
        }
        return view;
    }

    private class ViewHolder {
        protected TextView adapter_device_id_tv;
        protected TextView adapter_device_software_version_tv;

        public ViewHolder(TextView adapter_device_id_tv,
                          TextView adapter_device_software_version_tv) {
            this.adapter_device_id_tv = adapter_device_id_tv;
            this.adapter_device_software_version_tv = adapter_device_software_version_tv;
        }
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    public void setList (List<DeviceInformation> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
