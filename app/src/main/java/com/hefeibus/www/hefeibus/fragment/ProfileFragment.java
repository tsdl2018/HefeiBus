package com.hefeibus.www.hefeibus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hefeibus.www.hefeibus.R;
import com.hefeibus.www.hefeibus.base.BaseMvpFragment;
import com.hefeibus.www.hefeibus.base.IPresenter;

/**
 * 个人资料界面
 * Created by xyw-mac on 2018/3/18.
 */

public class ProfileFragment extends BaseMvpFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return invokeMe(inflater, container);
    }

    @Override
    protected void init() {

    }

    @Override
    protected IPresenter onPresenterCreated() {
        return null;
    }

    @Override
    protected void setAttributes() {

    }

    @Override
    protected void initViews(View view) {

    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_profile;
    }
}
