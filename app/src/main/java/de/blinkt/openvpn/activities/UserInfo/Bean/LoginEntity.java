package de.blinkt.openvpn.activities.UserInfo.Bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class LoginEntity implements Serializable {
	private String NickName;
	private String Email;
	private String UserHead;
	private String Tel;
	private String TrueName;
	private String Age;
	private String Sex;
	private String Height;
	private String Weight;
	private String MovingTarget;
	private String BraceletIMEI;
	private String BraceletVersion;
	private int NotificaCall;
	private int NotificaSMS;
	private int NotificaWeChat;
	private int NotificaQQ;
	private int LiftWristLight;
	private String Token;

	public String getBraceletIMEI() {
		return BraceletIMEI;
	}

	public void setBraceletIMEI(String braceletIMEI) {
		BraceletIMEI = braceletIMEI;
	}

	public String getBraceletVersion() {
		return BraceletVersion;
	}

	public void setBraceletVersion(String braceletVersion) {
		BraceletVersion = braceletVersion;
	}

	public String getBirthday() {
		return Birthday;
	}

	public void setBirthday(String birthday) {
		Birthday = birthday;
	}

	private String Birthday;

	public String getNickName() {
		return NickName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getUserHead() {
		return UserHead;
	}

	public void setUserHead(String userHead) {
		UserHead = userHead;
	}

	public String getTel() {
		return Tel;
	}

	public void setTel(String tel) {
		Tel = tel;
	}

	public String getTrueName() {
		return TrueName;
	}

	public void setTrueName(String trueName) {
		TrueName = trueName;
	}

	public String getAge() {
		return Age;
	}

	public void setAge(String age) {
		Age = age;
	}

	public String getSex() {
		return Sex;
	}

	public void setSex(String sex) {
		Sex = sex;
	}

	public String getHeight() {
		return Height;
	}

	public void setHeight(String height) {
		Height = height;
	}

	public String getWeight() {
		return Weight;
	}

	public void setWeight(String weight) {
		Weight = weight;
	}

	public String getMovingTarget() {
		return MovingTarget;
	}

	public void setMovingTarget(String movingTarget) {
		MovingTarget = movingTarget;
	}

	public int getNotificaCall() {
		return NotificaCall;
	}

	public void setNotificaCall(int notificaCall) {
		NotificaCall = notificaCall;
	}

	public int getNotificaSMS() {
		return NotificaSMS;
	}

	public void setNotificaSMS(int notificaSMS) {
		NotificaSMS = notificaSMS;
	}

	public int getNotificaWeChat() {
		return NotificaWeChat;
	}

	public void setNotificaWeChat(int notificaWeChat) {
		NotificaWeChat = notificaWeChat;
	}

	public int getNotificaQQ() {
		return NotificaQQ;
	}

	public int getLiftWristLight() {
		return LiftWristLight;
	}

	public void setLiftWristLight(int liftWristLight) {
		LiftWristLight = liftWristLight;
	}

	public void setNotificaQQ(int notificaQQ) {
		NotificaQQ = notificaQQ;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}
}
