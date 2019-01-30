package com.example.keveaux_tm.pdfreader.CategoryFragments.JsonPackage;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.keveaux_tm.pdfreader.gridViewAdapter.GridViewAdapter;
import com.example.keveaux_tm.pdfreader.gridViewAdapter.GettersSetters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CategoryJsonDownloader {
    private static final String PDF_SITE_URL = "http://104.248.124.210/pdfwork/pdfwork/";
    private final Context c;
    private GridViewAdapter gridViewAdapter;
    RequestQueue requestQueue;


    public CategoryJsonDownloader(Context c) {
        this.c = c;
    }

    public void jsondownloader(String loginURL, final GridView gridView, final ProgressBar myProgressBar) {

        final ArrayList<GettersSetters> pdfDocuments = new ArrayList<>();

        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        requestQueue = Volley.newRequestQueue(c);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            GettersSetters GettersSetters;
                            JSONArray ja = response.getJSONArray("results");

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jsonObject = ja.getJSONObject(i);

                                String id=jsonObject.getString("id");
                                String name = jsonObject.getString("name");
                                String author = jsonObject.getString("author");
                                String pdfURL = jsonObject.getString("pdf_url");
                                String pdfIconURL = jsonObject.getString("pdf_icon_url");
                                String price = jsonObject.getString("price");


                                GettersSetters = new GettersSetters();
                                GettersSetters.setId(id);
                                GettersSetters.setName(name);
                                GettersSetters.setAuthorName(author);
                                GettersSetters.setPrice(price);
                                GettersSetters.setPdfURL(PDF_SITE_URL + pdfURL);
                                GettersSetters.setPdfIconURL(PDF_SITE_URL + pdfIconURL);


                                pdfDocuments.add(GettersSetters);
                            }
                            gridViewAdapter = new GridViewAdapter(c, pdfDocuments);
                            gridView.setAdapter(gridViewAdapter);
                            myProgressBar.setVisibility(View.GONE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(c, "" + error, Toast.LENGTH_SHORT).show();
                    }
                }

        );
        requestQueue.add(jor);


    }

}
