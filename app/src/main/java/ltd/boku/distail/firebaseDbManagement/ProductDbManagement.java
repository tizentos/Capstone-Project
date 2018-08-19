package ltd.boku.distail.firebaseDbManagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ltd.boku.distail.CartViewModel;
import ltd.boku.distail.model.Product;

public class ProductDbManagement {

    private DatabaseReference productReference;
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;
    private CartViewModel mCartViewModel;

    Product product;
    Context mContext;

    public ProductDbManagement(DatabaseReference databaseReference, Context context){
        productReference=databaseReference;

    }
    public ProductDbManagement(DatabaseReference databaseReference, CartViewModel cartViewModel,Context context){
        productReference=databaseReference;
        mCartViewModel =cartViewModel;
        mContext=context;
    }

    public  void getProductById(String productId){
        //TODO orderByKey()
        productReference.orderByKey().equalTo(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot localProduct : dataSnapshot.getChildren()){
                    product=localProduct.getValue(Product.class);
                    mCartViewModel.setProductsLiveData(product);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  void insertProduct(Product product){
        productReference.push().setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Product added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Product adding failed!!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public  void removeProduct(String productId){
        productReference.child(productId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Product removed successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Product can't be removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  void updateProduct(Product product){

    }

}