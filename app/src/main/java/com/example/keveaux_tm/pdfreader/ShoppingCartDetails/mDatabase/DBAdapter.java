package com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
    Context c;
    SQLiteDatabase db;

    DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper=new DBHelper(c);
    }
    //OPEN
    public DBAdapter openDB()
    {
        try {
            db=helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    //CLOSE
    public void closeDB()
    {
        try {
            helper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SAVE
    public long add(String name, String url,String price,String id,String uid)
    {
        try {
            ContentValues cv=new ContentValues();
            cv.put(Constants.NAME,name);
            cv.put(Constants.URL, url);
            cv.put(Constants.PRICE,price);
            cv.put(Constants.BOOK_ID,id);
            cv.put(Constants.UID,uid);

            db.insert(Constants.TB_NAME, Constants.ROW_ID, cv);

            return 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void deleteAll(){
        try {
            db = helper.getWritableDatabase();
            db.execSQL("delete from " +Constants.TB_NAME);
            db.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Delete User Details
    public void DeleteUser(int userid){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(Constants.TB_NAME, Constants.ROW_ID+" = ?",new String[]{String.valueOf(userid)});
        db.close();
    }

    //RETRIEVE
    public Cursor getBooks()
    {
        String[] columns={Constants.ROW_ID,Constants.NAME,Constants.URL,Constants.PRICE};

        return db.query(Constants.TB_NAME,columns,null,null,null,null,null);
    }


    public long getProfilesCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, Constants.TB_NAME);
        db.close();
        return count;
    }

    public Cursor getprice(){
        String[] columns={Constants.PRICE};
        return db.query(Constants.TB_NAME,columns,null,null,null,null,null);

    }
}
