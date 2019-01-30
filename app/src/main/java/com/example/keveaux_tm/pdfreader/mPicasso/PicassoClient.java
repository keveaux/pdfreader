package com.example.keveaux_tm.pdfreader.mPicasso;


import android.widget.ImageView;

import com.example.keveaux_tm.pdfreader.R;
import com.squareup.picasso.Picasso;

public class PicassoClient {

    //DOWNLOAD AND CACHE IMG
    public static void loadImage(String url, ImageView img)
    {
        if(url != null && url.length()>0)
        {
            Picasso.get().load(url).placeholder(R.drawable.placeholder).into(img);
        }else {
            Picasso.get().load(R.drawable.placeholder).into(img);
        }
    }

}
