package com.dev.geochallenger.presenters;

import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.IChallengeView;

/**
 * Created by Yuriy Diachenko on 24.04.2016.
 */
public class ChallengePresenter extends IPresenter<IChallengeView> {

    public ChallengePresenter(IChallengeView view) {
        super(view);
    }

    @Override
    public void init() {

    }
}
