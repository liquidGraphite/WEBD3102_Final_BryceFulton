package com.nscc.finance;

public class Transaction {
    private String type; // income or expense
    private String category;
    private double amount;
    private String date;
    private String note;
    private int id;

    // Constructor
    public Transaction() {
    }

    // Getters
    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    // Setters
    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setId(int id) {
        this.id = id;
    }

}
