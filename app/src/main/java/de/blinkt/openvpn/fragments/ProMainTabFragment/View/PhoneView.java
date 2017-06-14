package de.blinkt.openvpn.fragments.ProMainTabFragment.View;

import android.content.Intent;

import de.blinkt.openvpn.model.ContactRecodeEntity;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface PhoneView {
    void showToast(int toastId);
    void showToast(String toastContent);
    void toCallDetailActivity(ContactRecodeEntity contactRecodeEntity);
    void rlNoPermission(int isVisiable);
    void simCallPhone(ContactRecodeEntity contactRecodeEntity);
}
