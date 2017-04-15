package de.blinkt.openvpn.http;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.BaseEntry;
import de.blinkt.openvpn.activities.LoginMainActivity;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.PublicEncoderTools;
import de.blinkt.openvpn.util.SharedUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/9/6 0006.
 */
public abstract class CommonHttp implements Callback, Runnable {
	protected static final int GET_MODE = 0;
	protected static final int POST_MODE = 1;
	protected static final int POST_IMAGE = 2;
	protected static final int POST_JSON = 3;
	private static final String CACHE_FILE = "aixiaoqi_cache_file";
	protected int sendMethod_ = POST_MODE;
	protected String slaverDomain_;
	private static Context context_;
	protected String hostUrl_;
	protected int cmdType_;
	private int status;
	private String msg;
	private static Call call;
	private String TAG = "CommonHttp";

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	private Object data;
	protected HashMap<String, String> params;
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	protected boolean isDownload;
	static OkHttpClient client;
	//公钥
	private String PARTNER = "2006808";
	private Handler mHandler = new Handler();
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");


	private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
		@Override
		public synchronized Response intercept(Chain chain) throws IOException {
			Request request = chain.request();
			if (!NetworkUtils.isNetworkAvailable(context_)) {
				request = request.newBuilder()
						.cacheControl(CacheControl.FORCE_CACHE)
						.build();
				Log.d("CacheInterceptor", "no network");

			}
			Log.i("http", "request=" + request);
			Response response = chain.proceed(request);
			Log.i("http", "response=" + response);

			String cacheControl = request.cacheControl().toString();
			if (NetworkUtils.isNetworkAvailable(context_)) {
				return response.newBuilder()
						//这里设置的为0就是说不进行缓存，我们也可以设置缓存时间
						.removeHeader("Cache-Control")
						.header("Cache-Control", "public, max-age=" + 0)
						.removeHeader("Pragma")
						.build();
			} else {
				if (TextUtils.isEmpty(cacheControl)) {
					cacheControl = "public, only-if-cached, max-stale=" + 4 * 24 * 60 * 60;
				}
				Log.i("http", "response.code():" + response.code());
				return response.newBuilder()
						.removeHeader("Cache-Control")
						.header("Cache-Control", cacheControl)
						.removeHeader("Pragma")
						.build();
			}
		}
	};

	@Override
	public void run() {
		excute();
	}

	protected abstract void BuildParams() throws Exception;

	protected abstract void parseResult(String response);

	protected abstract void errorResult(String s);

	protected abstract void noNet();

	public int getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	/**
	 * 响应
	 * @param arg0
	 * @param response
	 * @throws IOException
	 */
	@Override
	public void onResponse(Call arg0, Response response) throws IOException {
		if (response.isSuccessful()) {
			if (isDownload) {
				Download(response);
				return;
			}
			String responseBody = response.body().string();
			Gson gson = new Gson();
			BaseEntry baseEntry = gson.fromJson(responseBody, BaseEntry.class);
			status = baseEntry.getStatus();
			msg = baseEntry.getMsg();
			data = baseEntry.getData();
			if (status == 1) {
				right(gson.toJson(baseEntry.getData()));
			} else if (status == -999) {
//				ICSOpenVPNApplication.getInstance().finishAllActivity();
				//token过期
				if (!CommonTools.isFastDoubleClick(1000)) {

					Intent intent1 = new Intent();
					intent1.setAction(SportFragment.CLEARSPORTDATA);
					intent1.setAction(ProMainActivity.STOP_CELL_PHONE_SERVICE);

					if (ICSOpenVPNApplication.uartService != null)
						ICSOpenVPNApplication.uartService.disconnect();

					if (ICSOpenVPNApplication.getInstance() != null)
						ICSOpenVPNApplication.getInstance().sendBroadcast(intent1);

					Intent intent = new Intent(context_, LoginMainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra(IntentPutKeyConstant.OTHER_DEVICE_LOGIN, context_.getResources().getString(R.string.token_interrupt));
					context_.startActivity(intent);

				}
			} else {
				right("");
			}
		} else if (response.code() >= 400 && response.code() < 500) {//网址有错
			error(response.message());
		} else if (response.code() >= 500 && response.code() < 600) {//服务器异常
			//大部分是没有缓存该页面所以出现了504的返回
			if (response.code() == 504) {
				noNetShow();
				return;
			}
			error(response.message());
		} else {
			error(response.message());
		}
	}


	private void Download(Response response) {
		File fileDir = new File(Constant.DOWNLOAD_PATH);
		//此处可以增加判断文件是否存在的逻辑
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		File file = new File(fileDir, "/upload.zip");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {

			}
		}
		InputStream is = null;
		byte[] buf = new byte[2048];
		int len = 0;
		FileOutputStream fos = null;
		try {
			is = response.body().byteStream();
			long contentLen = response.body().contentLength();
			long downloadLen = 0;
			fos = new FileOutputStream(file);
//			long currentPercent = 0;
			while ((len = is.read(buf)) != -1) {
				fos.write(buf, 0, len);
				downloadLen += len;
				//更新进度条
//                if (downloadLen * 100 / contentLen - currentPercent >= 1) {
//                    currentPercent = downloadLen * 100 / contentLen;
//                    right(String.valueOf(currentPercent));
//                }
			}
			fos.flush();
			if (downloadLen < contentLen && file.isFile() && file.exists()) {
				file.delete();
				right(Constant.DOWNLOAD_FAIL);
			} else {
				right(Constant.DOWNLOAD_SUCCEED);
			}
		} catch (Exception e) {
			right(Constant.DOWNLOAD_FAIL);
			e.printStackTrace();
		} finally {
			try {
				if (is != null) is.close();
			} catch (IOException e) {
			}
			try {
				if (fos != null) fos.close();
			} catch (IOException e) {
			}
		}
	}

	private void right(final String message) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				parseResult(message);
			}
		});
	}

	private void error(final String message) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				errorResult(message);
			}
		});
	}

	private void noNetShow() {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				noNet();
			}
		});
	}

	@Override
	public void onFailure(Call call, IOException e) {
		if (!NetworkUtils.isNetworkAvailable(context_)) {
			noNetShow();
			return;
		}
		String s = e.getMessage();
		error(s);
	}

	public static void setContext(Context context) {
		context_ = context;
	}

	public CommonHttp() {
		if (null == client) {
			synchronized (this) {
//				File file = context_.getCacheDir();
				File cacheFile = new File(context_.getCacheDir(), CACHE_FILE);
				Cache cache = new Cache(cacheFile, 1024 * 1024 * 5); //5Mb
				client = new OkHttpClient().newBuilder().connectTimeout(15, TimeUnit.SECONDS)
						.readTimeout(20, TimeUnit.SECONDS)
						.readTimeout(20, TimeUnit.SECONDS)
						.retryOnConnectionFailure(true)
						.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
						.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
						.cache(cache)
						.build();
			}
			try {
				setCertificates(context_.getAssets().open("api.unitoys.com.crt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setCurrentUrl() {
		if (Constant.IS_DEBUG) {
			hostUrl_ = HttpConfigUrl.NEWBASICAPITEST + slaverDomain_;
		} else {
			hostUrl_ = HttpConfigUrl.NEWBASICAPI + slaverDomain_;
		}

	}


	//set request params
	void excute() {
		try {
			BuildParams();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setCurrentUrl();
		sendRequest();
	}

	/**
	 * 发送请求
	 */
	private void sendRequest() {
		String expires = System.currentTimeMillis() / 1000L + "";
		String md5 = PublicEncoderTools.MD5Encode(PARTNER + expires + HttpConfigUrl.PRIVATE_KEY);
		SharedUtils sharedUtils = SharedUtils.getInstance();

		if (isDownload) {
			hostUrl_ = slaverDomain_;
		}
		if (sendMethod_ == GET_MODE) {
			if (params != null && !params.isEmpty()) {
				hostUrl_ = hostUrl_ + "?";
				Iterator iter = params.entrySet().iterator();
				int i = 0;
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					if (i == params.size() - 1) {
						hostUrl_ += key + "=" + value;
					} else {
						hostUrl_ += key + "=" + value + "&";
					}
					i++;
				}
				iter.remove();
			}
			Request request;

			//获取request对象
			request = getRequest(expires, md5, sharedUtils);
			call = client.newCall(request);
			try {
				call.enqueue(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sendMethod_ == POST_MODE) {
			FormBody.Builder formEncodingBuilder = new FormBody.Builder();
			if (params != null && !params.isEmpty()) {
				Iterator iter = params.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					if (key != null && value != null) {
						formEncodingBuilder.add(key, value);
					}
				}
			}
			if (params == null) {
				formEncodingBuilder.add("1", "1");
			}
			RequestBody formBody = formEncodingBuilder.build();
			try {
				Request request;
				request = getRequest(expires, md5, sharedUtils, formBody);
				call = client.newCall(request);
				try {
					call.enqueue(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (sendMethod_ == POST_JSON) {
			Iterator iter = params.entrySet().iterator();
			Map.Entry entry = (Map.Entry) iter.next();
			RequestBody formBody = RequestBody.create(JSON, (String) entry.getValue());
			try {
				Request request;
				request = getRequest(expires, md5, sharedUtils, formBody);
				call = client.newCall(request);
				try {
					call.enqueue(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (sendMethod_ == POST_IMAGE) {
			// mImgUrls为存放图片的url集合

			MultipartBody.Builder builder = new MultipartBody.Builder("AaB03x").setType(MultipartBody.FORM);
			Set<String> set = params.keySet();
			for (String key : set) {
				builder.addFormDataPart(key, System.currentTimeMillis() + ".png", RequestBody.create(MEDIA_TYPE_PNG, new File(params.get(key))));
			}


			RequestBody requestBody = builder.build();
			Request request;
			request = getRequest(expires, md5, sharedUtils, requestBody);
			//构建请求
			call = client.newCall(request);
			try {
				call.enqueue(this);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private Request getRequest(String expires, String md5, SharedUtils sharedUtils) {
		Request request;
		//判断token是否为空
		if (TextUtils.isEmpty(sharedUtils.readString(Constant.TOKEN)))
			request = new Request.Builder().url(hostUrl_).addHeader(Constant.PARTNER, PARTNER).addHeader(Constant.EXPIRES, expires).addHeader(Constant.TERMINAL_HEADER, "Android").addHeader(Constant.VERSION_HEADER, CommonTools.getVersion(ICSOpenVPNApplication.getContext())).addHeader(Constant.SIGN, md5).build();
		else
			request = new Request.Builder().url(hostUrl_).addHeader(Constant.TOKEN, sharedUtils.readString(Constant.TOKEN)).addHeader(Constant.PARTNER, PARTNER).addHeader(Constant.EXPIRES, expires).addHeader(Constant.TERMINAL_HEADER, "Android").addHeader(Constant.VERSION_HEADER, CommonTools.getVersion(ICSOpenVPNApplication.getContext())).addHeader(Constant.SIGN, md5).build();
		return request;
	}

	private Request getRequest(String expires, String md5, SharedUtils sharedUtils, RequestBody requestBody) {
		Request request;
		if (TextUtils.isEmpty(sharedUtils.readString(Constant.TOKEN)))
			request = new Request.Builder().url(hostUrl_).post(requestBody).addHeader(Constant.PARTNER, PARTNER).addHeader(Constant.EXPIRES, expires).addHeader(Constant.TERMINAL_HEADER, "Android").addHeader(Constant.VERSION_HEADER, CommonTools.getVersion(ICSOpenVPNApplication.getContext())).addHeader(Constant.SIGN, md5).build();
		else
			request = new Request.Builder().url(hostUrl_).post(requestBody).addHeader(Constant.TOKEN, sharedUtils.readString(Constant.TOKEN)).addHeader(Constant.PARTNER, PARTNER).addHeader(Constant.EXPIRES, expires).addHeader(Constant.TERMINAL_HEADER, "Android").addHeader(Constant.VERSION_HEADER, CommonTools.getVersion(ICSOpenVPNApplication.getContext())).addHeader(Constant.SIGN, md5).build();
		return request;
	}

	public static void setCertificates(InputStream... certificates) {
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);
			int index = 0;
			for (InputStream certificate : certificates) {
				String certificateAlias = Integer.toString(index++);
				keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

				try {
					if (certificate != null)
						certificate.close();
				} catch (IOException e) {
				}
			}

			SSLContext sslContext = SSLContext.getInstance("TLS");

			TrustManagerFactory trustManagerFactory =
					TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(keyStore);
			sslContext.init
					(
							null,
							trustManagerFactory.getTrustManagers(),
							new SecureRandom()
					);
			client.newBuilder().sslSocketFactory(sslContext.getSocketFactory()).build();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void cancel() {
		if (call != null && !call.isCanceled())
			call.cancel();
	}

}
