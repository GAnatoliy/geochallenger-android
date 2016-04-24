package com.dev.geochallenger.presenters;

import android.support.annotation.Nullable;

import com.dev.geochallenger.models.entities.LeaderBoardItem;
import com.dev.geochallenger.models.entities.login.UserResponce;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.models.repositories.interfaces.ITokenRepository;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.ILeaderboardView;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by a_dibrivnyj on 4/24/16.
 */
public class LeaderboardPresenter extends IPresenter<ILeaderboardView> {
    private final IModel iModel;
    private final ITokenRepository iTokenRepository;

    public LeaderboardPresenter(ILeaderboardView view, IModel iModel, ITokenRepository iTokenRepository) {
        super(view);
        this.iModel = iModel;
        this.iTokenRepository = iTokenRepository;
    }

    @Override
    public void init() {
        iModel.getLeaderBoard(new OnDataLoaded<List<LeaderBoardItem>>() {
            @Override
            public void onSuccess(List<LeaderBoardItem> items) {
                view.updateLeaderBoardView(items);
                getUserData(iTokenRepository.getToken());
            }

            @Override
            public void onError(Throwable t, @Nullable ResponseBody error) {
                view.showErrorMessage("Error", t.getMessage());
                view.hideProgress();
            }
        });
    }

    public void getUserData(String token) {
        iModel.getUser(token, new OnDataLoaded<UserResponce>() {
            @Override
            public void onSuccess(UserResponce loginResponce) {
                view.updateUserData(loginResponce);
            }

            @Override
            public void onError(Throwable t, @Nullable ResponseBody error) {
                view.showErrorMessage("Error", t.getMessage());
                view.hideProgress();
            }
        });
    }
}
