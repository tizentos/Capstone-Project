package ltd.boku.distail.model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Product  implements Serializable {
    private String name;
    private int quantity;
    private String description;
    private double  price;
    private String  currency;
    private float rating;
    private String merchantId;
    private  String category;
    private String photoUrl;

    @Exclude
    private static final long serialVersionUID=1L;

    public Product() {
    }

    public Product(String name, int quantity, String description, double price, String currency, float rating, String merchantId, String category,String photoUrl) {
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.rating = rating;
        this.merchantId = merchantId;
        this.category = category;
        this.photoUrl=photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
