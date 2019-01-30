package com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDatabase;

public class Constants {
    //COLUMNS
    static final String ROW_ID="id";
    static final String NAME="name";
    static final String URL="url";
    static final String PRICE ="PRICE";
    static final String BOOK_ID ="bookid";
    static final String UID ="uid";

    //DB PROPERTIES
    static final String DB_NAME="book_DB";
    public static final String TB_NAME="book_TB";
    static final int DB_VERSION=3;

    //CREATE TABLE STMT
    static final String CREATE_TB="CREATE TABLE book_TB(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT NOT NULL,url TEXT NOT NULL,price INTEGER NOT NULL,bookid TEXT NOT NULL," +
            "uid TEXT NOT NULL );";

    //UPGRADE TB
    static final String UPGRADE_TB="DROP TABLE IF EXISTS "+TB_NAME;

}
