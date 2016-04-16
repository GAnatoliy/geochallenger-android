package com.dev.geochallenger.models.interfaces;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public interface OnDataLoaded<T> {
    void onSuccess(T t);

    void onError(Throwable t);
}
