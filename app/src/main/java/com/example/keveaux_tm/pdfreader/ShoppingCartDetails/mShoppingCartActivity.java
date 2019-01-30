package com.example.keveaux_tm.pdfreader.ShoppingCartDetails;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDataObject.mBooks;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase.DBAdapter;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mRecycler.*;
import com.example.keveaux_tm.pdfreader.PaymentActivities.*;

import org.json.JSONObject;

import java.util.ArrayList;

public class mShoppingCartActivity extends AppCompatActivity {


    RecyclerView rv;
    MyAdapter adapter;
    ArrayList<mBooks> mBooksArrayList = new ArrayList<>();
    private String paymentoptionArr[] = {"Mpesa", "PayPal & Card"};
    TextView amounttv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_shopping_cart);

        //call pop up on button click
        this.createPopup();

        amounttv = findViewById(R.id.amounttxtview);
        amounttv.setText("total amount is " + getTotal_price());

        //RECYCLERVIEW
        rv = findViewById(R.id.myrecycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());

        //ADAPTER
        adapter = new MyAdapter(this, mBooksArrayList);

        rv.setAdapter(adapter);

        retrieve();

        //delete all data in SqLite
        Button removeall = findViewById(R.id.removeallfromcart);
        removeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter db = new DBAdapter(mShoppingCartActivity.this);
                db.deleteAll();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    //get total price from SqLite
    public int getTotal_price() {
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        Cursor c = db.getprice();

        int total = 0;

        while (c.moveToNext()) {
            int price = c.getInt(0);
            total += price;
        }

        return total;

    }

    //fetch info from SqLite db
    private void retrieve() {
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        Cursor c = db.getBooks();
        //loop through the db to fetch all data
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String url = c.getString(2);
            int price = c.getInt(3);

            mBooks mybooks = new mBooks();
            mybooks.setId(id);
            mybooks.setName(name);
            mybooks.setPdfURL(url);
            mybooks.setPrice(price);

            mBooksArrayList.add(mybooks);
        }
        if (mBooksArrayList.size() > 0) {
            rv.setAdapter(adapter);
        }
        //close db
        db.closeDB();
    }

    // Create phone popup window.
    private void createPopup() {
        // Get phone button.
        final Button checkOutButton = findViewById(R.id.CheckOut);
        checkOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Get popup view.
                View popupView = getLayoutInflater().inflate(R.layout.popupwindow, null);

                // Get phone list view.
                final ListView paymentoptionListView = popupView.findViewById(R.id.popupWindowPhoneList);


                // Set header text in list view.
                TextView headerTextView = new TextView(mShoppingCartActivity.this);
                headerTextView.setText("total amount is " + getTotal_price() + "\nChoose Payment Method");
                headerTextView.setTextSize(15);
                headerTextView.setBackgroundColor(Color.parseColor("#C51162"));
                paymentoptionListView.addHeaderView(headerTextView);

                // Create phone data adapter.
                ArrayAdapter<String> paymentmethodAdapter = new ArrayAdapter<String>(mShoppingCartActivity.this, android.R.layout.simple_list_item_1, paymentoptionArr);

                paymentoptionListView.setAdapter(paymentmethodAdapter);
                paymentoptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {


                        switch (itemIndex) {
                            case 0:

                                break;
                            //open mpesa activity
                            case 1:
                                Intent mpesaintent = new Intent(mShoppingCartActivity.this, MpesaActivity.class);
                                mpesaintent.putExtra("amount", getTotal_price());
                                startActivity(mpesaintent);
                                break;
                            //open paypal activity
                            case 2:
                                Intent paypalintent = new Intent(mShoppingCartActivity.this, paypal.class);
                                paypalintent.putExtra("amount", getTotal_price());
                                startActivity(paypalintent);
                                break;


                        }


                    }
                });

                // Create popup window.
                PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                // Set popup window animation style.
                popupWindow.setAnimationStyle(R.style.popup_window_animation);

                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03DAC6")));

                popupWindow.setFocusable(true);

                popupWindow.setOutsideTouchable(true);

                popupWindow.update();

                // Show popup window offset 1,1 to phoneBtton at the screen center.
                popupWindow.showAtLocation(checkOutButton, Gravity.CENTER, 0, 0);
            }
        });
    }

    //restart the activity to clear caches
    @Override
    protected void onRestart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        super.onRestart();
    }
}
