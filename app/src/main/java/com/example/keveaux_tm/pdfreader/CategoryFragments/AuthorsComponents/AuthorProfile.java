package com.example.keveaux_tm.pdfreader.CategoryFragments.AuthorsComponents;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.keveaux_tm.pdfreader.Main.MainActivity;
import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.SendData;
import com.example.keveaux_tm.pdfreader.mPicasso.PicassoClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class AuthorProfile extends Activity {

    LinearLayout authorBooksLayout;
    Button followMeBtn;
    private FirebaseAuth mAuth;
    String author_id;
    String user_id;
    TextView followerstv,bookscounttv;
    String author_name,description,author_image,author_email;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_profile);


        //initialise firebase auth
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser user = mAuth.getCurrentUser();

        //firebase uid
        user_id =user.getUid();

        //get strings from authorgridview using bundles
        author_name = getIntent().getExtras().getString("author_name");
        author_id = getIntent().getExtras().getString("author_id");
        description = getIntent().getExtras().getString("description");
        author_image = getIntent().getExtras().getString("author_image");
        author_email = getIntent().getExtras().getString("author_email");



        authorBooksLayout =findViewById(R.id.books);
        followMeBtn =findViewById(R.id.followme);
        followerstv=findViewById(R.id.followerstv);
        bookscounttv=findViewById(R.id.bookscount);

        TextView author_name_tv=findViewById(R.id.authorname);
        author_name_tv.setText(author_name);

        TextView author_email_tv=findViewById(R.id.author_email);
        author_email_tv.setText(author_email);

        TextView about_author=findViewById(R.id.about_author);
        about_author.setText(description);

        ImageView authorimageview=findViewById(R.id.authorimage);

        //go to specific author's books
        authorBooksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AuthorProfile.this,BooksByAuthorActivity.class);
                intent.putExtra("authorId",author_id);
                startActivity(intent);
            }
        });

        //load image from picasso
        PicassoClient.loadImage(author_image,authorimageview);

        author_email_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ author_email });
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                i.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(i, "Send email"));
            }
        });


        followjsondownloader("http://104.248.124.210/pdfwork/pdfwork/fetchfollowmes.php?uid=" + user_id+"&id="+author_id);
        countbooksandfollowers("http://104.248.124.210/pdfwork/pdfwork/count.php?id=" + author_id,followerstv);
        countbooksandfollowers("http://104.248.124.210/pdfwork/pdfwork/countbooks.php?id=" + author_id,bookscounttv);

    }

    //implement follow and un follow author
    public void followjsondownloader(final String loginURL) {


        requestQueue = Volley.newRequestQueue(AuthorProfile.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, loginURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {

                        //not following
                        if(response.isNull("results")){
                            followMeBtn.setVisibility(View.VISIBLE);
                            followMeBtn.setBackgroundResource(R.drawable.buttonstyle);
                            followMeBtn.setText("follow me");
                            followMeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    JSONArray resultSet     = new JSONArray();

                                    JSONObject rowObject = new JSONObject();

                                    try {
                                        rowObject.put("uid",user_id);
                                        rowObject.put("authorid",author_id);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    resultSet.put(rowObject);


                                    String post="http://104.248.124.210/pdfwork/pdfwork/sendfollowmes.php";

                                    SendData.SendData(resultSet.toString(),post);

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            followjsondownloader("http://104.248.124.210/pdfwork/pdfwork/fetchfollowmes.php?uid=" + user_id+"&id="+author_id);
                                            countbooksandfollowers("http://104.248.124.210/pdfwork/pdfwork/count.php?id=" + author_id,followerstv);
                                            countbooksandfollowers("http://104.248.124.210/pdfwork/pdfwork/countbooks.php?id=" + author_id,bookscounttv);

                                        }
                                    }, 400);

                                    }

                            });


                        }
                        //following
                        else {
                            followMeBtn.setVisibility(View.VISIBLE);
                            followMeBtn.setBackgroundResource(R.drawable.buttonstylefollowing);
                            followMeBtn.setText("following");
                            followMeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    unfollow("http://104.248.124.210/pdfwork/pdfwork/unfollow.php?uid=" + user_id+"&id="+author_id);
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Do something after 100ms
                                            followjsondownloader("http://104.248.124.210/pdfwork/pdfwork/fetchfollowmes.php?uid=" + user_id+"&id="+author_id);
                                            countbooksandfollowers("http://104.248.124.210/pdfwork/pdfwork/count.php?id=" + author_id,followerstv);
                                            countbooksandfollowers("http://104.248.124.210/pdfwork/pdfwork/countbooks.php?id=" + author_id,bookscounttv);

                                        }
                                    }, 400);

                                }
                            });

                            }

                            }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }

        );
        requestQueue.add(jor);


    }

    //delete follow info in db
    public void unfollow(String url){
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

    }
},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }

        );

        requestQueue.add(jor);
    }

    //count number of books and followers the author has
    public void countbooksandfollowers(String url, final TextView tv){
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Iterator<String> keys= response.keys();
                        while (keys.hasNext())
                        {
                            String keyValue = keys.next();
                            try {
                                String valueString = response.getString(keyValue);
                                tv.setText(valueString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }

        );

        requestQueue.add(jor);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (AuthorProfile.this, MainActivity.class);
        intent.putExtra("EXTRA", "openAuthors");
        startActivity(intent);
        super.onBackPressed();
    }
}