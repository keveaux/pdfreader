package com.example.keveaux_tm.pdfreader.CategoryFragments.AuthorsComponents;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keveaux_tm.pdfreader.gridViewAdapter.GettersSetters;
import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.mPicasso.PicassoClient;

import java.util.ArrayList;

public class AuthorsGridViewAdapter extends BaseAdapter {
    Context c;
    ArrayList<GettersSetters> authordetailsList;

    //CONSTRUCTOR
    public AuthorsGridViewAdapter(Context c, ArrayList<GettersSetters> authordetailsList) {
        this.c = c;
        this.authordetailsList = authordetailsList;
    }

    @Override
    public int getCount() {return authordetailsList.size();}
    @Override
    public Object getItem(int pos) {return authordetailsList.get(pos);}
    @Override
    public long getItemId(int pos) {return pos;}
    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.row_model,viewGroup,false);
        }

        TextView txtName = view.findViewById(R.id.nametxtview);


        final ImageView authorimage = view.findViewById(R.id.imageview);


        final GettersSetters pdf= (GettersSetters) this.getItem(pos);

        txtName.setText(pdf.getAuthorName());

        //fetch image from online using picasso
        PicassoClient.loadImage(pdf.getAuthorimage(),authorimage);

        //open author profile
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=pdf.getAuthorName();
                String AuthorId=pdf.getAuthorId();
                String description=pdf.getAuthordescription();
                String authorimage=pdf.getAuthorimage();
                String author_email=pdf.getAuthor_email();



                Intent authordetailsintent = new Intent(c,AuthorProfile.class);

                authordetailsintent.putExtra("author_name", name);
                authordetailsintent.putExtra("author_id", AuthorId);
                authordetailsintent.putExtra("description", description);
                authordetailsintent.putExtra("author_image",authorimage);
                authordetailsintent.putExtra("author_email",author_email);

                c.startActivity(authordetailsintent);

            }
        });



        return view;
    }
}
