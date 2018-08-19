package ltd.boku.distail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import ltd.boku.distail.model.Product;

public class CartViewModel extends AndroidViewModel {
    MutableLiveData<List<String>>  productIdsLiveData= new MutableLiveData<List<String>>();
    MutableLiveData<Product> productsLiveData=new MutableLiveData<>();

    public CartViewModel(@NonNull Application application) {
        super(application);

    }

    public MutableLiveData<List<String>> getProductIdsLiveData() {
        return productIdsLiveData;
    }

    public void setProductIdsLiveData(List<String> productIdsLiveData) {
        this.productIdsLiveData.setValue(productIdsLiveData);
    }
    public void setProductsLiveData(Product products){
        this.productsLiveData.setValue(products);
    }
}
