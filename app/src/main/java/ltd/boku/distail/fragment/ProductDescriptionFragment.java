package ltd.boku.distail.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ltd.boku.distail.R;
import ltd.boku.distail.model.Product;

import static ltd.boku.distail.adapter.ProductFirebaseRecyclerAdapter.PRODUCT;


public class ProductDescriptionFragment extends Fragment {

    public static final String PRODUCT_DESCRIPTION="product description";

    TextView descriptionText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view=inflater.inflate(R.layout.product_description_layout,container,false);
        descriptionText=view.findViewById(R.id.product_details_description);
        Product product=(Product) getArguments().getSerializable(PRODUCT);

        descriptionText.setText(product.getDescription());

        return view;
    }
}