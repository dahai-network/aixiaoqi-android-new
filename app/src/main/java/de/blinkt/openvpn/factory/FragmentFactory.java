package de.blinkt.openvpn.factory;

import android.support.v4.util.SparseArrayCompat;

import de.blinkt.openvpn.activities.Base.BaseFragment;
import de.blinkt.openvpn.fragments.PackageDetailsFragment;
import de.blinkt.openvpn.fragments.PaymentTermFragment;
import de.blinkt.openvpn.fragments.ProductFeatureFragment;

/**
 * @author kim
 */
public class FragmentFactory {

    //SparseArray 数组进行优化
    static SparseArrayCompat<BaseFragment> PackageDetailFragments = new SparseArrayCompat<>();
    private static final int PACKAGEDETAILS = 0;
    private static final int PRODUCTFEATURE = 1;
    private static final int PAYMENTTERM = 2;


    /**
     * 详情界面
     *
     * @param position
     * @return
     */
    public static BaseFragment getDetailFragment(int position) {

        BaseFragment fragment = null;
        BaseFragment tmpFragment = PackageDetailFragments.get(position);
        if (tmpFragment != null) {
            fragment = tmpFragment;
            return fragment;
        }
        switch (position) {
            case PACKAGEDETAILS:
                fragment = new PackageDetailsFragment();
                break;
            case PRODUCTFEATURE:
                fragment = new ProductFeatureFragment();
                break;
            case PAYMENTTERM:
                fragment = new PaymentTermFragment();
                break;

            default:
                break;

        }
        if (fragment != null) {
            PackageDetailFragments.put(position, fragment);
        }
        return fragment;
    }



}
