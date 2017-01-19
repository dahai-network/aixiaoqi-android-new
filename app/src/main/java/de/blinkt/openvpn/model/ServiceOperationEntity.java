package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/18.
 * 服务操作类，用于提供给EventBus作为操作
 */

public class ServiceOperationEntity implements Serializable{
	//服务操作常量(删除)
	public static final int REMOVE_SERVICE = 0;
	//服务操作常量（创建）
	public static final int CREATE_SERVICE = 1;
	//服务名称
	private String ServiceName;
	//服务操作类型
	private int operationType;

	public String getServiceName() {
		return ServiceName;
	}

	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}

	public int getOperationType() {
		return operationType;
	}

	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
}
