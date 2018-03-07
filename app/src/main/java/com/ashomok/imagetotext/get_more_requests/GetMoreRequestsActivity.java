package com.ashomok.imagetotext.get_more_requests;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ashomok.imagetotext.R;
import com.ashomok.imagetotext.billing.model.SkuRowData;
import com.ashomok.imagetotext.firebaseUiAuth.BaseLoginActivity;
import com.ashomok.imagetotext.get_more_requests.row.PromoListAdapter;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.imagetotext.utils.LogUtil.DEV_TAG;

/**
 * Created by iuliia on 3/2/18.
 */

public class GetMoreRequestsActivity extends BaseLoginActivity
        implements GetMoreRequestsContract.View{

    private static final String TAG = DEV_TAG + GetMoreRequestsActivity.class.getSimpleName();
    @Inject
    GetMoreRequestsPresenter mPresenter;

    @Inject
    PromoListAdapter promoListAdapter;

    private View mRootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this); //todo or extends daggerappcompat activity instaead
        setContentView(R.layout.activity_get_more_requests);
        mRootView = findViewById(android.R.id.content);
        if (mRootView == null) {
            mRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        }
        initToolbar();
        initPromoList();
        initPaidOption();
        mPresenter.takeView(this);
    }

    @Override
    public void updateUi(boolean isUserSignedIn) {
        //todo refresh list
        //todo
    }

    private void initPaidOption() {
        //todo
    }

    private void initPromoList() {
        RecyclerView recyclerView = findViewById(R.id.promo_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(promoListAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Show CollapsingToolbarLayout Title ONLY when collapsed
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getResources().getString(R.string.get_free_requests));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void showError(int errorMessageRes) {

    }

    @Override
    public void showInfo(int infoMessageRes) {

    }

    @Override
    public void initBuyRequestsRow(SkuRowData item) {

    }

    @Override
    public void showInfo(String message) {

    }
}