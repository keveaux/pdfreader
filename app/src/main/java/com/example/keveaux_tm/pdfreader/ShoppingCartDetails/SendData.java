package com.example.keveaux_tm.pdfreader.ShoppingCartDetails;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SendData {

    public SendData() {
    }

    public static void SendData(final String shoppingcartdata, final String post){
        new  AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                //Create Array of Post Variabels
                ArrayList<NameValuePair> postVars = new ArrayList<NameValuePair>();

                //Add a 1st Post Value called JSON with String value of JSON inside
                //This is first and last post value sent because server side will decode the JSON and get other vars from it.
                postVars.add(new BasicNameValuePair("JSON", shoppingcartdata));

                //Declare and Initialize Http Clients and Http Posts
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(post);

                //Format it to be sent
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(postVars));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                /* Send request and Get the Response Back */
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    String responseBody = EntityUtils.toString(response.getEntity());
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    Log.v("MAD", "Error sending... ");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("MAD", "Error sending... ");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                //Toast.makeText(SendData.this, ""+fetchBoksInCart(), Toast.LENGTH_SHORT).show();
                super.onPostExecute(o);
            }


        }.execute(null,null,null);
    }
}
