package com.dev.geochallenger.views.interfaces;

import com.dev.geochallenger.models.entities.LeaderBoardItem;
import com.dev.geochallenger.models.entities.login.UserResponce;

import java.util.List;

/**
 * Created by a_dibrivnyj on 4/24/16.
 */
public interface ILeaderboardView extends IView {
    void updateLeaderBoardView(List<LeaderBoardItem> items);

    void updateUserData(UserResponce loginResponce);
}
