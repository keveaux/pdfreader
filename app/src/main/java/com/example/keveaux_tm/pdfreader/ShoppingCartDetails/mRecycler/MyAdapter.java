package com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mRecycler;

import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mShoppingCartActivity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase.DBAdapter;
import com.example.keveaux_tm.pdfreader.mPicasso.PicassoClient;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDataObject.*;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder>{



    Context c;
    static ArrayList<mBooks> booksArrayList;
    public static int count;



    public MyAdapter(Context c, ArrayList<mBooks> mBooksArrayList) {
        this.c = c;
        this.booksArrayList = mBooksArrayList;
    }



    @Override
    public MyHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model,viewGroup,false);
        MyHolder holder=new MyHolder(v);


        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {


        myHolder.nametxt.setText(booksArrayList.get(i).getName());
        myHolder.pricetxt.setText(booksArrayList.get(i).getPrice()+" Ksh");


        myHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter dbAdapter=new DBAdapter(c);
                dbAdapter.DeleteUser(booksArrayList.get(i).getId());
                Intent intent=new Intent(c,mShoppingCartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                c.startActivity(intent);
            }
        });
        PicassoClient.loadImage(booksArrayList.get(i).getPdfURL(),myHolder.img);

    }

    @Override
    public  int getItemCount() {
        return booksArrayList.size();
    }
}
