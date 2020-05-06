package com.zk.rfidreaderdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.zk.common.utils.LogUtil;
import com.zk.rfid.bean.UR880SendInfo;
import com.zk.rfid.callback.FactorySettingListener;
import com.zk.rfid.ur880.UR880Entrance;
import com.zk.rfidreaderdemo.R;
import com.zk.rfidreaderdemo.activity.HomeActivity;
import com.zk.rfidreaderdemo.databinding.FragmentLabelSettingsBinding;

import java.lang.ref.WeakReference;

public class LabelSettingsFragment extends Fragment implements View.OnClickListener {
    private final static int SET_ANTENNA_CONFIGURATION = 0x01;
    private final static int GET_ANTENNA_CONFIGURATION = 0x02;
    private final static int GET_ANTENNA_STANDING_WAVE_RADIO = 0x03;
    private final static int SET_GPO_OUTPUT_STATUS = 0x04;
    private final static int GET_GPI_OUTPUT_STATUS = 0x05;
    private final static int SET_BUZZER_STATUS_SETTING = 0x06;
    private final static int GET_BUZZER_STATUS_SETTING = 0x07;
    private final static int TIME_SYNCHRONIZATION = 0x08;
    private final static int DEVICE_RESTART = 0x09;

    private String mDeviceID = null;

    private View mView;
    private FragmentLabelSettingsBinding mBinding;

    private LabelSettingsFragmentHandler mHandler;

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case GET_ANTENNA_CONFIGURATION:
                Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                Bundle bundleGetAntenna = msg.getData();
                mBinding.labelSettingsAntennaEnableZeroSw.setChecked(bundleGetAntenna.getInt("antennaEnableZero") == 1);
                mBinding.labelSettingsAntennaEnableOneSw.setChecked(bundleGetAntenna.getInt("antennaEnableOne") == 1);
                mBinding.labelSettingsAntennaEnableTwoSw.setChecked(bundleGetAntenna.getInt("antennaEnableTwo") == 1);
                mBinding.labelSettingsAntennaEnableThreeSw.setChecked(bundleGetAntenna.getInt("antennaEnableThree") == 1);
                mBinding.labelSettingsAntennaPowerZeroEt.setText(String.valueOf(bundleGetAntenna.getInt("antennaPowerZero")));
                mBinding.labelSettingsAntennaPowerOneEt.setText(String.valueOf(bundleGetAntenna.getInt("antennaPowerOne")));
                mBinding.labelSettingsAntennaPowerTwoEt.setText(String.valueOf(bundleGetAntenna.getInt("antennaPowerTwo")));
                mBinding.labelSettingsAntennaPowerThreeEt.setText(String.valueOf(bundleGetAntenna.getInt("antennaPowerThree")));
                mBinding.labelSettingsDwellTimeZeroEt.setText(String.valueOf(bundleGetAntenna.getInt("dwellTimeZero")));
                mBinding.labelSettingsDwellTimeOneEt.setText(String.valueOf(bundleGetAntenna.getInt("dwellTimeOne")));
                mBinding.labelSettingsDwellTimeTwoEt.setText(String.valueOf(bundleGetAntenna.getInt("dwellTimeTwo")));
                mBinding.labelSettingsDwellTimeThreeEt.setText(String.valueOf(bundleGetAntenna.getInt("dwellTimeThree")));
                mBinding.labelSettingsCalendarCycleZeroEt.setText(String.valueOf(bundleGetAntenna.getInt("calendarCycleZero")));
                mBinding.labelSettingsCalendarCycleOneEt.setText(String.valueOf(bundleGetAntenna.getInt("calendarCycleOne")));
                mBinding.labelSettingsCalendarCycleTwoEt.setText(String.valueOf(bundleGetAntenna.getInt("calendarCycleTwo")));
                mBinding.labelSettingsCalendarCycleThreeEt.setText(String.valueOf(bundleGetAntenna.getInt("calendarCycleThree")));
                break;
            case GET_ANTENNA_STANDING_WAVE_RADIO:
                //delete
                break;
            case GET_GPI_OUTPUT_STATUS:
                Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                Bundle bundleGetGPI = msg.getData();
                mBinding.labelSettingsGpiOneLevelSp.setSelection(bundleGetGPI.getInt("portZeroStatus"));
                mBinding.labelSettingsGpiTwoLevelSp.setSelection(bundleGetGPI.getInt("portOneStatus"));
                break;
            case GET_BUZZER_STATUS_SETTING:
                //delete
                break;
            case SET_BUZZER_STATUS_SETTING:
            case SET_GPO_OUTPUT_STATUS:
            case SET_ANTENNA_CONFIGURATION:
            case TIME_SYNCHRONIZATION:
            case DEVICE_RESTART:
                Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static LabelSettingsFragment newInstance() {
        LabelSettingsFragment fragment = new LabelSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
                    R.layout.fragment_label_settings, container, false);
            mBinding.setOnClickListener(this);
            mHandler = new LabelSettingsFragmentHandler(this);

