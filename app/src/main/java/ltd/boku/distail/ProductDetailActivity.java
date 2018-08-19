package ltd.boku.distail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ltd.boku.distail.databinding.ActivityProductDetailBinding;
import ltd.boku.distail.model.Cart;
import ltd.boku.distail.model.Product;
import ltd.boku.distail.model.User;
import ltd.boku.distail.viewpagerAdapter.TabViewPagerAdapter;

import static ltd.boku.distail.MainActivity.IN_CART;
import static ltd.boku.distail.MainActivity.PRODUCT_DETAIL_BUNDLE;
import static ltd.boku.distail.MainActivity.PRODUCT_KEY;
import static ltd.boku.distail.adapter.ProductFirebaseRecyclerAdapter.PRODUCT;
import static ltd.boku.distail.fragment.ReviewFragment.CURRENT_USER;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";


    private static final int RC_SIGN_IN = 1;
    Product currentProduct;
    User currentUser;
    Cart UserCart;
    String productKey;
    boolean inCart=false;
    List<String> productIds = new ArrayList<>();

    DatabaseReference UserCartReference;
    FirebaseDatabase mFirebaseDatabase;
    FirebaseUser user;

    ActivityProductDetailBinding activityProductDetailBinding;
    private TabViewPagerAdapter mTabViewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProductDetailBinding= DataBindingUtil.setContentView(this,R.layout.activity_product_detail);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        Bundle bundle=null;

        if (getIntent().hasExtra(PRODUCT_DETAIL_BUNDLE)){
            bundle=getIntent().getBundleExtra(PRODUCT_DETAIL_BUNDLE);
            currentProduct=(Product) bundle.getSerializable(PRODUCT);
            currentUser=(User)bundle.getSerializable(CURRENT_USER);
            inCart=bundle.getBoolean(IN_CART,false);
            productKey = bundle.getString(PRODUCT_KEY, null);
            UserCartReference=mFirebaseDatabase.getReference("Cart").child(currentUser.getUserId());
        }

        loadCart();
        Picasso.with(this).load(currentProduct.getPhotoUrl())
                .placeholder(R.drawable.placeholder)
                .into(activityProductDetailBinding.productDetailImage);
        updateFavoriteButton();

        activityProductDetailBinding.productDetailPrice.
                setText(getString(R.string.product_item_price,currentProduct.getCurrency(),currentProduct.getPrice()));

        activityProductDetailBinding.productDetailRating.setRating(currentProduct.getRating());

        mTabViewPagerAdapter =new TabViewPagerAdapter(getSupportFragmentManager(), bundle);
        activityProductDetailBinding.viewPager.setAdapter(mTabViewPagerAdapter);
        activityProductDetailBinding.tabs.setupWithViewPager(activityProductDetailBinding.viewPager);


        activityProductDetailBinding.productDetailFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inCart){
                    UserCart.getProductIds().remove(productKey);
                    UserCartReference.setValue(UserCart).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProductDetailActivity.this, "Remove in cart!", Toast.LENGTH_SHORT).show();
                            inCart=false;
                            updateFavoriteButton();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductDetailActivity.this, "Can not remove in Cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    UserCart.getProductIds().add(productKey);
                    UserCartReference.setValue(UserCart).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProductDetailActivity.this, "Added to  cart!", Toast.LENGTH_SHORT).show();
                            inCart=true;
                            updateFavoriteButton();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductDetailActivity.this, "Can not add to Cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        activityProductDetailBinding.productDetailPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO goto ravepay layout
                Toast.makeText(ProductDetailActivity.this, "Goto RavePay Layout", Toast.LENGTH_SHORT).show();
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if (user.isAnonymous()){
                        //TODO login using real means
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
//                                        .setTheme()
                                        .setIsSmartLockEnabled(false)
                                        .enableAnonymousUsersAutoUpgrade()   //TODO auto upgrade feature
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                                new AuthUI.IdpConfig.PhoneBuilder().build()
                                        ))
                                        .build(),
                                RC_SIGN_IN);
                    } else{
                        //TODO proceed to ravepay
                        String name=user.getDisplayName();
                        String email=user.getEmail();
                        gotoPaymentPlatform(name,email);
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "No User sign-in", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void gotoPaymentPlatform(String name, String email){
        new RavePayManager(ProductDetailActivity.this)
                .setCountry("NG")
                .setAmount(currentProduct.getPrice())
                .setCurrency(currentProduct.getCurrency())
                .setfName(name)
                .setlName("")
                .setSecretKey("FLWSECK-d2d4c008883e5d6a5aad1a750bf34751-X")
                .setPublicKey("FLWPUBK-1d603dff60b51053f74d8b11b75b5234-X")
                .setEmail(email)
                .allowSaveCardFeature(true)
                .withTheme(R.style.RavePayTheme)
                .initialize();
    }


    private void updateFavoriteButton() {
        if (!inCart) {
            activityProductDetailBinding.productDetailFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_48dp));
        } else {
            activityProductDetailBinding.productDetailFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_48dp));
        }
    }

    private void loadCart() {
//        Query  query=UserCartReference.orderByChild(user.getUid());
//        ChildEventListener childEventListener=query.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        UserCartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    UserCartReference.setValue(new Cart());
                    UserCart = new Cart(productIds);
                } else {
                    UserCart = new Cart();
                    for (DataSnapshot productId : dataSnapshot.getChildren()) {
                        UserCart.setProductIds((ArrayList<String>) productId.getValue());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
                //TODO show dialog
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == RC_SIGN_IN){
            IdpResponse idpResponse=IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();


                //TODO check this getUser() method
                com.firebase.ui.auth.data.model.User user=idpResponse.getUser();
                gotoPaymentPlatform(user.getName(),user.getEmail());
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
            } else{
                if (idpResponse.getError().getErrorCode() == ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT){
                    AuthCredential nonAnonymousCredential =idpResponse.getCredentialForLinking();
                    Toast.makeText(this, "Error linking account", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
