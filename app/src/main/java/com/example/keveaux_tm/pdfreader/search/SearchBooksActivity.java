package com.example.keveaux_tm.pdfreader.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.keveaux_tm.pdfreader.gridViewAdapter.GridViewAdapter;
import com.example.keveaux_tm.pdfreader.CheckInternetConnection.NoInternet;
import com.example.keveaux_tm.pdfreader.gridViewAdapter.GettersSetters;
import com.example.keveaux_tm.pdfreader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchBooksActivity extends AppCompatActivity {


    private GridViewAdapter adapter;
    private static final String PDF_SITE_URL = "http://104.248.124.210/pdfwork/pdfwork/";
    RequestQueue requestQueue;

    //fetch book being searched for
    public void fetchbooks() {
        //check for internet connection
        NoInternet noInternet = new NoInternet();

        if (!noInternet.isConnected(SearchBooksActivity.this)) {

            noInternet.builddialog(SearchBooksActivity.this).show();
        } else {
            final GridView myGridView = findViewById(R.id.mysearchGridView);
            final ProgressBar myProgressBar = findViewById(R.id.mysearchprogressbar);

            Bundle extras = getIntent().getExtras();
            String searchkey = extras.getString("bookname").toString();

            jsondownloader("http://104.248.124.210/pdfwork/pdfwork/searchexample.php?book_name=" + searchkey, myGridView, myProgressBar);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_search);

        fetchbooks();

    }


    //check if book being searched for is in the db and fetch it
    public void jsondownloader(String loginURL, final GridView gv, final ProgressBar myProgressBar) {

        final ArrayList<GettersSetters> searchList = new ArrayList<>();

        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(SearchBooksActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                        //if there is no such book
                        if (response.isNull("results")) {
                            myProgressBar.setVisibility(View.GONE);

                            TextView noresult = findViewById(R.id.noresult);
                            noresult.setVisibility(View.VISIBLE);
                            }

                        try {
                            GettersSetters gettersSetters;
                            JSONArray ja = response.getJSONArray("results");

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jsonObject = ja.getJSONObject(i);


                                String id = jsonObject.getString("id");
                                String name = jsonObject.getString("name");
                                String author = jsonObject.getString("author");
                                String pdfURL = jsonObject.getString("pdf_url");
                                String pdfIconURL = jsonObject.getString("pdf_icon_url");
                                String price = jsonObject.getString("price");


                                gettersSetters = new GettersSetters();
                                gettersSetters.setName(name);
                                gettersSetters.setAuthorName(author);
                                gettersSetters.setPrice(price);
                                gettersSetters.setPdfURL(PDF_SITE_URL + pdfURL);
                                gettersSetters.setPdfIconURL(PDF_SITE_URL + pdfIconURL);
                                gettersSetters.setId(id);


                                searchList.add(gettersSetters);
                            }
                            adapter = new GridViewAdapter(SearchBooksActivity.this, searchList);
                            gv.setAdapter(adapter);
                            myProgressBar.setVisibility(View.GONE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchBooksActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                    }
                }

        );
        requestQueue.add(jor);


    }
}
