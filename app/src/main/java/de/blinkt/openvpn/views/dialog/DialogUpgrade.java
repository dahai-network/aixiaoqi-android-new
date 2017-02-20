package de.blinkt.openvpn.views.dialog;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.service.DfuService;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DialogUtils;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;

/**
 * Created by Administrator on 2016/11/3 0003.
 */
public class DialogUpgrade extends DialogBase{
    TextView mTextPercentage;
    ProgressBar mProgressBar;
    TextView mTextUploading;
    public DialogUpgrade(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type){
        super(dialogInterfaceTypeBase,context,layoutId,type);
    }

    @Override
    protected void setDialogStyle() {
        DialogUtils.dialogSet(dialog, context, Gravity.CENTER, 0.7, 1,true,false, false);
    }
    public Dialog getDialogUpgrade(){
        return dialog;
    }
    @Override
    protected void setDialogContentView(View view) {
        mTextPercentage=(TextView)view.findViewById(R.id.textviewProgress);
        mProgressBar=(ProgressBar)view.findViewById(R.id.progressbar_file);
        mTextUploading=(TextView)view.findViewById(R.id.textviewUploading);
    }
    public  DfuProgressListener getDfuProgressListener(){
        return mDfuProgressListener;
    }
    DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting( String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_connecting);
        }

        @Override
        public void onDfuProcessStarting( String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_starting);
        }

        @Override
        public void onEnablingDfuMode( String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_switching_to_dfu);
        }

        @Override
        public void onFirmwareValidating( String deviceAddress) {
            mProgressBar.setIndeterminate(true);
        }

        @Override
        public void onDeviceDisconnecting( String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_disconnecting);
        }

        @Override
        public void onDfuCompleted( String deviceAddress) {
            mTextPercentage.setText(R.string.dfu_status_completed);
            noUpgrade();
            dialog.dismiss();
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onDfuAborted( String deviceAddress) {
            noUpgrade();
            mTextPercentage.setText(R.string.dfu_status_aborted);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//					onUploadCanceled();

                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(percent);
            mTextPercentage.setText(percent + "%");
            if (partsTotal > 1)
                mTextUploading.setText(context.getString(R.string.dfu_status_uploading_part, currentPart, partsTotal));
            else
                mTextUploading.setText(R.string.dfu_status_uploading);
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            CommonTools.showShortToast(context, message);
            noUpgrade();
            // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }
    };

    private void noUpgrade() {
        MyDeviceActivity.isUpgrade=false;
        MyDeviceActivity.startDfuCount=0;
    }
}