            UR880Entrance.getInstance().addOnFactorySettingListener(mFactorySettingListener);

            setDeviceID(((HomeActivity) getActivity()).getDeviceID());

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
        if (TextUtils.isEmpty(mDeviceID)) {
            Toast.makeText(getContext(), "您未选择读写器，请在设备信息界面选择需要操作的读写器。", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.label_settings_set_antenna_btn:
                int antennaEnableZero = mBinding.labelSettingsAntennaEnableZeroSw.isChecked() ? 1 : 0;
                int antennaEnableOne = mBinding.labelSettingsAntennaEnableOneSw.isChecked() ? 1 : 0;
                int antennaEnableTwo = mBinding.labelSettingsAntennaEnableTwoSw.isChecked() ? 1 : 0;
                int antennaEnableThree = mBinding.labelSettingsAntennaEnableThreeSw.isChecked() ? 1 : 0;
                int antennaPowerZero = 0;
                int antennaPowerOne = 0;
                int antennaPowerTwo = 0;
                int antennaPowerThree = 0;
                int dwellTimeZero = 0;
                int dwellTimeOne = 0;
                int dwellTimeTwo = 0;
                int dwellTimeThree = 0;
                int calendarCycleZero = 0;
                int calendarCycleOne = 0;
                int calendarCycleTwo = 0;
                int calendarCycleThree = 0;
                String antennaPower = mBinding.labelSettingsAntennaPowerZeroEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "1号天线发射功率未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    antennaPowerZero = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsAntennaPowerOneEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "2号天线发射功率未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    antennaPowerOne = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsAntennaPowerTwoEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "3号天线发射功率未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    antennaPowerTwo = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsAntennaPowerThreeEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "4号天线发射功率未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    antennaPowerThree = Integer.parseInt(antennaPower);
                }

                antennaPower = mBinding.labelSettingsDwellTimeZeroEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "1号天线驻留时间未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    dwellTimeZero = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsDwellTimeOneEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "2号天线驻留时间未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    dwellTimeOne = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsDwellTimeTwoEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "3号天线驻留时间未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    dwellTimeTwo = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsDwellTimeThreeEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "4号天线驻留时间未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    dwellTimeThree = Integer.parseInt(antennaPower);
                }

