package cn.qfishphone.sipengine;

import android.content.Context;
import android.util.Log;

public class SipEngineCoreImpl implements SipEngineCore {
	private static String TAG = "*ASTGO*";
	private long nativePtr = 0;
	private native long Init(SipEngineEventListener alistener,Context context);
	private native boolean Terminate(long nativePtr);
	private native boolean IsInitialized(long nativePtr);
	private native boolean CoreEventProgress(long nativePtr);
	private native boolean MakeUrlCall(long nativePtr,String url);
	private native boolean MakeCall(long nativePtr,String num);
	private native boolean Hangup(long nativePtr);
	private native boolean RegisterSipAccount(long nativePtr,String username, String password, String server, int port, int expire);
	private native boolean DeRegisterSipAccount(long nativePtr);
	private native boolean SetUseICE(long nativePtr, boolean yesno);
	private native boolean SetAEC(long nativePtr, boolean yesno);
	private native boolean SetAGC(long nativePtr, boolean yesno);
	private native boolean SetNS(long nativePtr, boolean yesno);
	private native boolean SetLoudspeakerStatus(long nativePtr, boolean yesno);
	private native boolean HaveIncomingCall(long nativePtr);
	private native boolean AnswerCall(long nativePtr);
	private native boolean SetSpeakerVolume(long nativePtr, int vol);
	private native boolean SetVoFEC(long nativePtr, boolean yesno);
	private native boolean SetRC4Crypt(long nativePtr,boolean yesno);
	private native boolean StartRecoding(long nativePtr, String path);
	private native boolean StopRecoding(long nativePtr);
	private native boolean SendDtmf(long nativePtr,int mode ,String dtmf);
	private native boolean EnableDebug(long nativePtr, boolean yesno);
	private native boolean CallIsInRecording(long nativePtr);
	private native boolean ForceReRegster(long nativePtr);
	private native boolean ResetTransport(long nativePtr);
	private native boolean SendVOSBalanceQuery(long nativePtr);
	private native boolean SetNetworkStateReachable(long nativePtr, boolean yesno);
	private native boolean SetHold(long nativePtr);
	private native boolean SetUnHold(long nativePtr);
    private native boolean MuteMic(long nativePtr,boolean yesno);
    private native boolean MuteSpk(long nativePtr,boolean yesno);
    private native boolean SetStunServer(long nativePtr, String stun_server);
    private native boolean SetTurnServer(long nativePtr, String user, String passwd, String turn_server);
    private native boolean SetAudioCodecs(long nativePtr, String codec_list);
    private native boolean AccountIsRegistered(long nativePtr);
    private native boolean SetTransport(long nativePtr, int type, int bind_port);
    
	private SipEngineEventListener mListener=null;
	private Context mContext=null;
	
	public SipEngineCoreImpl(SipEngineEventListener alistener, Context context) {
		mListener=alistener;
		mContext = context;
	}


	public void isValid(){
		if(nativePtr==0){
			Log.w(TAG,"nativePtr");
		}
	}
	@Override
	public boolean CoreInit() {
		nativePtr = Init(mListener,mContext);
	     if(nativePtr==0){
	        Log.d(TAG, "Init failed ");
	        return false;
	     }else{
	    	 Log.d(TAG, "Init successful");
	     }
      
	     return true;
	}
	@Override
	public boolean CoreTerminate() {
		return Terminate(nativePtr);
	}
	@Override
	public boolean IsInitialized() {
		return IsInitialized(nativePtr);
	}
	@Override
	public synchronized boolean CoreEventProgress() {
		return CoreEventProgress(nativePtr);
	}
	@Override
	public boolean MakeUrlCall(String url) {
		return MakeUrlCall(nativePtr,url);
	}
	@Override
	public boolean MakeCall(String num) {
		return MakeCall(nativePtr,num);
	}
	@Override
	public boolean Hangup() {
		return Hangup(nativePtr);
	}

	public boolean RegisterSipAccount(String username, String password, String server, int port, int expire) {
		return RegisterSipAccount(nativePtr,username, password, server,port,expire);
	}

	public boolean DeRegisterSipAccount() {
		return DeRegisterSipAccount(nativePtr);
	}

	public boolean SetUseICE(boolean yesno) {
		return SetUseICE(nativePtr,yesno);
	}

	public boolean SetAEC(boolean yesno) {
		return SetAEC(nativePtr,yesno);
	}

	public boolean SetAGC(boolean yesno) {
		return SetAGC(nativePtr,yesno);
	}

	public boolean SetNS(boolean yesno) {
		return SetNS(nativePtr,yesno);
	}

	public boolean SetLoudspeakerStatus(boolean yesno) {
		return SetLoudspeakerStatus(nativePtr,yesno);
	}
	@Override
	public boolean HaveIncomingCall() {
		return HaveIncomingCall(nativePtr);
	}
	@Override
	public boolean AnswerCall() {
		return AnswerCall(nativePtr);
	}
	@Override
	public boolean SetSpeakerVolume(int vol) {
		return SetSpeakerVolume(nativePtr,vol);
	}
	
	public synchronized void destroy() {
		isValid();
		Terminate(nativePtr);
		nativePtr=0;
	}
	@Override
	public boolean SetNetworkStateReachable(boolean yesno) {
		return SetNetworkStateReachable(nativePtr,yesno);
	}
	@Override
	public boolean SetVoFEC(boolean yesno) {
		return SetVoFEC(nativePtr, yesno);
	}
	@Override
	public boolean SetRC4Crypt(boolean yesno) {
		return SetRC4Crypt(nativePtr,yesno);
	}
	@Override
	public boolean StartRecoding(String path) {
		return StartRecoding(nativePtr, path);
	}
	@Override
	public boolean StopRecoding() {
		return StopRecoding(nativePtr);
	}
	@Override
	public boolean SendDtmf(int mode, String dtmf) {
		return SendDtmf(nativePtr,mode, dtmf);
	}
	@Override
	public boolean EnableDebug(boolean yesno) {
		return EnableDebug(nativePtr, yesno);
	}
	@Override
	public boolean CallIsInRecording() {
		return CallIsInRecording(nativePtr);
	}
	@Override
	public boolean ForceReRegster() {
		return ForceReRegster(nativePtr);
	}
	@Override
	public boolean SendVOSBalanceQuery() {
		return SendVOSBalanceQuery(nativePtr);
	}
	@Override
	public boolean ResetTransport()
	{
		return ResetTransport(nativePtr);
	}	
	@Override
	public boolean SetHold() {
		return SetHold(nativePtr);
	}
	@Override
	public boolean SetUnHold() {
		return SetUnHold(nativePtr);
	}
	@Override
	public boolean MuteMic(boolean yesno) {
		return MuteMic(nativePtr,yesno);
	}
	@Override
	public boolean MuteSpk(boolean yesno) {
		return MuteSpk(nativePtr,yesno);
	}
	@Override
	public boolean SetStunServer(String stun_server) {
		return SetStunServer(nativePtr,stun_server);
	}
	@Override
	public boolean SetTurnServer(String user, String passwd, String turn_server) {
		return SetTurnServer(nativePtr,user,passwd,turn_server);
	}
	@Override
	public boolean SetAudioCodecs(String codec_list) {
		return SetAudioCodecs(nativePtr,codec_list);
	}
	@Override
	public boolean AccountIsRegistered() {
		return AccountIsRegistered(nativePtr);
	}
	@Override
	public boolean SetTransport(int type, int bind_port) {
		return SetTransport(nativePtr,type,bind_port);
	}

	@Override
	public void setSipEngineEventListener(SipEngineEventListener listener) {
		mListener = listener;
	}
}
