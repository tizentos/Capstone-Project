package ltd.boku.distail.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;



import ltd.boku.distail.R;
import ltd.boku.distail.model.Review;

public class ReviewFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Review,ReviewFirebaseRecyclerAdapter.ReviewViewHolder>{

    Context mContext;

    public ReviewFirebaseRecyclerAdapter(@NonNull FirebaseRecyclerOptions options,Context context) {
        super(options);
        mContext=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull Review model) {
        holder.reviewAuthor.setText(model.getAuthor());
        holder.reviewText.setText(model.getDescription());
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.review_layout,parent,false);
        ReviewViewHolder reviewViewHolder=new ReviewViewHolder(view);

        return reviewViewHolder;
    }

    public static  class ReviewViewHolder extends RecyclerView.ViewHolder{
        public TextView reviewText;
        public TextView reviewAuthor;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            reviewText=itemView.findViewById(R.id.product_detail_review_text);
            reviewAuthor=itemView.findViewById(R.id.product_detail_review_author);
        }
    }
}
