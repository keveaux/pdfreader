package com.example.keveaux_tm.pdfreader.CategoryFragments.AuthorsComponents;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.keveaux_tm.pdfreader.gridViewAdapter.GettersSetters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuthorJsonDownloader {
    private static final String PDF_SITE_URL = "http://104.248.124.210/pdfwork/pdfwork/";

    private static final String AUTHOR_SITE_URL = "http://104.248.124.210/pdfwork/pdfwork/fetch_authors.php";
    private final Context c;
    private AuthorsGridViewAdapter authorsGridViewAdapter;

    //Constructor
    public AuthorJsonDownloader(Context c) {
        this.c = c;
    }

    //retrieve author's information from the db
    public void retrieveAuthorDetails(final GridView gridView, final ProgressBar myProgressBar) {
        final ArrayList<GettersSetters> authorInfoArrayList = new ArrayList<>();

        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        AndroidNetworking.get(AUTHOR_SITE_URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jo;
                        GettersSetters GettersSetters;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                jo = response.getJSONObject(i);

                                String author_email = jo.getString("author_email");
                                String AuthorId = jo.getString("AuthorId");
                                String name = jo.getString("name");
                                String description = jo.getString("description");
                                String author_image = jo.getString("author_image");


                                GettersSetters = new GettersSetters();
                                GettersSetters.setAuthor_email(author_email);
                                GettersSetters.setAuthorName(name);
                                GettersSetters.setAuthordescription(description);
                                GettersSetters.setAuthorId(AuthorId);
                                GettersSetters.setauthorimage(PDF_SITE_URL + author_image);

                                authorInfoArrayList.add(GettersSetters);
                            }
                            authorsGridViewAdapter = new AuthorsGridViewAdapter(c, authorInfoArrayList);
                            gridView.setAdapter(authorsGridViewAdapter);
                            myProgressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            myProgressBar.setVisibility(View.GONE);
                            Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    //ERROR
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        myProgressBar.setVisibility(View.GONE);
                        Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
