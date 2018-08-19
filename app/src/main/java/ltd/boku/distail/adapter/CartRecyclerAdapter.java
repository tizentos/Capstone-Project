package ltd.boku.distail.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.distail.R;
import ltd.boku.distail.model.Product;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder> {

    Context mContext;
    List<Product> mProducts=new ArrayList<>();

    public CartRecyclerAdapter(Context context) {
        super();
        mContext=context;
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product=mProducts.get(position);
        if  (product !=null){
            holder.cartProductPrice.setText(mContext.getString(R.string.product_item_price,product.getCurrency(),
                    product.getPrice())); //TODO check this!!!

            holder.cartProductName.setText(product.getName());
            Picasso.with(mContext).load(product.getPhotoUrl())
                    .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder))
                    .into(holder.cartProductImage);
        }
    }

    @Override
    public int getItemCount() {
       if (mProducts ==null)return 0;
       return mProducts.size();
    }

    public void setProducts(List<Product> products){
        mProducts=products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.cart_item_layout,parent,false);
        CartViewHolder cartViewHolder=new CartViewHolder(view);

        return cartViewHolder;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{
        TextView cartProductName;
        TextView cartProductPrice;
        ImageView cartProductImage;

        public CartViewHolder(View itemView) {
            super(itemView);
            cartProductImage=itemView.findViewById(R.id.cart_product_image);
            cartProductName=itemView.findViewById(R.id.cart_product_name_text);
            cartProductPrice=itemView.findViewById(R.id.cart_product_price_text);
        }
    }
}
