package com.dev.geochallenger.views.interfaces;

import com.dev.geochallenger.models.entities.routes.RouteResponse;

import java.util.List;

/**
 * Created by a_dibrivnyj on 4/23/16.
 */
public interface IMyRoutesView extends IView {

    public void updateRoutesList(final List<RouteResponse> routeResponses);

    void openRoute(RouteResponse route);
}
