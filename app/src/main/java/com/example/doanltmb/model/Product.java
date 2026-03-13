package com.example.doanltmb.model;

public class Product {
    private String name;
    private String price;
    // Tạm thời chưa cần ảnh thật, cứ tạo biến ở đây sau này dùng
    private String imageUrl;

    public Product(String name, String price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}