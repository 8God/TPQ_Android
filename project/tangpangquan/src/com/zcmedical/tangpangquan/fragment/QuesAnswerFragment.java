package com.zcmedical.tangpangquan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zcmedical.common.base.BaseFragment;
import com.zcmedical.tangpangquan.R;

public class QuesAnswerFragment extends BaseFragment {

    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_qanda, null);
        return contentView;
    }
}
