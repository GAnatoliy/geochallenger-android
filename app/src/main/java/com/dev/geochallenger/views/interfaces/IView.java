package com.dev.geochallenger.views.interfaces;

public interface IView {

    public void showProgress();
    public void hideProgress();
    public void showErrorMessage(String title, String body);
    public String getErrorTitle(Exception exception);
}
