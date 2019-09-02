package com.liyy.earthlive.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

public abstract class BaseActivity<T extends BasePresenter> extends Activity implements BaseView {

    public T mPresenter;

    public abstract T createPresenter();

    public abstract int getContentViewId();

    public abstract void initView();

    public void setListener(){}

    protected void processLogic(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initView();
        setListener();
        processLogic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Override
    public void showLoading(String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoading() {
        Toast.makeText(BaseActivity.this, "Hide Loading", Toast.LENGTH_SHORT).show();
    }

    public void transferActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(BaseActivity.this, clazz);
        startActivity(intent);
    }

}
