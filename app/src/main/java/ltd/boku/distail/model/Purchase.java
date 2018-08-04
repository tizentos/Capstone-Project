package ltd.boku.distail.model;

public class Purchase {
//    private String _id
    private String customerId;
    private String merchantId;
    private String productName;
    private int quantity;
    private int price;

    public Purchase() {
    }

    public Purchase(String customerId, String merchantId, String productName, int quantity, int price) {
        this.customerId = customerId;
        this.merchantId = merchantId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
