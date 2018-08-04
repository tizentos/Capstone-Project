package ltd.boku.distail.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<String> productIds=new ArrayList<>();

    public Cart() {
    }

    public Cart(List<String> productIds) {
        this.productIds = productIds;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }
}
