package com.zk.rfidreaderdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.zk.common.utils.LogUtil;
import com.zk.rfidreaderdemo.R;
import com.zk.rfidreaderdemo.activity.HomeActivity;
import com.zk.rfidreaderdemo.databinding.FragmentLabelOperationBinding;

import java.lang.ref.WeakReference;

public class LabelOperationFragment extends Fragment implements View.OnClickListener{
    private FragmentLabelOperationBinding mBinding;

    private View mView;

    private LabelOperationFragmentHandler mHandler;
    private void handleMessage(Message msg) {
        switch (msg.what) {

        }
    }

    public static LabelOperationFragment newInstance() {
        LabelOperationFragment fragment = new LabelOperationFragment();
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
                    R.layout.fragment_label_operation, container, false);
            mBinding.setOnClickListener(this);
            mHandler = new LabelOperationFragmentHandler(this);
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
        switch (v.getId()){

        }
    }

    private static class LabelOperationFragmentHandler extends Handler {
        private final WeakReference<LabelOperationFragment> labelOperationFragmentWeakReference;

        LabelOperationFragmentHandler(LabelOperationFragment labelOperationFragment) {
            super();
            labelOperationFragmentWeakReference = new WeakReference<>(labelOperationFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (labelOperationFragmentWeakReference.get() != null) {
                labelOperationFragmentWeakReference.get().handleMessage(msg);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser){
            LogUtil.Companion.getInstance().d("isVisibleToUser");
            if (mBinding != null && getActivity() != null && ((HomeActivity) getActivity()).getDeviceID() != null) {
                mBinding.labelOperationIdTv.setText("设备编号：" + ((HomeActivity) getActivity()).getDeviceID());
            }
        } else {
            LogUtil.Companion.getInstance().d("!!!isVisibleToUser");
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

}
