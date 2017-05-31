package de.blinkt.openvpn.activities.Set.PresenterImpl;

import android.text.TextUtils;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Set.ModelImpl.UserFeedbackModelImpl;
import de.blinkt.openvpn.activities.Set.Presenter.UserFeedbackPresenter;
import de.blinkt.openvpn.activities.Set.View.UserFeedbackView;
import de.blinkt.openvpn.http.CommonHttp;


/**
 * Created by Administrator on 2017/5/25 0025.
 */

public class UserFeedbackPresenterImpl implements UserFeedbackPresenter,NetModelBaseImpl.OnLoadFinishListener {
    private UserFeedbackModelImpl userFeedbackModel;
    private UserFeedbackView userFeedbackView;

    public UserFeedbackPresenterImpl(UserFeedbackView userFeedbackView) {
        this.userFeedbackView = userFeedbackView;
        userFeedbackModel=new UserFeedbackModelImpl(this);
    }

    @Override
    public void requsetUserFeedback( ) {
       String  userFeedbackContent=userFeedbackView.getUserFeedbackContent();
        if(TextUtils.isEmpty(userFeedbackContent)){
            userFeedbackView.showToast(R.string.feedback_content_is_null);
            return;
        }
        if(userFeedbackContent.length()<10){
            userFeedbackView.showToast(R.string.feedback_content_is_too_short);
            return;
        }
        if(userFeedbackContent.length()>500){
            userFeedbackView.showToast(R.string.feedback_content_is_too_long);
            return;
        }
        userFeedbackModel.loadUserFeedback(userFeedbackContent);
    }

    @Override
    public void onDestory() {
        userFeedbackModel=null;
        userFeedbackView=null;
    }



    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        userFeedbackView.finishView();
        if(!TextUtils.isEmpty(object.getMsg()))
            userFeedbackView.showToast(object.getMsg());
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {

    }

    @Override
    public void noNet() {

    }

}
