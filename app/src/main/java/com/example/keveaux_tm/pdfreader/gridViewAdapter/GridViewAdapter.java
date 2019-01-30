package com.example.keveaux_tm.pdfreader.gridViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keveaux_tm.pdfreader.BookDetails.mBookDetails;
import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.mPicasso.PicassoClient;

import java.util.ArrayList;

/*
Our custom adapter class
 */
public class GridViewAdapter extends BaseAdapter {
    Context c;
    ArrayList<GettersSetters> pdfDocuments;

    public GridViewAdapter(Context c, ArrayList<GettersSetters> pdfDocuments) {
        this.c = c;
        this.pdfDocuments = pdfDocuments;
    }
    @Override
    public int getCount() {return pdfDocuments.size();}
    @Override
    public Object getItem(int pos) {return pdfDocuments.get(pos);}
    @Override
    public long getItemId(int pos) {return pos;}
    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.row_model,viewGroup,false);
        }

        TextView txtName = view.findViewById(R.id.nametxtview);
        TextView txtAuthor = view.findViewById(R.id.authortxt);
        final ImageView pdfIcon = view.findViewById(R.id.imageview);
//        Button previewButton=view.findViewById(R.id.preview);

        final GettersSetters pdf= (GettersSetters) this.getItem(pos);

        txtName.setText(pdf.getName());
        txtAuthor.setText(pdf.getAuthorName());

        //load image using picasso
        PicassoClient.loadImage(pdf.getPdfIconURL(),pdfIcon);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name=pdf.getName();
                String id=pdf.getId();
                String description=pdf.getDescription();
                String pdfurl=pdf.getPdfURL();
                String pdficonurl=pdf.getPdfIconURL();
                String price=pdf.getPrice();
                Intent bookDetailsIntent = new Intent(c,mBookDetails.class);

                bookDetailsIntent.putExtra("bookname", name);
                bookDetailsIntent.putExtra("id", id);
                bookDetailsIntent.putExtra("description", description);
                bookDetailsIntent.putExtra("url", pdfurl);
                bookDetailsIntent.putExtra("pdf_icon_url", pdficonurl);
                bookDetailsIntent.putExtra("price",price);
                c.startActivity(bookDetailsIntent);

            }
        });



        return view;
    }
}
