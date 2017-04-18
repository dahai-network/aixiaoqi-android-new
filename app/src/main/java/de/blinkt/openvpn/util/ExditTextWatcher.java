package de.blinkt.openvpn.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public abstract class ExditTextWatcher implements TextWatcher {
    int id;
    public ExditTextWatcher(EditText editText, int id) {
        editText.addTextChangedListener(this);
        this.id = id;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textChanged(s,id);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public abstract void textChanged(CharSequence s, int id);

}
