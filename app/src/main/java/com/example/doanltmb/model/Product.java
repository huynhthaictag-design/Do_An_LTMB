package com.example.doanltmb.model;

public class Product {
    private int id; // ID này rất quan trọng để sửa lỗi symbol
    private String name;
    private String price;
    private String imageUrl;
    private String description;

    public Product(String name, String price, String imageUrl, String description) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // Các hàm Getter và Setter để DatabaseHelper gọi được
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}