package ltd.boku.distail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.distail.adapter.CartRecyclerAdapter;
import ltd.boku.distail.firebaseDbManagement.CartDbManagement;
import ltd.boku.distail.model.Cart;
import ltd.boku.distail.model.Product;
import ltd.boku.distail.model.User;

import static ltd.boku.distail.fragment.ReviewFragment.CURRENT_USER;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";

    List<String> productIds=new ArrayList<>();
    int count;
    List<Product> mProducts=new ArrayList<>();

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference UserCartReference;

    RecyclerView cartRecyclerView;
    Button checkOutButton;

    CartRecyclerAdapter cartRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //TODO check this intent extra
        User currentUser= (User) getIntent().getSerializableExtra(CURRENT_USER);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        UserCartReference = mFirebaseDatabase.getReference("Cart").child(currentUser.getUserId());

        CartViewModel cartViewModel= ViewModelProviders.of(this).get(CartViewModel.class);
        InitializeUIData(cartViewModel);


        checkOutButton=findViewById(R.id.cart_check_out_button);
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        setupRecyclerView();

    }


    private void InitializeUIData(CartViewModel cartViewModel) {
        final CartDbManagement cartDbManagement=new CartDbManagement(UserCartReference,mFirebaseDatabase,cartViewModel,this);

        cartDbManagement.getProductIds();
        cartViewModel.productIdsLiveData.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                Log.d(TAG, "onChanged:  " + strings.toString());
                productIds=strings;
                count =productIds.size();
                cartDbManagement.getProducts();
            }
        });

        cartViewModel.productsLiveData.observe(this, new Observer<Product>() {
            @Override
            public void onChanged(@Nullable Product product) {
                Log.d(TAG, "onChanged:  " + product.toString());
                synchronized (this) {
                    if (count >= 0) {
                        mProducts.add(product);
                        count--;
                        if (count == 0) {
                            Toast.makeText(CartActivity.this, "Populate Recycler View", Toast.LENGTH_SHORT).show();
                            if (cartRecyclerAdapter!=null) cartRecyclerAdapter.setProducts(mProducts);
                        }
                    }
                }
            }
        });
    }

    private void setupRecyclerView() {
        cartRecyclerView=findViewById(R.id.cart_recycler_view);
        cartRecyclerAdapter=new CartRecyclerAdapter(this);
        cartRecyclerView.setAdapter(cartRecyclerAdapter);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        cartRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
