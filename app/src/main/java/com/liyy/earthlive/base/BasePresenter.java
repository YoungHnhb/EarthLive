package com.liyy.earthlive.base;

public abstract class BasePresenter<V extends BaseView> {

    private V mView;

    public void attachView(V view) {
        this.mView = view;
    }

    public void detachView() {
        this.mView = null;
    }

    public boolean isAttached() {
        return mView != null;
    }

    public V getView() {
        return mView;
    }


}
