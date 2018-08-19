package ltd.boku.distail.dialogfragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;

public class CheckoutDialogFragment extends DialogFragment {

    public CheckoutDialogFragment() {
        super();
    }

    @Override
    public void onResume() {
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.70);

        getDialog().getWindow().setLayout(width, height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        super.onResume();
    }
}
