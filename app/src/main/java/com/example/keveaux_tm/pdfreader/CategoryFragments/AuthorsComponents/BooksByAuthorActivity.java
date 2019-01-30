package com.example.keveaux_tm.pdfreader.CategoryFragments.AuthorsComponents;

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

public class BooksByAuthorActivity extends AppCompatActivity {

    private GridViewAdapter gridViewAdapter;
    private static final String PDF_SITE_URL = "http://104.248.124.210/pdfwork/pdfwork/";


    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_by_author);

        fetchbooks();
    }

    //fetch author's book
    public void fetchbooks() {
        //check for internet connection
        NoInternet noInternet = new NoInternet();

        if (!noInternet.isConnected(BooksByAuthorActivity.this)) {

            noInternet.builddialog(BooksByAuthorActivity.this).show();
        } else {
            final GridView myGridView = findViewById(R.id.authorbooksGridView);
            final ProgressBar myProgressBar = findViewById(R.id.authorbooksprogressbar);


            String authorid=getIntent().getExtras().getString("authorId");

            authorbooksjsondownloader("http://104.248.124.210/pdfwork/pdfwork/authorbooks.php?author_books=" + authorid, myGridView, myProgressBar);

        }
    }

    //fetch json response containing author's books
    public void authorbooksjsondownloader(String loginURL, final GridView gridView, final ProgressBar myProgressBar) {

        final ArrayList<GettersSetters> authorBooksList = new ArrayList<>();

        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //check if response sent is null
                        if(response.isNull("results")){
                            myProgressBar.setVisibility(View.GONE);

                            TextView noresult=findViewById(R.id.noresult);
                            noresult.setVisibility(View.VISIBLE);

                        }

                        try {

                            GettersSetters GettersSetters;
                            JSONArray ja = response.getJSONArray("results");

                            for(int i=0;i<ja.length();i++) {

                                JSONObject jsonObject = ja.getJSONObject(i);


                                String name = jsonObject.getString("name");
                                String author = jsonObject.getString("author");
                                String pdfURL = jsonObject.getString("pdf_url");
                                String pdfIconURL = jsonObject.getString("pdf_icon_url");
                                String price = jsonObject.getString("price");


                                GettersSetters = new GettersSetters();
                                GettersSetters.setName(name);
                                GettersSetters.setAuthorName(author);
                                GettersSetters.setPrice(price);
                                GettersSetters.setPdfURL(PDF_SITE_URL + pdfURL);
                                GettersSetters.setPdfIconURL(PDF_SITE_URL + pdfIconURL);


                                authorBooksList.add(GettersSetters);
                            }
                            gridViewAdapter = new GridViewAdapter(BooksByAuthorActivity.this, authorBooksList);
                            gridView.setAdapter(gridViewAdapter);
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
                        Toast.makeText(BooksByAuthorActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                    }
                }

        );
        requestQueue.add(jor);

    }
}
