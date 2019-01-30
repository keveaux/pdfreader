package com.example.keveaux_tm.pdfreader.Main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.keveaux_tm.pdfreader.gridViewAdapter.JSONDownloader;
import com.example.keveaux_tm.pdfreader.CheckInternetConnection.NoInternet;
import com.example.keveaux_tm.pdfreader.R;


public class AllBooksFragment extends Fragment {

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_all_books,container,false);

        fetchbooks();

        final SwipeRefreshLayout swipeview=view.findViewById(R.id.allbooksswipe);

        swipeview.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_purple)
        );

        swipeview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeview.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeview.setRefreshing(false);
                        fetchbooks();
                    }
                },2500);
            }
        });

        return view;


    }

    public void fetchbooks(){
        final NoInternet noInternet=new NoInternet();

        if(!noInternet.isConnected(getActivity())) {

            noInternet.builddialog(getActivity()).show();}
        else {
            final GridView myGridView = view.findViewById(R.id.allbooksGridView);
            final ProgressBar myProgressBar = view.findViewById(R.id.allbooksprogressbar);

            new JSONDownloader(getActivity()).retrieve(myGridView, myProgressBar,"http://104.248.124.210/android/pdfwork/pdfwork/");
        }
    }
}
