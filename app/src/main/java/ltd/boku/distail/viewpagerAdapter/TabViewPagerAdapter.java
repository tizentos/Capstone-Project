package ltd.boku.distail.viewpagerAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ltd.boku.distail.fragment.ProductDescriptionFragment;
import ltd.boku.distail.fragment.ReviewFragment;

public class TabViewPagerAdapter extends FragmentPagerAdapter {
    Bundle mBundle;
    public TabViewPagerAdapter(FragmentManager fm, Bundle bundle) {
        super(fm);
        mBundle=bundle;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Description";
            case 1:
                return "Reviews";
        }
        return null;

    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                ProductDescriptionFragment productDescriptionFragment =new ProductDescriptionFragment();
                productDescriptionFragment.setArguments(mBundle);
                return productDescriptionFragment;
            case 1:
                ReviewFragment reviewFragment=new ReviewFragment();
                reviewFragment.setArguments(mBundle);
                return reviewFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
