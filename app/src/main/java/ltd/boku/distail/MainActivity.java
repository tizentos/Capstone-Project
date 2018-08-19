package ltd.boku.distail;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import ltd.boku.distail.adapter.ProductFirebaseRecyclerAdapter;
import ltd.boku.distail.model.Cart;
import ltd.boku.distail.model.Product;

import static ltd.boku.distail.adapter.ProductFirebaseRecyclerAdapter.PRODUCT;
import static ltd.boku.distail.fragment.ReviewFragment.CURRENT_USER;

public class MainActivity extends AppCompatActivity implements ProductFirebaseRecyclerAdapter.OnItemClickListener{

    public static final String PRODUCT_DETAIL_BUNDLE="product_detail_bundle";
    public static final String PRODUCT_KEY="key";
    public static final String IN_CART="in_cart";
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference rootProductDatabaseReference;
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
        rootProductDatabaseReference =mFirebaseDatabase.getReference().child("product");
        UserDatabaseReference=mFirebaseDatabase.getReference().child("user");


        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser=mFirebaseAuth.getCurrentUser();
                Log.d(TAG, "onAuthStateChanged: entering");
                if (firebaseUser != null){
                    Toast.makeText(getApplicationContext(), "Logged in already", Toast.LENGTH_SHORT).show();
                    initializeUser(firebaseUser);
                    setupRecyclerView(null);
                } else{
                    mFirebaseAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d(TAG, "onSuccess: entering");
                            FirebaseUser firebaseUser=mFirebaseAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),"Log-in anonymously",Toast.LENGTH_LONG).show();
                            assert firebaseUser != null;
                            initializeUser(firebaseUser);
                            setupRecyclerView(null);
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

       //seedProductData();


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

        mUser=new ltd.boku.distail.model.User(firebaseUser.getUid(),"anonymous",firebaseUser.getDisplayName());
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

    private void setupRecyclerView(String searchQuery) {
        Query query;
        if (searchQuery ==null){
            query= rootProductDatabaseReference.orderByKey();
        } else{
            query= rootProductDatabaseReference.orderByChild("name")
                    .startAt(searchQuery).endAt(searchQuery+ "\uf8ff");
        }


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
        String[]  category={ "electronics", "men wear", "groceries", "ladies wear"};
        int[]  price ={ 100,200,300,400,500};
        String[] name = {"Tosin", "Clara", "Polo", "Crest","Phoenix"};

        for (int i =0; i<50; i++ ){

            int catIndex=(int) Math.round(Math.random()* (category.length-1));
            int priceIndex=(int) Math.round(Math.random()* (price.length-1));
            int nameIndex=(int) Math.round(Math.random()* (name.length-1));

            Log.d(TAG, "seedProductData: catindex: "+ catIndex + "priceIndex: " + priceIndex + "nameIndex" + nameIndex);
            Product product=new Product(name[nameIndex],2,"Another product",price[priceIndex],"USD",4,
                    "34534534fv",category[catIndex],
                    "https://firebasestorage.googleapis.com/v0/b/friendlychat-1af6c.appspot.com/o/chat_photos%2Fsample%202.PNG?alt=media&token=05036ac8-a8d9-4d17-94da-744d09a9e72b");
            rootProductDatabaseReference.push().setValue(product);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: entering");
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
        mSearchView.setMaxWidth(1000);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                mSearchView.clearFocus();
                setupRecyclerView(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        switch (id){
            case R.id.menu_cart:
                Intent intent=new Intent(this,CartActivity.class);
                intent.putExtra(CURRENT_USER,mUser);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemClickListener(Product model, boolean inCart,String key) {
        Intent intent=new Intent(this,ProductDetailActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(PRODUCT,model);
        bundle.putSerializable(CURRENT_USER,mUser);
        bundle.putBoolean(IN_CART,inCart);
        bundle.putString(PRODUCT_KEY,key);

        intent.putExtra(PRODUCT_DETAIL_BUNDLE,bundle);
        startActivity(intent);
    }

}