                antennaPower = mBinding.labelSettingsCalendarCycleZeroEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "1号天线驻留周期未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    calendarCycleZero = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsCalendarCycleOneEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "2号天线驻留周期未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    calendarCycleOne = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsCalendarCycleTwoEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "3号天线驻留周期未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    calendarCycleTwo = Integer.parseInt(antennaPower);
                }
                antennaPower = mBinding.labelSettingsCalendarCycleThreeEt.getText().toString().trim();
                if (TextUtils.isEmpty(antennaPower)) {
                    Toast.makeText(getContext(), "4号天线驻留周期未填写。", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    calendarCycleThree = Integer.parseInt(antennaPower);
                }

                UR880Entrance.getInstance().send(
                        new UR880SendInfo.Builder().setAntennaConfiguration(
                                mDeviceID,
                                antennaEnableZero, antennaEnableOne, antennaEnableTwo, antennaEnableThree,
                                antennaPowerZero, antennaPowerOne, antennaPowerTwo, antennaPowerThree,
                                dwellTimeZero, dwellTimeOne, dwellTimeTwo, dwellTimeThree,
                                calendarCycleZero, calendarCycleOne, calendarCycleTwo, calendarCycleThree).build());
                break;
            case R.id.label_settings_get_antenna_btn:
                UR880Entrance.getInstance().send(
                        new UR880SendInfo.Builder().
                                getAntennaConfiguration(mDeviceID).build());
                break;
            case R.id.label_settings_get_gpi_btn:
                UR880Entrance.getInstance().send(
                        new UR880SendInfo.Builder().
                                getGPIOutputStatus(mDeviceID).build());
                break;
            case R.id.label_settings_set_gpo_btn:
                UR880Entrance.getInstance().send(
                        new UR880SendInfo.Builder().
                                setGPOOutputStatus(mDeviceID, mBinding.labelSettingsGpoPinSp.getSelectedItemPosition(),
                                        mBinding.labelSettingsGpoLevelSp.getSelectedItemPosition()).build());
                break;
            case R.id.label_settings_time_synchronization_btn:
                UR880Entrance.getInstance().send(
                        new UR880SendInfo.Builder().
                                timeSynchronization(mDeviceID).build());
                break;
            case R.id.label_settings_device_restart_btn:
                UR880Entrance.getInstance().send(
                        new UR880SendInfo.Builder().
                                deviceRestart(mDeviceID).build());
                break;
        }
    }

    private static class LabelSettingsFragmentHandler extends Handler {
        private final WeakReference<LabelSettingsFragment> labelSettingsFragmentWeakReference;

        LabelSettingsFragmentHandler(LabelSettingsFragment labelSettingsFragment) {
            super();
            labelSettingsFragmentWeakReference = new WeakReference<>(labelSettingsFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (labelSettingsFragmentWeakReference.get() != null) {
                labelSettingsFragmentWeakReference.get().handleMessage(msg);
            }
        }
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        if (isVisibleToUser) {
//            LogUtil.Companion.getInstance().d("isVisibleToUser");
//            if (mBinding != null && getActivity() != null && ((HomeActivity) getActivity()).getDeviceID() != null) {
//                mBinding.labelSettingIdTv.setText("设备编号：" + ((HomeActivity) getActivity()).getDeviceID());
//            }
//        } else {
//            LogUtil.Companion.getInstance().d("!!!isVisibleToUser");
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }

    private FactorySettingListener mFactorySettingListener = new FactorySettingListener() {
        @Override
        public void setAntennaConfigurationResult(boolean result, int errorNumber) {
            Message msg = Message.obtain();
            msg.what = SET_ANTENNA_CONFIGURATION;
            msg.obj = "天线配置结果：" + result + "  resultCode：" + errorNumber;
            mHandler.sendMessage(msg);
        }

        @Override
        public void getAntennaConfigurationResult(boolean result,
                                                  int errorNumber,
                                                  int antennaEnableZero, int antennaEnableOne, int antennaEnableTwo, int antennaEnableThree,
                                                  int antennaPowerZero, int antennaPowerOne, int antennaPowerTwo, int antennaPowerThree,
                                                  int dwellTimeZero, int dwellTimeOne, int dwellTimeTwo, int dwellTimeThree,
                                                  int calendarCycleZero, int calendarCycleOne, int calendarCycleTwo, int calendarCycleThree) {
            Message msg = Message.obtain();
            msg.what = GET_ANTENNA_CONFIGURATION;
            Bundle bundle = new Bundle();
            bundle.putBoolean("result", result);
            bundle.putInt("errorNumber", errorNumber);
            bundle.putInt("antennaEnableZero", antennaEnableZero);
            bundle.putInt("antennaEnableOne", antennaEnableOne);
            bundle.putInt("antennaEnableTwo", antennaEnableTwo);
            bundle.putInt("antennaEnableThree", antennaEnableThree);
            bundle.putInt("antennaPowerZero", antennaPowerZero);
            bundle.putInt("antennaPowerOne", antennaPowerOne);
            bundle.putInt("antennaPowerTwo", antennaPowerTwo);
            bundle.putInt("antennaPowerThree", antennaPowerThree);
            bundle.putInt("dwellTimeZero", dwellTimeZero);
            bundle.putInt("dwellTimeOne", dwellTimeOne);
            bundle.putInt("dwellTimeTwo", dwellTimeTwo);
            bundle.putInt("dwellTimeThree", dwellTimeThree);
            bundle.putInt("calendarCycleZero", calendarCycleZero);
            bundle.putInt("calendarCycleOne", calendarCycleOne);
            bundle.putInt("calendarCycleTwo", calendarCycleTwo);
            bundle.putInt("calendarCycleThree", calendarCycleThree);
            msg.setData(bundle);
            msg.obj = "获取天线配置结果：" + result + "  resultCode：" + errorNumber;
            mHandler.sendMessage(msg);
        }

        @Override
        public void getAntennaStandingWaveRatioResult(float antennaZero, float antennaOne, float antennaTwo, float antennaThree) {
            Message msg = Message.obtain();
            msg.what = GET_ANTENNA_STANDING_WAVE_RADIO;

            mHandler.sendMessage(msg);
        }

        @Override
        public void setGPOOutputStatusResult(boolean result, int errorNumber) {
            Message msg = Message.obtain();
            msg.what = SET_GPO_OUTPUT_STATUS;
            msg.obj = "GPO设置结果：" + result + "  resultCode：" + errorNumber;
            mHandler.sendMessage(msg);
        }

        @Override
        public void getGPIOutputStatusResult(boolean result, int errorNumber, int portZeroStatus, int portOneStatus, int portTwoStatus, int portThreeStatus) {
            Message msg = Message.obtain();
            msg.what = GET_GPI_OUTPUT_STATUS;
            Bundle bundle = new Bundle();
            bundle.putBoolean("result", result);
            bundle.putInt("errorNumber", errorNumber);
            bundle.putInt("portZeroStatus", portZeroStatus);
            bundle.putInt("portOneStatus", portOneStatus);
            msg.setData(bundle);
            msg.obj = "获取GPI结果：" + result + "  resultCode：" + errorNumber;
            mHandler.sendMessage(msg);
        }

        @Override
        public void setBuzzerStatusResult(int errorNumber) {
            Message msg = Message.obtain();
            msg.what = SET_BUZZER_STATUS_SETTING;
            msg.obj = "蜂鸣器设置结果：" + errorNumber;
            mHandler.sendMessage(msg);
        }

        @Override
        public void getBuzzerStatusResult(int buzzerStatus) {
            Message msg = Message.obtain();
            msg.what = GET_BUZZER_STATUS_SETTING;
            msg.obj = buzzerStatus;
            mHandler.sendMessage(msg);
        }

        @Override
        public void timeSynchronizationResult(int errorNumber) {
            Message msg = Message.obtain();
            msg.what = TIME_SYNCHRONIZATION;
            msg.obj = "时间同步设置结果：" + errorNumber;
            mHandler.sendMessage(msg);
        }

        @Override
        public void deviceRestartResult(int errorNumber) {
            Message msg = Message.obtain();
            msg.what = DEVICE_RESTART;
            msg.obj = "设备重启设置结果：" + errorNumber;
            mHandler.sendMessage(msg);
        }
    };

    public void setDeviceID(String deviceID){
        if (deviceID != null) {
            mDeviceID = deviceID;
            if (mBinding != null) {
                mBinding.labelSettingIdTv.setText("设备编号：" + deviceID);
            }
        }
    }
}
