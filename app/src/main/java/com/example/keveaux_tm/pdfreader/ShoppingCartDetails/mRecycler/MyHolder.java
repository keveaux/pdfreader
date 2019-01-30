package com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mRecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keveaux_tm.pdfreader.R;

class MyHolder extends RecyclerView.ViewHolder {

    TextView nametxt;
    TextView pricetxt;
    ImageView img;
    Button remove;

    public MyHolder(View itemView) {
        super(itemView);

        pricetxt=itemView.findViewById(R.id.pricetxt);
        nametxt=itemView.findViewById(R.id.nametxt);
        img=itemView.findViewById(R.id.movieimage);
        remove=itemView.findViewById(R.id.removeitem);
    }
}
