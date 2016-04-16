package com.dev.geochallenger.views.interfaces;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.CitiesEntity;
import com.dev.geochallenger.views.interfaces.IView;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public interface IMainView extends IView {

    void initMap(List<Poi> pois);

    void populateAutocompeteList(CitiesEntity citiesEntity);

}
