package com.dev.geochallenger.views.interfaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.geochallenger.presenters.interfaces.IPresenter;

public abstract class ABaseFragmentView<T extends IPresenter> extends Fragment implements IView {

    protected T presenter;
    protected View root;
    private ProgressDialog progressDialog;

    protected abstract T createPresenter();

    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(getLayoutId(), container, false);
        onViewCreated(savedInstanceState);
        presenter = createPresenter();
        presenter.init();

        return root;
    }
    /**
     * just for the case if we need to get something from intent
     *
     * @param savedInstanceState
     */
    protected void onViewCreated(Bundle savedInstanceState) {
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    @Override
    public void showErrorMessage(String title, String body) {
        //TODO
    }

    protected void startActivityWithTransition(Intent intent) {
        // TODO: Add transition?
        startActivity(intent);
    }
}
