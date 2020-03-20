package com.zk.rfidreaderdemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.zk.common.utils.LogUtil;
import com.zk.rfid.bean.DeviceInformation;
import com.zk.rfid.ur880.UR880Entrance;
import com.zk.rfidreaderdemo.R;
import com.zk.rfidreaderdemo.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPortFinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding mActivityMainBinding;
    private int mConnectionType;
    private String[] serialPathItems;
    private boolean[] serialPathCheckedItems;
    private List<DeviceInformation> mDeviceInformationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mActivityMainBinding.setOnClickListener(this);

        LogUtil.Companion.getInstance().setLogSwitch(true);
        LogUtil.Companion.getInstance().init(this);
        LogUtil.Companion.getInstance().d("Main onCreate");
        initView();
    }

    private void initView() {
        mConnectionType = UR880Entrance.CONNECTION_TCP_IP;
        mActivityMainBinding.mainSerialPathLl.setVisibility(View.GONE);
        mActivityMainBinding.mainSerialBaudRateLl.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_connection_type_tv:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.connection_type))
                        .setSingleChoiceItems(R.array.connection_type_array, (mConnectionType - 1), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mConnectionType = i + 1;
                                if (mConnectionType == UR880Entrance.CONNECTION_TCP_IP) {
                                    mActivityMainBinding.mainConnectionTypeTv.setText(R.string.connection_type_1);
                                    mActivityMainBinding.mainSerialPathLl.setVisibility(View.GONE);
                                    mActivityMainBinding.mainSerialBaudRateLl.setVisibility(View.GONE);
                                    mActivityMainBinding.mainServerPortLl.setVisibility(View.VISIBLE);
                                } else if (mConnectionType == UR880Entrance.CONNECTION_SERIAL) {
                                    mActivityMainBinding.mainConnectionTypeTv.setText(R.string.connection_type_2);
                                    mActivityMainBinding.mainSerialPathLl.setVisibility(View.VISIBLE);
                                    mActivityMainBinding.mainSerialBaudRateLl.setVisibility(View.VISIBLE);
                                    mActivityMainBinding.mainServerPortLl.setVisibility(View.GONE);
                                }
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
                break;
            case R.id.main_serial_path_tv:
                if (serialPathItems == null) {
//                    serialPathItems = getResources().getStringArray(R.array.connection_type_array);
                    serialPathItems = new SerialPortFinder().getAllDevicesPath();
                    serialPathCheckedItems = new boolean[serialPathItems.length];
                    mDeviceInformationList = new ArrayList<>();
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.serial_path))
                        .setMultiChoiceItems(serialPathItems, serialPathCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                serialPathCheckedItems[i] = b;
                            }
                        })
                        .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDeviceInformationList.clear();
                                String serialPathTv = "";
                                for (int serialPathChecked = 0; serialPathChecked <
                                        serialPathCheckedItems.length; serialPathChecked++){
                                    if (serialPathCheckedItems[serialPathChecked]){
                                        DeviceInformation deviceInformation = new DeviceInformation();
                                        deviceInformation.setDeviceSerialPath(serialPathItems[serialPathChecked]);
                                        mDeviceInformationList.add(deviceInformation);
                                        if (serialPathTv.equals("")) {
                                            serialPathTv = serialPathTv + serialPathItems[serialPathChecked];
                                        } else {
                                            serialPathTv = serialPathTv + "\n" + serialPathItems[serialPathChecked];
                                        }
                                    }
                                }
                                mActivityMainBinding.mainSerialPathTv.setText(serialPathTv);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
                break;
            case R.id.main_sure_btn:
                if (mConnectionType == UR880Entrance.CONNECTION_TCP_IP){
                    String serverPort = mActivityMainBinding.mainServerPortEt.getText().toString().trim();
                    if (!TextUtils.isEmpty(serverPort)){
                        UR880Entrance.getInstance().init(mConnectionType, Integer.parseInt(serverPort), null);
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, R.string.fill_complete, Toast.LENGTH_SHORT).show();
                    }
                } else if (mConnectionType == UR880Entrance.CONNECTION_SERIAL){
                    String serialBaudRate = mActivityMainBinding.mainSerialBaudRateEt.getText().toString().trim();
                    if (!TextUtils.isEmpty(serialBaudRate) && mDeviceInformationList!= null && mDeviceInformationList.size() > 0){
                        for (DeviceInformation deviceInformation : mDeviceInformationList){
                            deviceInformation.setDeviceSerialBaudRate(serialBaudRate);
                        }
                        UR880Entrance.getInstance().init(mConnectionType, null, mDeviceInformationList);
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, R.string.fill_complete, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
