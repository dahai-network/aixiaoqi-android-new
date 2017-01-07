package cn.qfishphone.sipengine;

import java.io.Serializable;

public interface SipEngineCore extends Serializable{
	/*初始化Sip引擎*/
	boolean CoreInit();
	/*销毁Sip引擎*/
	boolean CoreTerminate();
	/*检测是否初始化成功*/
	boolean IsInitialized();
	/*核心事件处理函数*/
	boolean CoreEventProgress();
	/*呼叫一个sip url 例如 sip:alice@sip.domain.com*/
	boolean MakeUrlCall(String url);
	/*呼叫一个号码*/
	boolean MakeCall(String num);
	/*拒绝来电，或挂断当前通话*/
	boolean Hangup();
	/*注册sip帐号*/
	boolean RegisterSipAccount(String username, String password, String server, int port, int expire);
	/*检查账号是否已注册*/
	boolean AccountIsRegistered();
	/*强制重新注册*/
	boolean ForceReRegster();
	/*注销帐号*/
	boolean DeRegisterSipAccount();
	/*回声消除*/
	boolean SetAEC(boolean yesno);
	/*自动增益控制*/
	boolean SetAGC(boolean yesno);
	/*降噪*/
	boolean SetNS(boolean yesno);
	/*发送dtmf信号 mode 0=rfc2833, 1=sip info*/
	boolean SendDtmf(int mode, String dtmf);
	/*切换免提*/
	boolean SetLoudspeakerStatus(boolean yesno);
	/*RC4 加密设置*/
	boolean SetRC4Crypt(boolean yesno);
	/*检测是否有来电*/
	boolean HaveIncomingCall();
	/*接听来电*/
	boolean AnswerCall();
	/*设置扬声器增益*/
	boolean SetSpeakerVolume(int vol);
	/*设置网络是否可用，以便通知内核是否注册到服务器*/
	boolean SetNetworkStateReachable(boolean yesno);
	/*录制通话 path=/sdcard/xxxx/recoding_path */
	boolean StartRecoding(String path);
	/*停止录音*/
	boolean StopRecoding();
	/*判断是否正在录音*/
	boolean CallIsInRecording();
	/*打印Debug日志*/
	boolean EnableDebug(boolean yesno);
	/*for p2p mode*/
	/*P2P 语音模式，需要服务器端支持,开启后需要设置Stun、Turn 服务器*/
	boolean SetUseICE(boolean yesno);
	 /*增强抗丢包模式*/
	 boolean SetVoFEC(boolean yesno);
	/*重置Transport*/
	boolean ResetTransport();
	/*发送VOS余额查询请求*/
	boolean SendVOSBalanceQuery();
	/*保持呼叫*/
	boolean SetHold();
	/*恢复呼叫*/
	boolean SetUnHold();
	/*Mic静音*/
	boolean MuteMic(boolean yesno);
	/*Spk静音*/
	boolean MuteSpk(boolean yesno);
	/*设置Stun 服务器地址*/
	boolean SetStunServer(String stun_server);
	/*设置Turn 服务器信息*/
	boolean SetTurnServer(String user, String passwd, String turn_server);
	/*的事当前AudioCodecs List*/
	boolean SetAudioCodecs(String codec_list);
	/*设置传输端口类型:
	 *  参数1 0 = UDP, 1 = TCP, 2 = TLS
	 *  参数2 1-65535 指定端  -1 or 0 随机端口
	 * */
	boolean SetTransport(int type, int bind_port);
	/*
	 * 设置SipEngineEventListener
	 */
	void setSipEngineEventListener(SipEngineEventListener listener);
}
