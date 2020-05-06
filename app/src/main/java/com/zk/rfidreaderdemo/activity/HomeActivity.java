package com.zk.rfidreaderdemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.zk.common.utils.AppVersionUtil;
import com.zk.rfid.ur880.UR880Entrance;
import com.zk.rfidreaderdemo.R;
import com.zk.rfidreaderdemo.adapter.FragmentAdapter;
import com.zk.rfidreaderdemo.databinding.ActivityHomeBinding;
import com.zk.rfidreaderdemo.fragment.DeviceInformationFragment;
import com.zk.rfidreaderdemo.fragment.LabelInventoryFragment;
import com.zk.rfidreaderdemo.fragment.LabelSettingsFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private final static int CONNECT = 0x01;

    private ActivityHomeBinding mActivityHomeBinding;
    private FragmentAdapter mFragmentAdapter;
    private String mDeviceID;

    private HomeActivityHandler homeActivityHandler;
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case CONNECT:
                UR880Entrance.getInstance().connect();
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        mActivityHomeBinding.honeTitleTv.setText("RFID Reader Demo V" + AppVersionUtil.INSTANCE.appVersionNameForShow(this));

        initView();

        homeActivityHandler = new HomeActivityHandler(this);
        homeActivityHandler.sendEmptyMessageDelayed(CONNECT, 1000);
    }

    private void initView(){
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.device_info_tab_txt));
        titles.add(getString(R.string.label_inventory_tab_txt));
//        titles.add(getString(R.string.label_operation_tab_txt));
        titles.add(getString(R.string.label_settings_tab_txt));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(DeviceInformationFragment.newInstance());
        fragments.add(LabelInventoryFragment.newInstance());
//        fragments.add(LabelOperationFragment.newInstance());
        fragments.add(LabelSettingsFragment.newInstance());

        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mActivityHomeBinding.honeViewPager.setAdapter(mFragmentAdapter);
        mActivityHomeBinding.honeTabs.setupWithViewPager(mActivityHomeBinding.honeViewPager);
    }

    private static class HomeActivityHandler extends Handler {
        private final WeakReference<HomeActivity> homeActivityWeakReference;

        HomeActivityHandler(HomeActivity homeActivity) {
            super();
            homeActivityWeakReference = new WeakReference<>(homeActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (homeActivityWeakReference.get() != null) {
                homeActivityWeakReference.get().handleMessage(msg);
            }
        }
    }

    public String getDeviceID() {
        return mDeviceID;
    }

    public void setDeviceID(String deviceID) {
        mDeviceID = deviceID;
        ((LabelInventoryFragment) mFragmentAdapter.getItem(1)).setDeviceID(deviceID);
        ((LabelSettingsFragment) mFragmentAdapter.getItem(2)).setDeviceID(deviceID);
    }

    @Override
    protected void onDestroy() {
        UR880Entrance.getInstance().disConnect();
        super.onDestroy();
    }
}