package de.blinkt.openvpn.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.provider.Settings;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

/**
 * Created by Administrator on 2017/7/27 0027.
 */

public class IsHasrecodePermission implements DialogInterfaceTypeBase {

  private   DialogBalance noRecodePermissionDialog;
    // 音频获取源
    private  int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    private  int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private  int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private  int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private  int bufferSizeInBytes = 0;
    /**
     * 判断是是否有录音权限
     */
    Context context;
    public  IsHasrecodePermission(Context context){
        this.context=context;
    }

    public  boolean isHasPermission(){
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord =  new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            noRecodePermissionDialog = new DialogBalance(IsHasrecodePermission.this, context,R.layout.dialog_balance, 3);
            noRecodePermissionDialog.changeText(context.getResources().getString(R.string.no_recode_permission), context.getResources().getString(R.string.sure));

            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }




    @Override
    public void dialogText(int type, String text) {
        context.startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
    }
}
