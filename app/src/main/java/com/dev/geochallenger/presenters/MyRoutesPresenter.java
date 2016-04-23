package com.dev.geochallenger.presenters;

import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.IMyRoutesView;

/**
 * Created by a_dibrivnyj on 4/23/16.
 */
public class MyRoutesPresenter extends IPresenter<IMyRoutesView> {

    public MyRoutesPresenter(IMyRoutesView view) {
        super(view);
    }

    @Override
    public void init() {

    }
}
