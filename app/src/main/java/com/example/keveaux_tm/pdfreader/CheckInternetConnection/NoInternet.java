package com.example.keveaux_tm.pdfreader.CheckInternetConnection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

public class NoInternet {
    public boolean isConnected(Context context){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();

        if(networkInfo!=null&&networkInfo.isConnectedOrConnecting()){
            android.net.NetworkInfo wifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile!=null && mobile.isConnectedOrConnecting()) || (wifi!=null && wifi.isConnectedOrConnecting())){
                return true;
            }else return false;



        }else
            return false;

    }

    public AlertDialog.Builder builddialog(final Context c){
        AlertDialog.Builder builder =new AlertDialog.Builder(c);
        builder.setTitle("No internet connection");
        builder.setMessage("you need to have wifi or mobile data to proceed press OK to go to settings");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               c.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            }
        });

        return builder;
    }
}
