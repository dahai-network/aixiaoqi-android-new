package de.blinkt.openvpn.bluetooth.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * SDK发出信息后接收接口
 * Created by Administrator on 2016/8/11.
 */
public class ReceiveSocketService extends Service {
	private String TAG = "ReceiveSocketService";
	private final IBinder mBinder = new LocalBinder();
	private String sendAddress = "127.0.0.1";
	private MessageOutLisener listener;
	private DatagramSocket socket;
	private Thread socketThread;
	//用于暂停线程
	private boolean flag = true;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public ReceiveSocketService getService() {
			return ReceiveSocketService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "open");
		initSocket();
	}

	private String ByteToHexString(byte[] buffer, int length) {
		String ret = "";
		for (int i = 0; i < length; i++) {
			ret += String.format("%02x", buffer[i]);
		}

		return ret;
	}

	//从SDK发来的信息发给蓝牙设备
	public void initSocket() {
		socketThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DatagramSocket socket = new DatagramSocket(4567);
					byte data[] = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					while (flag) {
						socket.receive(packet);
						//获取IP和端口
						sendAddress = packet.getAddress().toString().replace("/", "");
//						receiveMsg(new String(packet.getData(), 0, packet.getLength()), packet);
						String receiveMsg = new String(packet.getData(), 0, packet.getLength());

						Log.i("Msg from Server:", receiveMsg);
						Log.i("Msg from Server(Byte):", ByteToHexString(packet.getData(), packet.getLength()));
						if (!flag) {
							break;
						}
							Log.i("BLUEINFO", "SDK-->蓝牙 = " + receiveMsg);
							listener.sendMsg(receiveMsg);
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.i(TAG, "IO流错误");
				}


			}
		});
		socketThread.start();
	}

	public void closeThread() {

		flag = false;
	}

	public void restartThread() {
		flag = true;
		if(socketThread.isInterrupted())
		{
			socketThread.start();
		}
	}

	//发送消息给Service，由Service帮忙发送给SDK
	public void setMsg(final String msg) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (socket == null) {
						socket = new DatagramSocket(null);
						socket.setReuseAddress(true);
					}
					InetAddress addr = InetAddress.getByName(sendAddress);
					byte[] data = msg.getBytes();
					DatagramPacket sendSocket = new DatagramPacket(data, data.length, addr, 4567);
					Log.i("BLUEINFO", "蓝牙 -- > SDK:" + msg);
					socket.send(sendSocket);
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void setListener(MessageOutLisener listener) {
		ReceiveSocketService.this.listener = listener;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	//输出信息接口
	public interface MessageOutLisener {
		void sendMsg(String msg);
	}
}
