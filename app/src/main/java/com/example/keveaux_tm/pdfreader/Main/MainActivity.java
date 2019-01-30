package com.example.keveaux_tm.pdfreader.Main;

import com.example.keveaux_tm.pdfreader.CategoryFragments.AuthorsComponents.AuthorsFragment;
import com.example.keveaux_tm.pdfreader.CategoryFragments.MyLibraryComponents.MyLibraryFragment;
import com.example.keveaux_tm.pdfreader.CategoryFragments.categories.MotivationalBooksFragment;
import com.example.keveaux_tm.pdfreader.CategoryFragments.categories.PoemsFragment;
import com.example.keveaux_tm.pdfreader.CategoryFragments.categories.ShortStoriesFragment;
import com.example.keveaux_tm.pdfreader.CheckInternetConnection.NoInternet;
import com.example.keveaux_tm.pdfreader.R;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase.DBAdapter;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mShoppingCartActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.keveaux_tm.pdfreader.FirebaseSignup.SignUpActivity;
import com.example.keveaux_tm.pdfreader.ShoppingCartDetails.shoppingCartIconCount.CountDrawable;
import com.example.keveaux_tm.pdfreader.search.*;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigation;
    Menu menu;
    DBAdapter dbAdapter;
    private FirebaseAuth mAuth;
    boolean doubleBackToExitPressedOnce = false;


    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private SimpleCursorAdapter myAdapter;

    SearchView searchView = null;
    private String[] strArrData = {"No Suggestions"};



    //change fragment on nav drawer item click
    public void changebooksfragment(android.support.v4.app.Fragment o) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentcontainer, o);
        fragmentTransaction.commit();
        drawerLayout.closeDrawers();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //init dbadapter
        dbAdapter = new DBAdapter(MainActivity.this);

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        //method that creates the nav drawer
        initInstances();

        //method that loads the language saved in shared preferences
        LoadLocale();


        final String[] from = new String[]{"bookName"};
        final int[] to = new int[]{android.R.id.text1};

        // setup SimpleCursorAdapter
        myAdapter = new SimpleCursorAdapter(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // Fetch data from mysql table using AsyncTask
        new AsyncFetch().execute();

        //first fragment to load in the main actiity will be all books fragment
        changebooksfragment(new AllBooksFragment());

        //check which fragment to open when the mainactivity receives an intent
        switch (getIntent().getStringExtra("EXTRA")) {

            //open my library fragment
            case "openMyLibraryFragment":
                changebooksfragment(new MyLibraryFragment());
                break;
            case "":
                changebooksfragment(new AllBooksFragment());
                break;
                //open authors fragment
            case "openAuthors":
                changebooksfragment(new AuthorsFragment());
                break;

        }
    }


    @Override
    protected void onRestart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        super.onRestart();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // adds inflator to action bar
        getMenuInflater().inflate(R.menu.search_main, menu);

        this.menu = menu;

        MenuItem shoppingcart = menu.findItem(R.id.shopping_cart);


        //open shopping cart activity when icon is clicked
        shoppingcart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent viewShoppingCartIntent = new Intent(getBaseContext(), mShoppingCartActivity.class);
                startActivity(viewShoppingCartIntent);
                return false;
            }
        });

        // Get Search inflator from action bar and Get Search service
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setIconified(true);
            searchView.setSuggestionsAdapter(myAdapter);

            // Getting selected (clicked) suggestion
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionClick(int position) {

                    // Add clicked text to search box
                    CursorAdapter ca = searchView.getSuggestionsAdapter();
                    Cursor cursor = ca.getCursor();
                    cursor.moveToPosition(position);
                    searchView.setQuery(cursor.getString(cursor.getColumnIndex("bookName")), false);
                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String s) {
                    final ProgressDialog pd = new ProgressDialog(MainActivity.this);

                    NoInternet noInternet = new NoInternet();

                    if (!noInternet.isConnected(MainActivity.this)) {

                        noInternet.builddialog(MainActivity.this).show();
                    } else {
                        pd.setMessage("loading");
                        pd.show();


                        Response.Listener<String> listener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    pd.dismiss();
                                    Intent i = new Intent(MainActivity.this, SearchBooksActivity.class);
                                    i.putExtra("bookname", s);
                                    startActivity(i);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };


                        SearchRequest searchRequest = new SearchRequest(s, listener);
                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                        queue.add(searchRequest);


                    }
                    return false;
                }

                //when text changes in the search view
                @Override
                public boolean onQueryTextChange(String s) {

                    // Filter data
                    final MatrixCursor mc = new MatrixCursor(new String[]{BaseColumns._ID, "bookName"});
                    for (int i = 0; i < strArrData.length; i++) {
                        if (strArrData[i].toLowerCase().startsWith(s.toLowerCase()))
                            mc.addRow(new Object[]{i, strArrData[i]});
                    }
                    myAdapter.changeCursor(mc);
                    return false;
                }
            });
        } else {
            searchView.setIconifiedByDefault(false);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;

        setCount(this, String.valueOf(dbAdapter.getProfilesCount()));
        return super.onPrepareOptionsMenu(menu);
    }

    public void setCount(Context context, String count) {
        MenuItem menuItem = menu.findItem(R.id.shopping_cart);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }


    // Every time when you press search button on keypad an Activity is recreated which in turn calls this function
    @Override
    protected void onNewIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }

            // User entered text and pressed search button. Perform task ex: fetching data from database and display

        }
    }


    // Create class AsyncFetch to fetch the suggestion list items in search view
    private class AsyncFetch extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides or your JSON file address
                url = new URL("http://104.248.124.210/pdfwork/pdfwork/fetch-all.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we receive data
                conn.setDoOutput(true);
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return ("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            ArrayList<String> dataList = new ArrayList<>();
            pdLoading.dismiss();


            if (result.equals("no rows")) {

                // Do some action if no data from database

            } else {

                try {

                    JSONArray jArray = new JSONArray(result);

                    // Extract data from json and store into ArrayList
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        dataList.add(json_data.getString("name"));
                    }

                    strArrData = dataList.toArray(new String[dataList.size()]);

                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                }

            }

        }

    }

    @Override
    protected void onStart() {

        super.onStart();
    }


    //change language to either kiswahili or english
    private void showchangelanguagedialog() {

        final String[] listitem = {"Kiswahili", "English"};
        //create an alert dialogue and set choice items
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(getResources().getString(R.string.choose_language));
        mBuilder.setSingleChoiceItems(listitem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    setLocale("sw");
                    recreate();
                } else if (i == 1) {
                    setLocale("en");
                    recreate();
                }

                dialog.dismiss();
            }
        });

        AlertDialog mdialog = mBuilder.create();
        mdialog.show();

    }


    //set the locale to shared preferences which will be retrieved in LoadLocale();
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext()
                .getResources().getDisplayMetrics());

        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

    }

    //load language saved in shared preferences
    public void LoadLocale() {
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);

    }


    //firebase logout
    public void logout() {
        GoogleSignInClient mGoogleSignInClient;

        // Google sign out
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        //sign out and open the sign up activity
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);

    }


    //share app url via various platforms
    public void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Here is the share content body";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Tell your friends about me ;)");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    //navigation drawer handler
    private void initInstances() {


        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        String separatedemail[] = email.split("@");


        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navemail = headerView.findViewById(R.id.email);
        TextView navUsername = headerView.findViewById(R.id.username);
        navemail.setText(email);
        navUsername.setText(separatedemail[0]);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        navigation = findViewById(R.id.navigation_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {

                    case R.id.change_language:
                        showchangelanguagedialog();
                        break;
                    case R.id.logout:
                        logout();
                        break;
                    case R.id.share:
                        share();
                        break;
                    case R.id.authors:
                        changebooksfragment(new AuthorsFragment());
                        break;
                    case R.id.all_books:
                        changebooksfragment(new AllBooksFragment());
                        break;
                    case R.id.short_stories:
                        changebooksfragment(new ShortStoriesFragment());
                        break;
                    case R.id.poems:
                        changebooksfragment(new PoemsFragment());
                        break;
                    case R.id.motivational:
                        changebooksfragment(new MotivationalBooksFragment());
                        break;
                    case R.id.my_library:
                        changebooksfragment(new MyLibraryFragment());
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


}
