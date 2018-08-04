package ltd.boku.distail.model;

public class Review {

    //TODO review id should equal the product key

    private String productPath;
    private String productId;
    private String description;
    private String userId;

    public Review() {
    }

    public Review(String productPath, String productId, String description, String userId) {
        this.productPath = productPath;
        this.productId = productId;
        this.description = description;
        this.userId = userId;
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
}
