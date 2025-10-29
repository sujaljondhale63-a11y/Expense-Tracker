package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private String id;
    private LocalDate date;
    private String category;
    private double amount;
    private String description;
    
    public Expense(String id, LocalDate date, String category, double amount, String description) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }
    
    public Expense(LocalDate date, String category, double amount, String description) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDateString() {
        return date.format(DATE_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,₹%.2f,%s",
                id, getDateString(), category, amount, description);
    }

}
