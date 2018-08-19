package ltd.boku.distail.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.distail.R;
import ltd.boku.distail.adapter.ReviewFirebaseRecyclerAdapter;
import ltd.boku.distail.model.Review;
import ltd.boku.distail.model.User;
import ltd.boku.distail.ui_helper.ItemDecorationProductList;

import static ltd.boku.distail.MainActivity.PRODUCT_KEY;

public class ReviewFragment extends Fragment {

    RecyclerView reviewRecyclerView;
    EditText reviewText;
    Button addReviewButton;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference reviewDatabaseReference;
    ReviewFirebaseRecyclerAdapter reviewFirebaseRecyclerAdapter;

    List<Review> reviewList=new ArrayList<>();
    String productKey;
    User user;


    public static final String CURRENT_USER="current_user";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        productKey = getArguments().getString(PRODUCT_KEY, null);
        user=(User) getArguments().getSerializable(CURRENT_USER);

        reviewDatabaseReference= mFirebaseDatabase.getReference().child("Review").child(productKey);
//        reviewFirebaseRecyclerAdapter=new ReviewFirebaseRecyclerAdapter()


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.reviews_layout,container,false);


        reviewText=view.findViewById(R.id.review_text);
        addReviewButton=view.findViewById(R.id.add_review_button);
        addReviewButton.setEnabled(false);

        Query query=reviewDatabaseReference.orderByKey();
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Review>()
                .setQuery(query,Review.class)
                .setLifecycleOwner(getActivity())
                .build();

        reviewFirebaseRecyclerAdapter = new ReviewFirebaseRecyclerAdapter(options,getContext());
        reviewRecyclerView=view.findViewById(R.id.product_detail_review_recycler);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewRecyclerView.setAdapter(reviewFirebaseRecyclerAdapter);

                DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        reviewRecyclerView.addItemDecoration(dividerItemDecoration);


        reviewText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    addReviewButton.setEnabled(true);
                } else {
                    addReviewButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review review=new Review("dummy product path",productKey,reviewText.getText().toString()
                ,user.getUserId(),user.getUserName());
                reviewDatabaseReference.push().setValue(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Review added succesfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Review can not be added", Toast.LENGTH_SHORT).show();
                    }
                });
                reviewText.setText("");
            }
        });
        return view;
    }
}
