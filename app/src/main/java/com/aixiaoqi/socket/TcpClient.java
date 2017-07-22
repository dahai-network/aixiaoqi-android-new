package com.aixiaoqi.socket;

import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;


/**
 * TCP Socket客户端
 *
 * @author jzj1993
 * @since 2015-2-22
 */
public abstract class TcpClient implements Runnable {


	private boolean connect = false;
	private SocketTransceiver transceiver;

	/**
	 * 建立连接
	 * <p>
	 * 连接的建立将在新线程中进行
	 * <p>
	 * 连接建立成功，回调{@code onConnect()}
	 * <p>
	 * 连接建立失败，回调{@code onConnectFailed()}
	 */
	public void connect() {

		new Thread(this).start();
	}


	@Override
	public void run() {
		try {
			Logger.d("TCP开始建立连接"+connect);
			if (!connect) {
				connectSocket();
			}
		} catch (ConnectException e) {
			connect = false;
			e.printStackTrace();
			this.onConnectFailed();
		} catch (Exception e) {
			e.printStackTrace();
			connect = false;
			this.onConnectFailed();
		}
	}

	//创建连接
	private void connectSocket() throws IOException {
		Logger.d("创建TCP");
		SocketAddress address = new InetSocketAddress(SocketConstant.hostIP, SocketConstant.port);
		Socket socket = new Socket();
		//TCP保活
		socket.setKeepAlive(true);
		socket.connect(address, 30000);
		socket.setTcpNoDelay(true);
		transceiver = new SocketTransceiver(socket) {
			@Override
			public void onReceive(InetAddress addr, byte[] s, int length) {
				TcpClient.this.onReceive(this, s, length);
			}

			@Override
			public void onDisconnect(InetAddress addr) {
				connect = false;
				TcpClient.this.onDisconnect(this);
			}
		};
		transceiver.start();
		connect = true;
		this.onConnect(transceiver);
	}

	/**
	 * 断开连接
	 * <p>
	 * 连接断开，回调{@code onDisconnect()}
	 */
	public void disconnect() {
		if (transceiver != null) {
			transceiver.stop();
			transceiver = null;
		}
	}

	/**
	 * 判断是否连接
	 *
	 * @return 当前处于连接状态，则返回true
	 */
	public boolean isConnected() {
		return connect;
	}

	/**
	 * 获取Socket收发器
	 *
	 * @return 未连接则返回null
	 */
	public SocketTransceiver getTransceiver() {
		return isConnected() ? transceiver : null;
	}

	/**
	 * 连接建立
	 *
	 * @param transceiver
	 *            SocketTransceiver对象
	 */
	public abstract void onConnect(SocketTransceiver transceiver);

	/**
	 * 连接建立失败
	 */
	public abstract void onConnectFailed();

	/**
	 * 接收到数据
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 *
	 * @param transceiver SocketTransceiver对象
	 * @param s           字符串
	 */
	public abstract void onReceive(SocketTransceiver transceiver, byte[] s, int length);

	/**
	 * 连接断开
	 * <p>
	 * 注意：此回调是在新线程中执行的
	 *
	 * @param transceiver SocketTransceiver对象
	 */
	public abstract void onDisconnect(SocketTransceiver transceiver);
}
