package ltd.boku.distail.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.distail.ProductDetailActivity;
import ltd.boku.distail.R;
import ltd.boku.distail.model.Cart;
import ltd.boku.distail.model.Product;

public class ProductFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Product,ProductFirebaseRecyclerAdapter.ProductViewHolder> {

    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private Cart cart;
    DatabaseReference CartReference;
    private List<String> productIds=new ArrayList<>();

    private static final String TAG = "ProductFirebaseRecycler";
    public static final String PRODUCT="product";

    public interface OnItemClickListener{
         void onItemClickListener(Product model,boolean inCart,String key);
    }

    public ProductFirebaseRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Product> options, Context context,Cart cart,DatabaseReference cartReference) {
        super(options);
        this.mContext=context;
        this.cart=cart;
        if (this.cart != null){
            productIds=cart.getProductIds();
        }

        mOnItemClickListener=(OnItemClickListener)context;

        CartReference=cartReference;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final Product model) {

        Picasso.with(mContext).load(Uri.parse(model.getPhotoUrl()))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.productImage);

        holder.favoriteButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));

        final String key = this.getRef(position).getKey();
        final boolean inCart=checkIfProductIncart(holder, key);


        holder.productRating.setRating(model.getRating());
        holder.productPrice.setText(mContext.getString(R.string.product_item_price, model.getCurrency(),model.getPrice()));
        holder.productName.setText(model.getName());


        final String itemKey= this.getRef(position).getKey();

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean inCart=checkIfProductIncart(holder, key);
                Toast.makeText(mContext, itemKey, Toast.LENGTH_SHORT).show();
                if (!inCart){
                    //TODO add new key
                    productIds.add(key);
                }else{
                    //TODO remove the key
                    productIds.remove(key);
                }
                cart.setProductIds(productIds);
                CartReference.setValue(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkIfProductIncart(holder,key);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: cart update error");
                    }
                });
            }
        });

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // boolean inCart=checkIfProductIncart(holder, key);
                //TODO check for bug
               mOnItemClickListener.onItemClickListener(model,inCart,key);
            }
        });
    }

    private boolean checkIfProductIncart(@NonNull ProductViewHolder holder, String key) {
        if (cart != null) {
            if (cart.getProductIds() != null) {
                if (cart.getProductIds().contains(key)) {
                    holder.favoriteButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                    return true;
                } else {
                    holder.favoriteButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                    return false;
                }
            }
        }
        cart=new Cart();
        return false;
    }

    @NonNull
    @Override
    public DatabaseReference getRef(int position) {
        return super.getRef(position);
    }

    @NonNull
    @Override
    public ObservableSnapshotArray<Product> getSnapshots() {
        return super.getSnapshots();
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.product_item,parent,false);
        ProductViewHolder productViewHolder=new ProductViewHolder(view);
        return productViewHolder;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        public ImageView productImage;
        public TextView productName;
        public TextView productPrice;
        public RatingBar productRating;
        public ImageButton favoriteButton;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.product_item_image);
            productName=itemView.findViewById(R.id.product_item_name_text);
            productPrice=itemView.findViewById(R.id.product_item_price_text);
            productRating=itemView.findViewById(R.id.product_detail_rating);
            favoriteButton=itemView.findViewById(R.id.product_detail_favorite);
        }
    }
}
