package com.example.keveaux_tm.pdfreader.BookDetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.keveaux_tm.pdfreader.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.util.Objects;

public class PDFActivity extends Activity implements OnLoadCompleteListener,OnPageErrorListener {

    ProgressBar pdfViewProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        //unpack our data from intent
        Intent i=this.getIntent();
        final String path= Objects.requireNonNull(i.getExtras()).getString("PATH");

        final PDFView pdfView=findViewById(R.id.pdfView);
        pdfViewProgressbar=findViewById(R.id.pdfViewProgressBar);

        pdfViewProgressbar.setVisibility(View.VISIBLE);

        //load the path
        FileLoader.with(this).load(path,false)
                .fromDirectory("My PDFs",FileLoader.DIR_INTERNAL).asFile(new FileRequestListener<File>() {
            @Override
            public void onLoad(FileLoadRequest fileLoadRequest, FileResponse<File> fileResponse) {
                pdfViewProgressbar.setVisibility(View.GONE);
                File pdf_file = fileResponse.getBody();

                try {

                    int pages[]={0,1,2,3,4,5};


                    //load the pdf documents
                    pdfView.fromFile(pdf_file).pages(pages).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {

                        }
                          }).enableAnnotationRendering(true)
                            .enableSwipe(true)
                            .swipeHorizontal(true)
                            .onLoad(PDFActivity.this)
                            .pageSnap(true)
                            .pageFling(true)
                            .autoSpacing(true)
                            .password("hello")
                            .scrollHandle(new DefaultScrollHandle(PDFActivity.this))
                            .spacing(10)
                            .onPageError(PDFActivity.this)
                            .pageFitPolicy(FitPolicy.HEIGHT)
                            .load();

                }catch (Exception e){
                        e.printStackTrace();
                }

            }



            @Override
            public void onError(FileLoadRequest fileLoadRequest, Throwable throwable) {

                //dismiss progressbar
                pdfViewProgressbar.setVisibility(View.GONE);
                Toast.makeText(PDFActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void loadComplete(int nbPages) {
        pdfViewProgressbar.setVisibility(View.GONE);
        Toast.makeText(PDFActivity.this,String.valueOf(nbPages),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPageError(int page, Throwable t) {
        pdfViewProgressbar.setVisibility(View.GONE);
        Toast.makeText(PDFActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
    }

}
