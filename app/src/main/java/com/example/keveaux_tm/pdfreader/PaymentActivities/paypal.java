package com.example.keveaux_tm.pdfreader.PaymentActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.keveaux_tm.pdfreader.Main.MainActivity;
import com.example.keveaux_tm.pdfreader.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase.Constants.TB_NAME;

import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase.DBAdapter;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase.DBHelper;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.SendData;

public class paypal extends AppCompatActivity {

    final String API_GET_TOKEN="http://104.248.124.210/pdfwork/pdfwork/Braintree/braintree/main.php";
    final String API_CHECK_OUT="http://104.248.124.210/pdfwork/pdfwork/Braintree/braintree/checkout.php";

    private static final int REQUEST_CODE=1234;
    String token,amount;
    HashMap<String,String> paramshash;
    LinearLayout group_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        group_payment=findViewById(R.id.payment_group);


        new getToken().execute();
    }

    //submit payments
    private void submitPayment() {
        DropInRequest dropInRequest=new DropInRequest().clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUEST_CODE){
            if(resultCode==RESULT_OK){
                DropInResult result=data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce=result.getPaymentMethodNonce();
                String strNonce=nonce.getNonce();


                    amount= String.valueOf(getIntent().getExtras().getInt("amount"));
                    paramshash=new HashMap<>();
                    paramshash.put("amount",amount);
                    paramshash.put("nonce",strNonce);

                    sendBoughtBooks();

            }else if(resultCode==RESULT_CANCELED) {
                Toast.makeText(this, "user cancel", Toast.LENGTH_SHORT).show();
            }
            else {
                Exception error= (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
                Log.d("EDMT_ERROR",error.toString());

            }
        }
    }

    //fetch books being bought and store the uid and bookid and store them in a jsonArray
    public JSONArray fetchBoksInCart()
    {
        DBHelper helper;

        String myTable = TB_NAME;//Set name of your table


        helper=new DBHelper(paypal.this);
        SQLiteDatabase myDataBase;
        myDataBase=helper.getReadableDatabase();

        //fetch bookid and uid from sqlite
        String searchQuery = "SELECT  bookid,uid FROM " + myTable;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );

        //init JsonArray
        JSONArray resultSet     = new JSONArray();

        //loop through the data in the db and store them all in the array
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) , cursor.getString(i));
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }

    //send bought books to server using sendData class and delete data in sqlite
    private void sendBoughtBooks() {
        RequestQueue requestQueue= Volley.newRequestQueue(paypal.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, API_CHECK_OUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().contains("Successful")){

                    Toast.makeText(paypal.this, "Transaction Successful", Toast.LENGTH_SHORT).show();

                    //url
                    String post="http://104.248.124.210/pdfwork/pdfwork/sendboughtbooks.php";

                    SendData.SendData(fetchBoksInCart().toString(),post);

                    DBAdapter dbAdapter=new DBAdapter(paypal.this); //init DBAdapter
                    dbAdapter.deleteAll();                    //delete data in Sqlite


                    //open My Library fragment after 400ms
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 1000ms
                            Intent intent = new Intent (paypal.this, MainActivity.class);
                            intent.putExtra("EXTRA", "openMyLibraryFragment");
                            startActivity(intent);
                        }
                    }, 400);


                }else{
                    Toast.makeText(paypal.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                }

                Log.d("EDMT_LOG",response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("EDMT_ERROR",error.toString());

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if(paramshash==null){
                    return null;
                }
                Map<String,String> params=new HashMap<>();
                for(String key:paramshash
                        .keySet()){
                    params.put(key,paramshash.get(key));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    //fetch token from server
    private class getToken extends AsyncTask {

        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog=new ProgressDialog(paypal.this,android.R.style.Theme_DeviceDefault_Dialog);
            mDialog.setCancelable(false);
            mDialog.setMessage("loading");
            mDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client=new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            group_payment.setVisibility(View.VISIBLE);

                            token=responseBody;

                            submitPayment();
                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    Log.d("EDMT_FAILURE",exception.toString());

                }
            });
            return null;
        }



        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mDialog.dismiss();
        }
    }


}
