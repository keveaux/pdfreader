package com.example.keveaux_tm.pdfreader.CategoryFragments.MyLibraryComponents;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.gridViewAdapter.GettersSetters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyLibraryJsonDownloader {

    private static final String PDF_SITE_URL="http://104.248.124.210/pdfwork/pdfwork/";
    private final Context c;
    private MyLibraryGridView adapter ;
    RequestQueue requestQueue;

    public MyLibraryJsonDownloader(Context c) {
        this.c=c;

    }

    public void jsondownloader(String loginURL, final GridView gv, final ProgressBar myProgressBar) {

        final ArrayList<GettersSetters> myLibraryList = new ArrayList<>();

        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        requestQueue = Volley.newRequestQueue(c);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.isNull("results")) {
                            myProgressBar.setVisibility(View.GONE);

                            Toast.makeText(c, "you have not bought any books", Toast.LENGTH_SHORT).show();
                        }

                        try {

                            GettersSetters GettersSetters;
                            JSONArray ja = response.getJSONArray("results");



                            //fetch all the books in the user has bought
                            for(int i=0;i<ja.length();i++) {

                                JSONObject jsonObject = ja.getJSONObject(i);


                                String name = jsonObject.getString("name");
                                String author = jsonObject.getString("author");
                                String pdfURL = jsonObject.getString("pdf_url");
                                String pdfIconURL = jsonObject.getString("pdf_icon_url");
                                String price=jsonObject.getString("price");


                                GettersSetters = new GettersSetters();
                                GettersSetters.setName(name);
                                GettersSetters.setAuthorName(author);
                                GettersSetters.setPrice(price);
                                GettersSetters.setPdfURL(PDF_SITE_URL + pdfURL);
                                GettersSetters.setPdfIconURL(PDF_SITE_URL + pdfIconURL);


                                myLibraryList.add(GettersSetters);
                            }
                            adapter = new MyLibraryGridView(c, myLibraryList);
                            gv.setAdapter(adapter);
                            myProgressBar.setVisibility(View.GONE);


                        }

                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        myProgressBar.setVisibility(View.GONE);
                        Toast.makeText(c, ""+error
                                , Toast.LENGTH_SHORT).show();
                    }
                }

        );
        requestQueue.add(jor);


    }

}
