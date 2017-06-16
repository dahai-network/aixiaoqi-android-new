package de.blinkt.openvpn.fragments.ProMainTabFragment.View;

import android.content.Intent;

import de.blinkt.openvpn.activities.CommomView.Toast;
import de.blinkt.openvpn.model.ContactRecodeEntity;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface PhoneView  extends Toast{
    void toCallDetailActivity(ContactRecodeEntity contactRecodeEntity);
    void rlNoPermission(int isVisiable);
    void simCallPhone(ContactRecodeEntity contactRecodeEntity);
}
