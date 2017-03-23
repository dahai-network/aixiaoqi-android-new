package com.aixiaoqi.socket;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;

/**
 * Socket收发器 通过Socket发送数据，并使用新线程监听Socket接收到的数据
 *
 * @author jzj1993
 * @since 2015-2-22
 */
public abstract class SocketTransceiver implements Runnable {

	protected Socket socket;
	protected InetAddress addr;
	protected DataInputStream in;
	protected DataOutputStream out;
	private boolean runFlag;

	/**
	 * 实例化
	 *
	 * @param socket 已经建立连接的socket
	 */
	public SocketTransceiver(Socket socket) {
		this.socket = socket;
		this.addr = socket.getInetAddress();
	}

	/**
	 * 获取连接到的Socket地址
	 *
	 * @return InetAddress对象
	 */
	public InetAddress getInetAddress() {
		return addr;
	}

	/**
	 * 开启Socket收发
	 * <p>
	 * 如果开启失败，会断开连接并回调{@code onDisconnect()}
	 */
	public void start() {
		runFlag = true;
		new Thread(this).start();

	}

	/**
	 * 断开连接(主动)
	 * <p>
	 * 连接断开后，会回调{@code onDisconnect()}
	 */
	public void stop() {
		runFlag = false;
		try {
			if (socket != null) {
				socket.shutdownInput();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送字符串
	 *
	 * @param s 字符串
	 * @return 发送成功返回true
	 */
	public boolean send(String s) {

		try {
			Log.e("toBLue", "发送字符串out1=" + (out != null));
			if (out == null)
				out = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("toBLue", "发送字符串IOException=" + e.getMessage());
			runFlag = false;
			this.onDisconnect(addr);
			return false;
		}
		if (out != null) {
			try {
				Log.e("toBLue", "发送字符串");
				out.write(HexStringExchangeBytesUtil.hexStringToBytes(s));
				out.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				this.onDisconnect(addr);
			}
		}
		return false;
	}

	/**
	 * 监听Socket接收的数据(新线程中运行)
	 */
	@Override
	public void run() {
		byte[] byteBuffer = new byte[1024];
		try {
			in = new DataInputStream(this.socket.getInputStream());
			Log.e("toBLue", "socket接收数据初始化");
		} catch (IOException e) {
			e.printStackTrace();
			runFlag = false;
			Log.e("toBLue", "Socket 断开");
		}
		while (runFlag) {
			try {
				int temp = in.read(byteBuffer);
				if (temp > 0) {
					this.onReceive(addr, byteBuffer, temp);
				}
			} catch (IOException e) {
				// 连接被断开(被动)
				runFlag = false;
			}
		}

		// 断开连接
		try {

			if(in!=null){
				in.close();
				in = null;
			}
			if(out!=null){
				out.close();
				out = null;
			}
			if(socket!=null){
				socket.close();
				socket = null;
			}
			Log.e("toBLue", "socket接收数据初始化=" + (socket != null));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.onDisconnect(addr);
	}

	/**
	 * 接收到数据
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 *
	 * @param addr 连接到的Socket地址
	 * @param s    收到的字符串
	 */
	public abstract void onReceive(InetAddress addr, byte[] s, int length);

	/**
	 * 连接断开
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 *
	 * @param addr 连接到的Socket地址
	 */
	public abstract void onDisconnect(InetAddress addr);
}
