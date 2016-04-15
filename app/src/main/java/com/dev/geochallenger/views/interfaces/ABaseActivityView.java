package com.dev.geochallenger.views.interfaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dev.geochallenger.presenters.interfaces.IPresenter;

public abstract class ABaseActivityView<T extends IPresenter> extends AppCompatActivity implements IView {
    protected ProgressDialog progressDialog;

    protected T presenter;

    protected abstract T createPresenter();

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        onViewCreated(savedInstanceState);
        presenter = createPresenter();
        presenter.init();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(ABaseActivityView.this);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }
        });
    }

    @Override
    public void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }
    @Override
    public void showErrorMessage(final String title, final String body) {
        //TODO
    }

    protected void startActivityWithTransition(Intent intent) {
        // TODO: Add transition?
        startActivity(intent);
    }
}
