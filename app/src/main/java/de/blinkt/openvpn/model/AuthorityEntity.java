package de.blinkt.openvpn.model;

/**
 * Created by wzj on 2017/3/20.
 * 重要权限设置
 */

public class AuthorityEntity {
	private String title;
	private String tip;
	private boolean isCanClick = false;
	private IntentEntity intentEntity;

	public AuthorityEntity() {
	}

	public AuthorityEntity(AuthorityEntity entity) {
		this.title = entity.getTitle();
		this.tip = entity.getTip();
		this.intentEntity = entity.getintentEntity();
	}

	public AuthorityEntity(String title, String tip, IntentEntity intentEntity) {
		this.title = title;
		this.tip = tip;
		this.intentEntity = intentEntity;
	}

	public boolean isCanClick() {
		return isCanClick;
	}

	public void setCanClick(boolean canClick) {
		isCanClick = canClick;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public IntentEntity getintentEntity() {
		return intentEntity;
	}

	public void setintentEntity(IntentEntity intentEntity) {
		this.intentEntity = intentEntity;
	}


}
