package com.example.keveaux_tm.pdfreader.CategoryFragments.MyLibraryComponents;

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

import com.example.keveaux_tm.pdfreader.CheckInternetConnection.NoInternet;
import com.example.keveaux_tm.pdfreader.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MyLibraryFragment extends Fragment {


     View view;
    FirebaseUser currentFirebaseUser;
    String uid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view=inflater.inflate(R.layout.fragment_all_books,container,false);

         currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

         uid =currentFirebaseUser.getUid();

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

        final NoInternet noInternet = new NoInternet();

        if (!noInternet.isConnected(getActivity())) {

            noInternet.builddialog(getActivity()).show();
        } else {
            final GridView myGridView = view.findViewById(R.id.allbooksGridView);
            final ProgressBar myProgressBar = view.findViewById(R.id.allbooksprogressbar);


            //fetch books where the user id matches a user id in the db
            new MyLibraryJsonDownloader(getActivity())
                    .jsondownloader("http://104.248.124.210/pdfwork/pdfwork/fetchboughtbooks.php?id="+uid
                            ,myGridView,myProgressBar);

        }

    }


}