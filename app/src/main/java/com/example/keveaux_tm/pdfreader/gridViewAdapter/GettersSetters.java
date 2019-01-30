package com.example.keveaux_tm.pdfreader.gridViewAdapter;

//getter and setter methods to fetch info from db
public class GettersSetters {
    String id;
    String name, authorname,pdfURL,pdfIconURL,description,price,authorId,authorimage,authordescription,author_email;


    /*online server getters and setters*/


    //get and set UserId
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    //get and set Book name
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    //get and set Author name
    public String getAuthorName() {return authorname;}
    public void setAuthorName(String category) {this.authorname = category;}

    //get and set pdf url stored in the db
    public String getPdfURL() {return pdfURL;}
    public void setPdfURL(String pdfURL) {this.pdfURL = pdfURL;}

    //get and set the pdf icon url stored in the server
    public String getPdfIconURL() {return pdfIconURL;}
    public void setPdfIconURL(String pdfIconURL) {this.pdfIconURL = pdfIconURL;}

    //get and set the book description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    /**/


    //get and set the book price
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    //get and set author id
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    public String getAuthorId() {
        return authorId;
    }

    //get and set author image
    public void setauthorimage(String authorimage) {
        this.authorimage = authorimage;
    }
    public String getAuthorimage() {
        return authorimage;
    }

    //get and set author description
    public String getAuthordescription() {
        return authordescription;
    }
    public void setAuthordescription(String authordescription) {
        this.authordescription = authordescription;
    }

    //get and set author email
    public void setAuthor_email(String author_email) {
        this.author_email = author_email;
    }
    public String getAuthor_email() {
        return author_email;
    }
    /**/
}
