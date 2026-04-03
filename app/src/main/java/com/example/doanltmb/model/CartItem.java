package com.example.doanltmb.model;

public class CartItem {
    private int cartId;
    private String productName;
    private double price;
    private String imageUrl;
    private int quantity;

    public CartItem(int cartId, String productName, double price, String imageUrl, int quantity) {
        this.cartId = cartId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public int getCartId() { return cartId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}