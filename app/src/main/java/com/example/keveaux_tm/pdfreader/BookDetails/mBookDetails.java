package com.example.keveaux_tm.pdfreader.BookDetails;

import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mShoppingCartActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keveaux_tm.pdfreader.FirebaseSignup.SignUpActivity;
import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase.DBAdapter;
import com.example.keveaux_tm.pdfreader.mPicasso.PicassoClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class mBookDetails extends FragmentActivity {

    public String book_name, id, product_description, url, icon_url, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_product_details);


        //fetch book info from GridViewAdapter using bundles
        id = getIntent().getExtras().getString("id");
        book_name = getIntent().getExtras().getString("bookname");
        product_description = getIntent().getExtras().getString("description");
        url = getIntent().getExtras().getString("url");
        icon_url = getIntent().getExtras().getString("pdf_icon_url");
        price = getIntent().getExtras().getString("price");


        //init textviews
        final TextView bookname = findViewById(R.id.bookname);
        bookname.setText(book_name);

        TextView description = findViewById(R.id.description);
        description.setText(product_description);

        TextView pricetv = findViewById(R.id.price);
        pricetv.setText(price + "Ksh");


        //init pallete class that helps fetch color from background of image
        Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                // access palette colors here
            }
        };

        final ImageView bookimage = findViewById(R.id.overlapImage);

        //fetch image from server using picasso
        PicassoClient.loadImage(icon_url,bookimage);

        bookimage.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) bookimage.getDrawable();

        Bitmap myBitmap = drawable.getBitmap();

        //fetch the most dominant color in the image
        if (myBitmap != null && !myBitmap.isRecycled()) {
            Palette.from(myBitmap).generate(paletteListener);
            Palette palette = Palette.generate(myBitmap);

            int blackHex = 0x000000;
            int dominantColor = palette.getDominantColor(blackHex);
            RelativeLayout relativeLayout = findViewById(R.id.layoutTop);
            relativeLayout.setBackgroundColor(dominantColor);
        }


        // button that takes you to the preview
        Button preview = findViewById(R.id.previewbtn);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mBookDetails.this, PDFActivity.class);
                i.putExtra("PATH", url);
                i.putExtra("bookname", book_name);
                i.putExtra("iconurl", icon_url);
                startActivity(i);
            }
        });


        final Button addtocart = findViewById(R.id.add_to_cart);

        //add book name
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //init firebase user
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                //get uid
                String uid = currentFirebaseUser.getUid();

                //save in sqlite
                save(book_name, icon_url, price, id, uid);


            }
        });


    }

    //save name url ..... to SqLite
    private void save(String name, String url, String price, String id, String uid) {
        //init DBAdapter
        DBAdapter db = new DBAdapter(this);
        db.openDB();//open db

        //add items to db
        long result = db.add(name, url, price, id, uid);

        //if added successfully pop up an alert dialog
        if (result == 1) {
            Toast.makeText(this, "successful", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mBookDetails.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mBookDetails.this);
            }

            builder.setTitle(getResources().getString(R.string.confirm))
                    .setMessage(book_name + " " + getResources().getString(R.string.proceed))
                    .setPositiveButton(getResources().getString(R.string.continueshopping), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mBookDetails.this, SignUpActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.proceedcheckout), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mBookDetails.this, mShoppingCartActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();

        } else {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
        }

        //close db
        db.closeDB();
    }

}
