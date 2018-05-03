package com.android.example.projectmanager.main.quote;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.example.projectmanager.quotesync.QuoteUpdaterService;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.utilities.PrefUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class QuoteFragment extends Fragment {

    @BindView(R.id.quote_image) ImageView imageView;
    @BindView(R.id.image_progress_bar) ProgressBar imageProgressBar;

    @BindView(R.id.panel_quote) LinearLayout quote_panel;
    @BindView(R.id.tv_quote) TextView tvQuote;
    @BindView(R.id.tv_quote_author) TextView tvQuoteAuthor;

    @BindView(R.id.quote_progressbar) ProgressBar quoteProgressBar;

    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;


    public QuoteFragment() {
        // Required empty public constructor
    }

    public static QuoteFragment newInstance() {
        QuoteFragment fragment = new QuoteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quote, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, getView());

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().startService(new Intent(getContext(), QuoteUpdaterService.class));

                imageProgressBar.setVisibility(View.VISIBLE);
                quoteProgressBar.setVisibility(View.VISIBLE);

                imageView.setImageDrawable(null);
                tvQuote.setText("");
                tvQuoteAuthor.setText("");
            }
        });

        updateScreen();

    }

    private void updateScreen() {

        String quoteDate = PrefUtils.getDate(getContext());
        if(quoteDate.equals(getString(R.string.label_na))) {
            imageProgressBar.setVisibility(View.VISIBLE);
            quoteProgressBar.setVisibility(View.VISIBLE);
            getActivity().startService(new Intent(getContext(), QuoteUpdaterService.class));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar c = Calendar.getInstance();
            String currentDate = sdf.format(c.getTime());

            int i = quoteDate.compareTo(currentDate);

            if (i == 0) {
                LoadImage();

                quoteProgressBar.setVisibility(View.GONE);
                tvQuote.setText(PrefUtils.getQuote(getContext()));
                tvQuoteAuthor.setText(PrefUtils.getAuthor(getContext()));
            } else {
                imageProgressBar.setVisibility(View.VISIBLE);
                quoteProgressBar.setVisibility(View.VISIBLE);
                getActivity().startService(new Intent(getContext(), QuoteUpdaterService.class));
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QuoteUpdaterService.BROADCAST_DATA_UPDATED);
        intentFilter.addAction(QuoteUpdaterService.BROADCAST_NO_CONNECTIVITY);
        intentFilter.addAction(QuoteUpdaterService.BROADCAST_DATA_UPDATE_FAILED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRefreshingReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRefreshingReceiver);
    }

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            mSwipeRefreshLayout.setRefreshing(false);

            if (QuoteUpdaterService.BROADCAST_DATA_UPDATED.equals(intent.getAction())) {

                LoadImage();

                quoteProgressBar.setVisibility(View.GONE);
                tvQuote.setText(PrefUtils.getQuote(getContext()));
                tvQuoteAuthor.setText(PrefUtils.getAuthor(getContext()));

                return;
            }

            if (QuoteUpdaterService.BROADCAST_NO_CONNECTIVITY.equals(intent.getAction())) {
                Snackbar mySnackBar = Snackbar.make(getView(),
                        R.string.no_internet_connection, Snackbar.LENGTH_SHORT);
                mySnackBar.show();

                LoadImage();

                quoteProgressBar.setVisibility(View.GONE);
                tvQuote.setText(PrefUtils.getQuote(getContext()));
                tvQuoteAuthor.setText(PrefUtils.getAuthor(getContext()));

                return;
            }

            if (QuoteUpdaterService.BROADCAST_DATA_UPDATE_FAILED.equals(intent.getAction())) {

                Snackbar mySnackBar = Snackbar.make(getView(),
                        R.string.quote_update_failed, Snackbar.LENGTH_SHORT);
                mySnackBar.show();

                LoadImage();

                quoteProgressBar.setVisibility(View.GONE);
                tvQuote.setText(PrefUtils.getQuote(getContext()));
                tvQuoteAuthor.setText(PrefUtils.getAuthor(getContext()));

                return;
            }

        }
    };

    private void LoadImage() {
        imageProgressBar.setVisibility(View.VISIBLE);
        Picasso.with(getContext())
                .load(PrefUtils.getImageLink(getContext()))
                .placeholder(R.drawable.ic_error_24dp)
                .into(imageView, new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {
                        imageProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        imageProgressBar.setVisibility(View.GONE);
                    }
                });
    }

}
