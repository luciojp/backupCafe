package com.cafesuspenso.ufcg.cafesuspenso.Model;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private int id;
    private String description, image, name;
    private Double price;
    private Date date;

    public Transaction(Integer id, String description, String image, Date date, String name, Double price){
        this.id = id;
        this.description = description;
        this.image = image;
        this.date = date;
        this.name = name;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
