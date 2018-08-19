package ltd.boku.distail.firebaseDbManagement;


import android.content.Context;
import android.support.annotation.NonNull;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.distail.CartViewModel;
import ltd.boku.distail.model.Product;

public class CartDbManagement {

    private DatabaseReference cartReference;
    private FirebaseDatabase mFirebaseDatabase;
    CartViewModel mCartViewModel;
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;
    private List<String> productIds=new ArrayList<>();
    private List<Product> products=new ArrayList<>();

    private Context mContext;

    private static final String TAG = "CartDbManagement";

    public CartDbManagement(DatabaseReference cartDatabaseReference,FirebaseDatabase firebaseDatabase
            ,CartViewModel cartViewModel, Context context){
        cartReference=cartDatabaseReference;
        mFirebaseDatabase=firebaseDatabase;
        mCartViewModel=cartViewModel;

        mContext=context;

    }

    public void getProductIds(){
        cartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot id : dataSnapshot.getChildren()){
                    for (DataSnapshot productId: id.getChildren() ) {
                        productIds.add(productId.getValue(String.class));
                    }
                    mCartViewModel.setProductIdsLiveData(productIds);
                    //Log.d(TAG, "onChildAdded:  " + cart.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getProducts(){
        List<Product> products=new ArrayList<>();
       // productIds=getProductIds();
        for (String productId : productIds) {
            ProductDbManagement productDbManagement = new ProductDbManagement(mFirebaseDatabase.getReference("product"),mCartViewModel, mContext);
            productDbManagement.getProductById(productId);
        }
    }


    public void attachValueEventListener(){

    }
    public void attachChildEventListener(){

    }

    public void detachValueEventListener(){

    }

    public void detachChildEventListener(){

    }
}
