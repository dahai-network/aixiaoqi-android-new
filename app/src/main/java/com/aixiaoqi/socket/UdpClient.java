package com.aixiaoqi.socket;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import de.blinkt.openvpn.activities.LaunchActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

/**
 * Created by Administrator on 2016/12/30 0030.
 */

public abstract class UdpClient implements Runnable {

	private boolean flag;
	DatagramSocket datagramSocket;
	DatagramSocket socket;
	private int sendPort;
	private String sendAddress = "127.0.0.1";
	public static String tag = null;
	private int port = 4567;
	private String TAG = "UdpClient";

	@Override
	public void run() {
		try {

			if (socket == null) {
				try {
					socket = new DatagramSocket(port);
				}catch (Exception e){
					exceptionPort();
				}
			}
			byte data[] = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			while (flag) {
				socket.receive(packet);
				sendPort = packet.getPort();
				String receiveMsg = new String(packet.getData(), 0, packet.getLength());
				Log.e("receiveMsg","receiveMsg="+receiveMsg);
				String tag = receiveMsg.substring(0, 7);
				//如果这次的标签与上次一样则选择过滤，如果不一样就把从SDK那里发过来的数据发个蓝牙
				if (SocketConstant.REGISTER_STATUE_CODE == 0) {
					SocketConstant.REGISTER_STATUE_CODE = 1;
				}

				if (!tag.equals(getSorcketTag())) {
					setSorketTag(tag);
					sendToBluetoothMsg(receiveMsg);
				}
			}
			if (socket != null) {
				closeReceiveSocket();
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
	}

	private void closeReceiveSocket() {
		socket.disconnect();
		socket.close();
		socket = null;
	}

	public String getSorcketTag() {
		return tag;
	}

	public void setSorketTag(String tag) {
		UdpClient.tag = tag;
	}

	public abstract void sendToBluetoothMsg(String msg);

	public void start() {
		flag = true;
		new Thread(this).start();
	}

	public void sendToSdkMessage(String msg) {
		try {
			if (datagramSocket == null) {
				datagramSocket = new DatagramSocket(null);
				datagramSocket.setReuseAddress(true);
			}
			InetAddress addr = InetAddress.getByName(sendAddress);
			byte[] data = msg.getBytes();
			DatagramPacket sendSocket = new DatagramPacket(data, data.length, addr, sendPort);
			exceptionPort();
			datagramSocket.send(sendSocket);
		} catch (SocketException e) {
			closeSendUdp();
			e.printStackTrace();
		} catch (UnknownHostException e) {
			closeSendUdp();
			e.printStackTrace();
		} catch (IOException e) {
			closeSendUdp();
			e.printStackTrace();
		}
	}

	private void exceptionPort() {
		if(sendPort==0){
			Intent intent = new Intent(ICSOpenVPNApplication.getContext().getApplicationContext(), LaunchActivity.class);


			PendingIntent restartIntent = PendingIntent.getActivity(
					ICSOpenVPNApplication.getContext().getApplicationContext().getApplicationContext(), 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			//退出程序
			AlarmManager mgr = (AlarmManager)ICSOpenVPNApplication.getContext().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
					restartIntent);
			ICSOpenVPNApplication.getInstance().finishAllActivity();
			System.exit(0);
		}
	}

	private void closeSendUdp() {
		datagramSocket.disconnect();
		datagramSocket.close();
		datagramSocket = null;
	}


	public void disconnect() {
		if (datagramSocket != null) {
			flag = false;
			closeReceiveSocket();
			closeSendUdp();
		}
	}
}
