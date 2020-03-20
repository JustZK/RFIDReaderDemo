package com.zk.rfidreaderdemo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.zk.common.utils.LogUtil;
import com.zk.rfid.bean.DeviceInformation;
import com.zk.rfid.callback.DeviceInformationListener;
import com.zk.rfid.ur880.UR880Entrance;
import com.zk.rfidreaderdemo.R;
import com.zk.rfidreaderdemo.activity.HomeActivity;
import com.zk.rfidreaderdemo.adapter.DeviceAdapter;
import com.zk.rfidreaderdemo.databinding.FragmentDeviceInformationBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DeviceInformationFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {
    private final static int REGISTER = 0x01;
    private final static int HEART = 0x02;
    private final static int VERSION = 0x03;
    private final static int REMOVED = 0x04;

    private View mView;
    private FragmentDeviceInformationBinding mBinding;
    private DeviceAdapter mDeviceAdapter;
    private List<DeviceInformation> mDeviceInformationList = new ArrayList<>();
    private DeviceInformation mDeviceInformation;
    private ProgressDialog mGetVersionProgressDialog;
    private boolean mFirstRegister = true;

    private DeviceInformationFragmentHandler mHandler;

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case REGISTER:
            case HEART:
                DeviceInformation deviceInformation = (DeviceInformation) msg.obj;
                if (!mDeviceInformationList.contains(deviceInformation)) {
                    mBinding.deviceInfoDeviceTv.setText("可用设备列表");
                    mDeviceInformationList.add(deviceInformation);
                    mDeviceAdapter.notifyDataSetChanged();
                    if (mFirstRegister){
                        mFirstRegister = false;
                        ((HomeActivity) getActivity()).setDeviceID(deviceInformation.getDeviceID());
                        deviceShow(deviceInformation);
                    }
                }
                break;
            case VERSION:
                if (mGetVersionProgressDialog != null && mGetVersionProgressDialog.isShowing()) {
                    mGetVersionProgressDialog.dismiss();
                }
                Bundle bundle = msg.getData();
                mDeviceInformation.setHardwareVersionNumber(bundle.getString("hardwareVersionNumber"));
                mDeviceInformation.setSoftwareVersionNumber(bundle.getString("softwareVersionNumber"));
                mDeviceInformation.setFirmwareVersionNumber(bundle.getString("firmwareVersionNumber"));
                deviceShow(mDeviceInformation);
                break;
            case REMOVED:
                String deviceID = msg.obj.toString();
                for (DeviceInformation deviceInformation1 : mDeviceInformationList){
                    if (deviceInformation1.getDeviceID().equals(deviceID)){
                        mDeviceInformationList.remove(deviceInformation1);
                        break;
                    }
                }
                mDeviceAdapter.notifyDataSetChanged();
                break;
        }
    }

    public static DeviceInformationFragment newInstance() {
        DeviceInformationFragment fragment = new DeviceInformationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {

            mBinding = DataBindingUtil.inflate(inflater,
                    R.layout.fragment_device_information, container, false);
            mBinding.setOnClickListener(this);
            mBinding.deviceInfoLv.setOnItemClickListener(this);
            mDeviceAdapter = new DeviceAdapter(getActivity(), mDeviceInformationList);
            mBinding.deviceInfoLv.setAdapter(mDeviceAdapter);


            UR880Entrance.getInstance().addOnDeviceInformationListener(mDeviceInformationListener);

            mHandler = new DeviceInformationFragmentHandler(this);

            mView = mBinding.getRoot();
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (null != parent) {
                parent.removeView(mView);
            }
        }
        return mView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.Companion.getInstance().d("position = " + position);
        mDeviceInformation = mDeviceInformationList.get(position);
        ((HomeActivity) getActivity()).setDeviceID(mDeviceInformation.getDeviceID());
        deviceShow(mDeviceInformation);
//        if (mGetVersionProgressDialog == null) {
//            mGetVersionProgressDialog = new ProgressDialog(getContext());
//            mGetVersionProgressDialog.setMessage("正在获取读写器详细信息...");
//        }
//        mGetVersionProgressDialog.show();
//        UR880Entrance.getInstance().send(
//                new UR880SendInfo.Builder().getVersionInformation(mDeviceInformation.getDeviceID()).build());
    }

    private DeviceInformationListener mDeviceInformationListener = new DeviceInformationListener() {
        @Override
        public void registered(String deviceID, String deviceVersionNumber, String deviceRemoteAddress) {
            DeviceInformation deviceInformation = new DeviceInformation();
            deviceInformation.setDeviceID(deviceID);
            deviceInformation.setDeviceVersionNumber(deviceVersionNumber);
            deviceInformation.setDeviceRemoteAddress(deviceRemoteAddress);
            Message message = Message.obtain();
            message.obj = deviceInformation;
            message.what = REGISTER;
            mHandler.sendMessage(message);
        }

        @Override
        public void heartbeat(String deviceID) {
            DeviceInformation deviceInformation = new DeviceInformation();
            deviceInformation.setDeviceID(deviceID);
            Message message = Message.obtain();
            message.obj = deviceInformation;
            message.what = HEART;
            mHandler.sendMessage(message);
        }

        @Override
        public void versionInformation(String hardwareVersionNumber, String softwareVersionNumber,
                                       String firmwareVersionNumber) {
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("hardwareVersionNumber", hardwareVersionNumber);
            bundle.putString("softwareVersionNumber", softwareVersionNumber);
            bundle.putString("firmwareVersionNumber", firmwareVersionNumber);
            message.setData(bundle);
            message.what = VERSION;
            mHandler.sendMessage(message);
        }

        @Override
        public void removed(String deviceID) {
            Message message = Message.obtain();
            message.what = REMOVED;
            message.obj = deviceID;
            mHandler.sendMessage(message);
        }
    };

    private static class DeviceInformationFragmentHandler extends Handler {
        private final WeakReference<DeviceInformationFragment> deviceInformationFragmentWeakReference;

        DeviceInformationFragmentHandler(DeviceInformationFragment deviceInformationFragment) {
            super();
            deviceInformationFragmentWeakReference = new WeakReference<>(deviceInformationFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (deviceInformationFragmentWeakReference.get() != null) {
                deviceInformationFragmentWeakReference.get().handleMessage(msg);
            }
        }
    }

    private void deviceShow(DeviceInformation deviceInformation) {
        if (!TextUtils.isEmpty(deviceInformation.getDeviceID())) {
            mBinding.deviceInfoIdTv.setVisibility(View.VISIBLE);
            mBinding.deviceInfoIdTv.setText(deviceInformation.getDeviceID());
        } else {
            mBinding.deviceInfoIdTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(deviceInformation.getDeviceVersionNumber())) {
            mBinding.deviceInfoVersionTv.setVisibility(View.VISIBLE);
            mBinding.deviceInfoVersionTv.setText(deviceInformation.getDeviceVersionNumber());
        } else {
            mBinding.deviceInfoVersionTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(deviceInformation.getDeviceRemoteAddress())) {
            mBinding.deviceInfoRemoteAddressTv.setVisibility(View.VISIBLE);
            mBinding.deviceInfoRemoteAddressTv.setText(deviceInformation.getDeviceRemoteAddress());
        } else {
            mBinding.deviceInfoRemoteAddressTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(deviceInformation.getHardwareVersionNumber())) {
            mBinding.deviceInfoHardwareTv.setVisibility(View.VISIBLE);
            mBinding.deviceInfoHardwareTv.setText(deviceInformation.getHardwareVersionNumber());
        } else {
            mBinding.deviceInfoHardwareTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(deviceInformation.getSoftwareVersionNumber())) {
            mBinding.deviceInfoSoftwareTv.setVisibility(View.VISIBLE);
            mBinding.deviceInfoSoftwareTv.setText(deviceInformation.getSoftwareVersionNumber());
        } else {
            mBinding.deviceInfoSoftwareTv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(deviceInformation.getFirmwareVersionNumber())) {
            mBinding.deviceInfoFirmwareTv.setVisibility(View.VISIBLE);
            mBinding.deviceInfoFirmwareTv.setText(deviceInformation.getFirmwareVersionNumber());
        } else {
            mBinding.deviceInfoFirmwareTv.setVisibility(View.GONE);
        }

    }

}
