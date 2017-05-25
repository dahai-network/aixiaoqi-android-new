package de.blinkt.openvpn.activities.Set.Presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.CommonTools;

import static android.content.Context.TELEPHONY_SERVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEEMAIL;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEPHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEWEIXINHAO;

/**
 * Created by Administrator on 2017/5/22 0022.
 */

public class ContactUsPersenter   {
    Context context;
    public ContactUsPersenter(Context context ){
        this.context=context;
    }


    public void clickEmail() {
        MobclickAgent.onEvent(context, CLICKCONTACTOURUSEEMAIL);
        String[] email = {context.getResources().getString(R.string.web_of_service)}; // 需要注意，email必须以数组形式传入
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // 设置邮件格式
        intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }


    public void clickPhone() {
        if (!CommonTools.isFastDoubleClick(4000)) {
            //友盟方法统计
            MobclickAgent.onEvent(context, CLICKCONTACTOURUSEPHONE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            if (!getSimState()) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + context.getResources().getString(R.string.service_phone)));
                context.startActivity(phoneIntent);
            }
        }
    }


    public void clickWeiXin() {
        MobclickAgent.onEvent(context, CLICKCONTACTOURUSEWEIXINHAO);
        Intent mmintent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
        if (mmintent != null)
            context.startActivity(mmintent);
    }

    public boolean getSimState() {
        boolean flg;
        String message = null;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

        switch (manager.getSimState()) {
            case TelephonyManager.SIM_STATE_ABSENT:
                message = "无SIM卡";
                flg = true;
                break;
            case TelephonyManager.SIM_STATE_READY:
                flg = false;
                break;
            default:
                message="SIM卡被锁定或未知状态";
                flg = true;
                break;
        }
        if (flg) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        return flg;

    }
}
