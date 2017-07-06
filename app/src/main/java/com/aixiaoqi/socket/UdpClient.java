package com.aixiaoqi.socket;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.LaunchActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogTip;

/**
 * Created by Administrator on 2016/12/30 0030.
 */

public abstract class UdpClient implements Runnable,DialogInterfaceTypeBase {

	private boolean flag;
	DatagramSocket datagramSocket;
	DatagramSocket socket;
	private int sendPort;
	private String sendAddress = "127.0.0.1";//因为是向so库发送IP，所以就是本地的地址。
	public static String tag = null;
	private int port = 4567;//端口号是有so那段固定的。

	@Override
	public void run() {
		try {

			if (socket == null) {
				try {
					socket = new DatagramSocket(port);
					socket.setReuseAddress(true);
				}catch (Exception e){
					port=0;
					exceptionPort();
				}
			}
			byte data[] = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			while (flag) {
				socket.receive(packet);//获取数据包
				sendPort = packet.getPort();//获取端口号
				String receiveMsg = new String(packet.getData(), 0, packet.getLength());//收到的信息
				Log.e("receiveMsg","receiveMsg="+receiveMsg);
				String tag = receiveMsg.substring(0, 7);//获取标签
				//如果这次的标签与上次一样则选择过滤，如果不一样就把从SDK那里发过来的数据发个蓝牙
				if (SocketConstant.REGISTER_STATUE_CODE == 0) {
					SocketConstant.REGISTER_STATUE_CODE = 1;
				}

				if (!tag.equals(getSorcketTag())) {
					setSorketTag(tag);
					sendToBluetoothMsg(receiveMsg);//把从so库获取的卡命令发给蓝牙
				}
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
			DatagramPacket sendSocket = new DatagramPacket(data, data.length, addr, sendPort);//根据端口号和地址发送信息给so库
			exceptionPort();
			datagramSocket.send(sendSocket);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private  void showDialog(){
		DialogTip dialogTip
				=new DialogTip(this,ICSOpenVPNApplication.getContext(), R.layout.dialog_tip, 3);
		dialogTip.setIvTipVisibility(View.GONE);
		dialogTip.setTvRechangeText(R.string.sure_restart);
		dialogTip.setTvContentText(R.string.sure_restart_content);
		dialogTip.setCanClickBack(false);
	}

	@Override
	public void dialogText(int type, String text) {
		if(type==3){
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

	//处理当端口号被占用的情况，当端口号被占用，只有杀死进程，端口号才能释放，否则创建UDP会失败。
	private void exceptionPort() {
		if(sendPort==0){
			Log.e("UdpClient","port="+port);
			Looper.prepare();
			showDialog();
			Looper.loop();

		}
	}



	//断开连接，因为端口号跟进程捆绑在一起，
// 而我们没有另外搞一个子线程，
// 因此进程没有关闭的情况下最好不要主动断开。
// 否则，当换卡且没有注册过的时候，就会抛出端口号被占用的情况。
	public void disconnect() {
		if (datagramSocket != null) {
			flag = false;
			closeReceiveSocket();
			closeSendUdp();
		}
	}
	private void closeSendUdp() {
		if (datagramSocket != null) {
			datagramSocket.disconnect();
			datagramSocket.close();
			datagramSocket = null;
		}
	}
}
