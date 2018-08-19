package ltd.boku.distail.model;

public class Review {

    //TODO review id should equal the product key

    private String productPath;
    private String productId;
    private String description;
    private String userId;
    private String author;

    public Review() {
    }

    public Review(String productPath, String productId, String description, String userId,String author) {
        this.productPath = productPath;
        this.productId = productId;
        this.description = description;
        this.userId = userId;
        this.author=author;
    }

    public String getProductPath() {
        return productPath;
    }

    public void setProductPath(String productPath) {
        this.productPath = productPath;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
