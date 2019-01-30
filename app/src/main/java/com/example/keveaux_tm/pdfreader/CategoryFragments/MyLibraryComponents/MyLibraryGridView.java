package com.example.keveaux_tm.pdfreader.CategoryFragments.MyLibraryComponents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.keveaux_tm.pdfreader.gridViewAdapter.GettersSetters;
import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.mPicasso.PicassoClient;

import java.util.ArrayList;

public class MyLibraryGridView extends BaseAdapter {

    Context c;
    ArrayList<GettersSetters> myLibraryList;

    public MyLibraryGridView(Context c, ArrayList<GettersSetters> pdfDocuments) {
        this.c = c;
        this.myLibraryList = pdfDocuments;
    }
    @Override
    public int getCount() {return myLibraryList.size();}
    @Override
    public Object getItem(int pos) {return myLibraryList.get(pos);}
    @Override
    public long getItemId(int pos) {return pos;}
    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.row_model,viewGroup,false);
        }

        final GettersSetters pdf= (GettersSetters) this.getItem(pos);


        //get percentage of the book read from shared preferences set in MyLibraryPdfActivity
        SharedPreferences mPrefs = c.getSharedPreferences("MyPercent",0);
        final int percent = mPrefs.getInt(pdf.getName(), 0);


        ProgressBar progressBar=view.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(percent);


        TextView txtName = view.findViewById(R.id.nametxtview);
        TextView txtAuthor = view.findViewById(R.id.authortxt);
        ImageView pdfIcon = view.findViewById(R.id.imageview);


        txtName.setText(pdf.getName());
        txtAuthor.setText(pdf.getAuthorName());

        //load icon using picasso
        PicassoClient.loadImage(pdf.getPdfIconURL(),pdfIcon);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name=pdf.getName();
                String pdfurl=pdf.getPdfURL();
                String pdficonurl=pdf.getPdfIconURL();
                Intent i = new Intent(c,MyLibraryPdfActivity.class);


                i.putExtra("pdfurl",pdfurl);
                i.putExtra("bookname",name);
                i.putExtra("iconurl",pdficonurl);
                c.startActivity(i);

                }
        });
        return view;
    }
}
