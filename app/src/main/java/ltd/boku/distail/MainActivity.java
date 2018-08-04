package ltd.boku.distail;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.distail.adapter.ProductFirebaseRecyclerAdapter;
import ltd.boku.distail.model.Cart;
import ltd.boku.distail.model.Product;
import ltd.boku.distail.ui_helper.ItemDecorationProductList;

public class MainActivity extends AppCompatActivity{

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference ProductDatabaseReference;
    DatabaseReference UserDatabaseReference;
    DatabaseReference UserCartReference;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    ltd.boku.distail.model.User mUser;
    Cart UserCart;

    private static final String TAG = "MainActivity";

    RecyclerView ProductListRecyclerView;
    ProductFirebaseRecyclerAdapter productFirebaseRecyclerAdapter;
    private SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        ProductDatabaseReference=mFirebaseDatabase.getReference().child("Product");
        UserDatabaseReference=mFirebaseDatabase.getReference().child("user");


        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser=mFirebaseAuth.getCurrentUser();
                Log.d(TAG, "onAuthStateChanged: entering");
                if (firebaseUser != null){
                    Toast.makeText(getApplicationContext(), "Logged in already", Toast.LENGTH_SHORT).show();
                    initializeUser(firebaseUser);
                    setupRecyclerView();
                } else{
                    mFirebaseAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d(TAG, "onSuccess: entering");
                            FirebaseUser firebaseUser=mFirebaseAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),"Log-in anonymously",Toast.LENGTH_LONG).show();
                            assert firebaseUser != null;
                            initializeUser(firebaseUser);
                            setupRecyclerView();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Unable to Log-in", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        };

       // seedProductData();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initializeUser(FirebaseUser firebaseUser) {
        Log.d(TAG, "initializeUser: entering");

        mUser=new ltd.boku.distail.model.User(firebaseUser.getUid(),"anonymous");
        UserDatabaseReference.child(mUser.getUserId()).setValue(mUser);

        UserCartReference=mFirebaseDatabase.getReference("Cart").child(mUser.getUserId());

        UserCartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: entering");
                List<String> productIds=new ArrayList<>();
                if (!dataSnapshot.exists()){
                    UserCartReference.setValue(new Cart());
                    Log.d(TAG, "onDataChange: set cart");
                    UserCart=new Cart(productIds);
                }else{
                    UserCart=new Cart();
                    for (DataSnapshot productId: dataSnapshot.getChildren()){
                        UserCart.setProductIds((ArrayList<String>)productId.getValue());
                    }
                    Log.d(TAG, "onDataChange: set cart");
                    if (productFirebaseRecyclerAdapter != null){
                        productFirebaseRecyclerAdapter.setCart(UserCart);
                        productFirebaseRecyclerAdapter.notifyDataSetChanged();
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: error oh");
            }
        });
    }

    private void setupRecyclerView() {
        Query query= ProductDatabaseReference.orderByKey();

        FirebaseRecyclerOptions options= new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class)
                .setLifecycleOwner(this)
                .build();

        productFirebaseRecyclerAdapter=new ProductFirebaseRecyclerAdapter(options,this,UserCart,UserCartReference);

        ProductListRecyclerView=findViewById(R.id.product_list_recycler);

        ProductListRecyclerView.setLayoutManager(new GridLayoutManager(this,2));

//        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//        ProductListRecyclerView.addItemDecoration(new ItemDecorationProductList(
//                8,2
//        ));

        ProductListRecyclerView.setAdapter(productFirebaseRecyclerAdapter);

    }

    public void seedProductData(){
        for (int i =0; i<50; i++ ){
            ProductDatabaseReference.push().setValue(new Product("Tosin",2,"Another product",500,"$",4,
                    "34534534fv","Men",
                    "https://firebasestorage.googleapis.com/v0/b/friendlychat-1af6c.appspot.com/o/chat_photos%2Fsample%202.PNG?alt=media&token=05036ac8-a8d9-4d17-94da-744d09a9e72b"));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);

        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
        mSearchView=(SearchView)menu.findItem(R.id.app_bar_search).getActionView();
        SearchableInfo searchableInfo=searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);
        mSearchView.setIconified(true);
        mSearchView.setQueryHint("Product name");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                mSearchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem menuItem=menu.findItem(R.id.app_bar_search);
        final MenuItem cartMenu=menu.findItem(R.id.menu_cart);
        cartMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                cartMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
                return true;
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
               cartMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
