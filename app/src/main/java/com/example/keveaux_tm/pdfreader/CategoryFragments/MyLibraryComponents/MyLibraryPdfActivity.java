package com.example.keveaux_tm.pdfreader.CategoryFragments.MyLibraryComponents;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.keveaux_tm.pdfreader.Main.MainActivity;
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

public class MyLibraryPdfActivity extends Activity implements OnLoadCompleteListener, OnPageErrorListener {

    ProgressBar pdfViewProgressbar;
    int defaultpage = 0;
    SharedPreferences defaultPagePref, percentagereadpref;
    SharedPreferences.Editor editor;
    String bookname, pdfurlpath;
    int numOfPages;
    double percentageofbookread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        //init shared prefs that store default page and percentage read
        defaultPagePref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        percentagereadpref = getApplicationContext().getSharedPreferences("MyPercent", MODE_PRIVATE);


        //unpack our data from intent
        Intent i = this.getIntent();
        pdfurlpath = Objects.requireNonNull(i.getExtras()).getString("pdfurl");
        bookname = Objects.requireNonNull(i.getExtras()).getString("bookname");


        //get the page the user was last in
        defaultpage = defaultPagePref.getInt(bookname, 0);


        final PDFView pdfView = findViewById(R.id.pdfView);
        pdfViewProgressbar = findViewById(R.id.pdfViewProgressBar);

        pdfViewProgressbar.setVisibility(View.VISIBLE);


        //load the pdfurlpath
        FileLoader.with(this).load(pdfurlpath, false)
                .fromDirectory(".My PDFs", FileLoader.DIR_INTERNAL).asFile(new FileRequestListener<File>() {
            @Override
            public void onLoad(FileLoadRequest fileLoadRequest, FileResponse<File> fileResponse) {
                pdfViewProgressbar.setVisibility(View.GONE);
                File pdf_file = fileResponse.getBody();

                try {
                    //load the pdf documents
                    pdfView.fromFile(pdf_file).defaultPage(defaultpage).onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            //set the default page every time the page is changed and save it in prefs
                            editor = defaultPagePref.edit();
                            editor.putInt(bookname, page);
                            editor.apply();

                            //set the percentage read every time the page is changed and save it in prefs
                            percentageofbookread = ((double) (page + 1) / (double) numOfPages) * 100;
                            editor = percentagereadpref.edit();
                            editor.putInt(bookname, (int) percentageofbookread);
                            editor.apply();

                            int stepSize = 17;

                            //add ads after every 17 pages
                            if (page % stepSize == 0 && page != 0) {
                                //TODO add ads
                            }
                        }
                    })
                            .enableAnnotationRendering(true)
                            .enableSwipe(true)
                            .swipeHorizontal(true)
                            .onLoad(MyLibraryPdfActivity.this)
                            .pageSnap(true)
                            .pageFling(true)
                            .autoSpacing(true)
                            .password("hello")
                            .scrollHandle(new DefaultScrollHandle(MyLibraryPdfActivity.this))
                            .spacing(10)
                            .onPageError(MyLibraryPdfActivity.this)
                            .pageFitPolicy(FitPolicy.HEIGHT)
                            .load();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(FileLoadRequest fileLoadRequest, Throwable throwable) {

                //dismiss progressbar
                pdfViewProgressbar.setVisibility(View.GONE);
                Toast.makeText(MyLibraryPdfActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // create the book's shortcut in home page
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyLibraryPdfActivity.this, bookname + " shortcut added ", Toast.LENGTH_SHORT).show();
                //when shortcut is clicked this activity is opened
                Intent shortcutIntent = new Intent(getApplicationContext(),
                        MyLibraryPdfActivity.class);

                //pass url and default page
                shortcutIntent.putExtra("pdfurl", pdfurlpath);
                shortcutIntent.putExtra("default_page", defaultpage);

                shortcutIntent.setAction(Intent.ACTION_MAIN);

                Intent addIntent = new Intent();
                addIntent.putExtra("pdfurl", pdfurlpath);
                addIntent.putExtra("default_page", defaultpage);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, bookname);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                                R.drawable.book_icon));

                addIntent
                        .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                getApplicationContext().sendBroadcast(addIntent);
            }
        });
    }

    @Override
    public void loadComplete(int nbPages) {
        pdfViewProgressbar.setVisibility(View.GONE);
        //set number of pages the book has
        numOfPages = nbPages;
    }

    @Override
    public void onPageError(int page, Throwable t) {
        pdfViewProgressbar.setVisibility(View.GONE);
        Toast.makeText(MyLibraryPdfActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MyLibraryPdfActivity.this, MainActivity.class);
        intent.putExtra("EXTRA", "openMyLibraryFragment");
        startActivity(intent);
        super.onBackPressed();
    }
}
