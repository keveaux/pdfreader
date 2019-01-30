package com.example.keveaux_tm.pdfreader.gridViewAdapter;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
Our HTTP Client

 */
public class JSONDownloader {
    private static final String PDF_SITE_URL="http://104.248.124.210/pdfwork/pdfwork/";
    private final Context c;
    private GridViewAdapter adapter ;

    public JSONDownloader(Context c) {
        this.c = c;
    }
    /*
    DOWNLOAD PDFS FROM MYSQL
     */
    public void retrieve(final GridView gv, final ProgressBar myProgressBar,String url)
    {
        final ArrayList<GettersSetters> pdfDocuments = new ArrayList<>();

        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        AndroidNetworking.get(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jo;
                        GettersSetters gettersSetters;
                        try
                        {
                            for(int i=0;i<response.length();i++)
                            {
                                jo=response.getJSONObject(i);

                                String id=jo.getString("id");
                                String name=jo.getString("name");
                                String author=jo.getString("author");
                                String description=jo.getString("description");
                                String pdfURL=jo.getString("pdf_url");
                                String pdfIconURL=jo.getString("pdf_icon_url");
                                String price=jo.getString("price");

                                gettersSetters=new GettersSetters();
                                gettersSetters.setId(id);
                                gettersSetters.setName(name);
                                gettersSetters.setAuthorName(author);
                                gettersSetters.setDescription(description);
                                gettersSetters.setPdfURL(PDF_SITE_URL+pdfURL);
                                gettersSetters.setPdfIconURL(PDF_SITE_URL+pdfIconURL);
                                gettersSetters.setPrice(price);

                                pdfDocuments.add(gettersSetters);
                            }
                            adapter =new GridViewAdapter(c,pdfDocuments);
                            gv.setAdapter(adapter);
                            myProgressBar.setVisibility(View.GONE);

                        }catch (JSONException e)
                        {
                            myProgressBar.setVisibility(View.GONE);
                            Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    //ERROR
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        myProgressBar.setVisibility(View.GONE);
                        Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : "+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
