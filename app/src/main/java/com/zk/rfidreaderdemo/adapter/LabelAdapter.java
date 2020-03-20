package com.zk.rfidreaderdemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zk.rfid.bean.LabelInfo;
import com.zk.rfidreaderdemo.R;

import java.util.List;

public class LabelAdapter extends BaseAdapter {
    private List<LabelInfo> list;
    private Context mContext;
    private LayoutInflater inflater;

    public LabelAdapter(Context mContext, List<LabelInfo> list) {
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
        LabelInfo labelInfo = list.get(i);
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_label_item, null);
            viewHolder = new ViewHolder(
                    (TextView) view.findViewById(R.id.adapter_label_antenna_number_tv),
                    (TextView) view.findViewById(R.id.adapter_label_rssi_tv),
                    (TextView) view.findViewById(R.id.adapter_label_time_tv),
                    (TextView) view.findViewById(R.id.adapter_label_epc_length_tv),
                    (TextView) view.findViewById(R.id.adapter_label_epc_tv),
                    (TextView) view.findViewById(R.id.adapter_label_tid_tv),
                    (TextView) view.findViewById(R.id.adapter_label_inventory_number_tv));
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.adapter_label_antenna_number_tv.setText(String.valueOf(labelInfo.getAntennaNumber()));
        viewHolder.adapter_label_rssi_tv.setText(String.valueOf(labelInfo.getRSSI()));
        viewHolder.adapter_label_time_tv.setText(TextUtils.isEmpty(labelInfo.getOperatingTime()) ? "---" : labelInfo.getOperatingTime());
        viewHolder.adapter_label_epc_length_tv.setText(String.valueOf(labelInfo.getEPCLength()));
        viewHolder.adapter_label_epc_tv.setText(TextUtils.isEmpty(labelInfo.getEPC()) ? "---" : labelInfo.getEPC());
        viewHolder.adapter_label_tid_tv.setText(TextUtils.isEmpty(labelInfo.getTID()) ? "---" : labelInfo.getTID());
        viewHolder.adapter_label_inventory_number_tv.setText(String.valueOf(labelInfo.getInventoryNumber()));
        return view;
    }

    private class ViewHolder {
        TextView adapter_label_antenna_number_tv;
        TextView adapter_label_rssi_tv;

        TextView adapter_label_time_tv;
        TextView adapter_label_epc_length_tv;

        TextView adapter_label_epc_tv;
        TextView adapter_label_tid_tv;

        TextView adapter_label_inventory_number_tv;

        ViewHolder(TextView adapter_label_antenna_number_tv,
                          TextView adapter_label_rssi_tv,
                          TextView adapter_label_time_tv,
                          TextView adapter_label_epc_length_tv,
                          TextView adapter_label_epc_tv,
                          TextView adapter_label_tid_tv,
                          TextView adapter_label_inventory_number_tv) {
            this.adapter_label_antenna_number_tv = adapter_label_antenna_number_tv;
            this.adapter_label_rssi_tv = adapter_label_rssi_tv;

            this.adapter_label_time_tv = adapter_label_time_tv;
            this.adapter_label_epc_length_tv = adapter_label_epc_length_tv;

            this.adapter_label_epc_tv = adapter_label_epc_tv;
            this.adapter_label_tid_tv = adapter_label_tid_tv;

            this.adapter_label_inventory_number_tv = adapter_label_inventory_number_tv;

        }
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    public void setList(List<LabelInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
