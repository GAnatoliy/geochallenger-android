package com.dev.geochallenger.presenters.interfaces;

import com.dev.geochallenger.views.interfaces.IView;

public abstract class IPresenter<T extends IView> {
    protected T view;
    public IPresenter(T view) {
        this.view = view;
    }

    public abstract void init();

}
