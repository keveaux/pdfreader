package com.example.keveaux_tm.pdfreader.ShoppingCartDetails.mDataObject;

public class mBooks {
    String name;
    String imageUrl;
    int price;
    int id;

    //Constructor
    public mBooks() {
    }


    //SqLite db getters and setters

    //get and set book name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //get and set the pdf url stored in the sqlite db
    public String getPdfURL() {
        return imageUrl;
    }
    public void setPdfURL(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    //get and set book's price
    public void setPrice(int price) {
        this.price = price;
    }
    public int getPrice() {
        return price;
    }

    //get and set Book id
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

}
