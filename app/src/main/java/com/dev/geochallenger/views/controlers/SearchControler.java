package com.dev.geochallenger.views.controlers;

import android.content.Context;
import android.widget.Toast;

import com.lapism.searchview.adapter.SearchAdapter;
import com.lapism.searchview.adapter.SearchItem;
import com.lapism.searchview.view.SearchCodes;
import com.lapism.searchview.view.SearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a_dibrivnyj on 4/20/16.
 */
public class SearchControler {

    private Context context;
    private SearchView searchView;
    private ArrayList<SearchItem> mSuggestionsList;
    private SearchView.OnQueryTextListener textListener;
    private SearchAdapter.OnItemClickListener clickListener;
    private SearchView.SearchMenuListener onMenuClickListener;

    public SearchControler(Context context, SearchView searchView) {
        this.context = context;
        this.searchView = searchView;
        mSuggestionsList = new ArrayList<>();
    }

    public void setOnQueryTextListener(SearchView.OnQueryTextListener textListener) {
        this.textListener = textListener;
    }

    public void setOnItemClickListener(SearchAdapter.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnMenuClickListener(SearchView.SearchMenuListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    public void init() {
        searchView.setVersion(SearchCodes.VERSION_TOOLBAR);
        searchView.setStyle(SearchCodes.STYLE_TOOLBAR_CLASSIC);
        searchView.setTheme(SearchCodes.THEME_LIGHT);
        searchView.setHint("Search");
        searchView.setVoice(false);
        searchView.setOnSearchMenuListener(onMenuClickListener);
        searchView.setOnQueryTextListener(textListener);

        List<SearchItem> mResultsList = new ArrayList<>();
        SearchAdapter mSearchAdapter = new SearchAdapter(context, mResultsList, mSuggestionsList, SearchCodes.THEME_LIGHT);
        mSearchAdapter.setOnItemClickListener(clickListener);
        searchView.setAdapter(mSearchAdapter);
    }

    public void clearSuggestionList() {
        mSuggestionsList.clear();
    }

    public void addItomToSuggestionList(SearchItem searchItem) {
        mSuggestionsList.add(searchItem);
    }

    public void hide() {
        searchView.hide(true);
    }

}
