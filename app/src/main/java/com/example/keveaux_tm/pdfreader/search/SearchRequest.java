package com.example.keveaux_tm.pdfreader.search;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;


import java.util.HashMap;
import java.util.Map;

public class SearchRequest extends StringRequest {
    private static final String Search_request_url="http://104.248.124.210/pdfwork/pdfwork/search.php";
    private Map<String,String> params;

    public SearchRequest(String book_name, Response.Listener<String> listener) {
        super(Request.Method.POST,Search_request_url,listener,null);

        params=new HashMap<>();
        params.put("book_name",book_name);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
