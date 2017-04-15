package cn.com.johnson.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.NetworkUtils;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

@SuppressLint("HandlerLeak")
/**
 * 自定义webview控件
 * @author JOLY WANG
 *
 */
public class CustomWebViewLayout extends LinearLayout implements OnClickListener {

	private static String TAG = CustomWebViewLayout.class.getSimpleName();
	//刷新控件
	private PtrClassicFrameLayout mPtrFrame;
	//错误层控件 
	private View mErrorView;


	//网页浏览控件
	public WebView mWebView;
	//记录当前url地址
	private String mCurrentUrl = "";
	//初始url地址
	private String mFirstUrl = "";
	//上下文对象
	private Context mContext;
	//private Activity mActivity;
	//是否为错误状态
	public boolean isError;
	//是否时首页
	private boolean isHome;

	//主页地址（用于兼容android3.0 webview reload特殊处理使用）
	private String mainPage = "";

	//是否为单页模式（只显示一个页面，子链接等以通知形式通知程序进行特殊处理）
	private Boolean mIsSinglePageModel = false;

	//消息处理对象
	private Handler mHandler = new Handler() {
		// 注意：在各个case后面不能做太耗时的操作，否则出现ANR对话框  
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					Log.d("testTimeout", "timeout...........");
					if (CustomWebViewLayout.this.mWebView != null && CustomWebViewLayout.this.mWebView.getProgress() < 100) {
						Log.d("testTimeout", ".getProgress() < 100");


						mWebView.setVisibility(View.GONE);

						//if(mActivity!=null)
						//((WebContainerActivity)mActivity).dismissLoading();
						if (mContext != null)
							//((WebContainerActivity)mContext).dismissLoading();
							mErrorView.setVisibility(View.VISIBLE);
						mWebView.stopLoading();
						isError = true;

						timer.cancel();
						timer.purge();
					}


					break;
			}
		}
	};
	//计时器对象（用于页面请求计时操作，如超时显示错误层控件）
	private Timer timer;
	//页面请求计时超时时间
	private long timeout = 60000;
	//手势覆盖层
	private GestureOverlayView gov;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public CustomWebViewLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public CustomWebViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

	public CustomWebViewLayout(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}


	/**
	 * 控件初始化函数
	 *
	 * @param context
	 */
	private void init(Context context) {
		Log.i(TAG, "INIT()");
		mContext = context;

		LayoutInflater.from(context).inflate(R.layout.customwebview, this);


		mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_web_view_frame);
		mPtrFrame.setLastUpdateTimeRelateObject(this);
		mPtrFrame.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, mWebView, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				System.out.println("webview url:" + mWebView.getUrl());
				if (mWebView.getUrl() == null) {
					Log.i(TAG, "setUrl!!!:" + mainPage);
					loadUrl(mWebView, mainPage);
				} else
					mWebView.reload();
			}
		});
		mWebView = (WebView) findViewById(R.id.rotate_header_web_view);

		webViewAddJS();

		mErrorView = findViewById(R.id.error_view);

		mErrorView.setOnClickListener(this);
		isError = false;
		isHome = false;

		setWebClient();

	}


	/*
	 * 获取当前网页地址
	 */
	public String getUrl() {
		if (mWebView != null) {
			return mWebView.getUrl();
		}
		return null;
	}

	/**
	 * 清除复位ui
	 */
	public void clearUI() {
		mWebView.setVisibility(View.GONE);
		setNormalShow();
	}

	/**
	 * 设置并加载url
	 *
	 * @param activity:待加载的activity
	 * @param mainurl:主页面，为空则不做修改
	 * @param url:待加载的url地址
	 */
	public void setUrl(Activity activity, String mainurl, String url) {
		this.mContext = activity;

		if (mContext != null) {
			if (!url.contains("javascript:")) {

				if (mainurl != null && !mainurl.equals(""))
					this.mainPage = mainurl;
				if (mWebView != null) {

					mFirstUrl = url;
					//mWebView.destroyDrawingCache();
					//mWebView.destroy();
					mWebView.clearHistory();
				}


				mPtrFrame.postDelayed(new Runnable() {
					@Override
					public void run() {
						mPtrFrame.autoRefresh();
					}
				}, 100);
			} else {
				mWebView.loadUrl(url);
			}

		}
	}

	/**
	 * 刷新url操作
	 */
	public void reloadUrl() {
		setNormalShow();
		if (mWebView != null)
			mWebView.reload();
	}


	/**
	 * 加载URL到WEBVIEW（含Cookie）
	 *
	 * @param url
	 */
	private void loadUrl(WebView webView, String url) {

		Log.i(TAG, "Come!!!!!mWebView url:" + url);
		if (mContext != null) {

			mCurrentUrl = url;

			// if(!mCurrentUrl.contains("javascript:"))

			webView.loadUrl(mCurrentUrl);
			mWebView = webView;
			//mWebView.loadUrl(mCurrentUrl);
			Log.i(TAG, "web url: " + url);
		}
	}


	//返回
	public void goBack() {
		/*
		Log.i(TAG, "back url: " + mBackUrl);
		mWebView.stopLoading();
	    
		
		if (!TextUtils.isEmpty(mBackUrl) &&  !mBackUrl.equals("null")) {
			loadUrl(mWebView, mBackUrl);
		} else {
			Log.i(TAG, "goBack!!!" + mBackUrl);
			if(mWebView!=null)
				//loadUrl(mWebView, mainPage);
				mWebView.goBack();
		}
		*/
		Log.i(TAG, "back url: " + mIsSinglePageModel);
		if (mWebView != null) {


			Log.i(TAG, "mWebView!=null!!mWebView.getOriginalUrl():" + mWebView.getOriginalUrl() + "|mFirstUrl:" + mFirstUrl);
			//resetRefrshState();

			if (mWebView.canGoBack() && !mWebView.getOriginalUrl().trim().equals(mFirstUrl.trim())) {

				mWebView.goBack();


			} else {

				if (mContext instanceof Activity) {
					((Activity) mContext).finish();
				}

			}


		}
	}

	/**
	 * 如果不是单页面模式（即是悬浮模式），则隐藏该悬浮。
	 */
	private void disswebview() {
		if (!mIsSinglePageModel) {
			Log.i(TAG, "disswebview");

			//HTML 5 Web 存储删除处理
			mWebView.loadUrl("javascript:(function(){for(var p in sessionStorage) delete sessionStorage[p];})();");
			//做清空内容操作，好的机子会闪现刷新前的页面问题。
			mWebView.loadData("<div><div>", "text/html", "utf-8");
		}
	}

	/**
	 * 单击时间处理
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			//返回按钮点击事件

			//错误层控件点击事件
			case R.id.error_view:
				setNormalShow();
				loadUrl(mWebView, mCurrentUrl);
				break;
			default:
				break;
		}
	}

	//设置正常显示
	public void setNormalShow() {
		isError = false;
		mErrorView.setVisibility(View.GONE);
	}


	/**
	 * 设置单一页面模式状态
	 *
	 * @param issinglepagemodel
	 */
	public void setSinglePageModel(Boolean issinglepagemodel) {
		mIsSinglePageModel = issinglepagemodel;
	}

	/**
	 * 旧的导航功能菜单按钮点击事件监听
	 *
	 * @author JOLY WANG
	 */
	private class OnOldOptionBtnClickListener implements OnClickListener {
		private String url;

		public OnOldOptionBtnClickListener(String url) {
			this.url = url;
		}

		@Override
		public void onClick(View v) {
			loadUrl(mWebView, url);
			//mWebView.loadUrl(url);
		}
	}

	/**
	 * 导航功能菜单按钮点击事件监听
	 *
	 * @author JOLY WANG
	 */
	private class OnOptionBtnClickListener implements OnClickListener {
		private String url;

		public OnOptionBtnClickListener(String url) {
			this.url = url;
		}

		@Override
		public void onClick(View v) {
			//loadUrl(mWebView, url);
			mWebView.loadUrl(url);
		}
	}

	public void onDestory() {
		mWebView.destroy();
		mWebView = null;
	}


	private void setWebClient() {
		WebViewClient mWebViewClient = new WebViewClient() {

			private Boolean isLoadPage = false;

			/**
			 * 页面加载完成回调的函数
			 */
			@SuppressLint("NewApi")
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "WV:onPageFinished");


				if (!isError) {
					mErrorView.setVisibility(View.GONE);

					mWebView.setVisibility(View.VISIBLE);
				} else {
					mErrorView.setVisibility(View.VISIBLE);

					mWebView.setVisibility(View.GONE);
				}

				if (timer != null) {
					timer.cancel();
					timer.purge();
				}

				if (isLoadPage) {
					mPtrFrame.refreshComplete();
					isLoadPage = false;
					view.loadUrl("javascript:AppEvent_Complete();");
				}
				//view.loadUrl("javascript:window.App.showSource(alert(document.documentElement.innerHTML));");
				//view.loadUrl("javascript:alert(document.documentElement.innerHTML);");
				//view.loadUrl("javascript:alert(navigator.userAgent.toLowerCase());");


				Log.i(TAG, "WV:onPageFinished end");
			}

			/**
			 * 页面加载开始回调的函数
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.i(TAG, "onPageStarted:" + url + "|mainpage:" + mainPage);
				isLoadPage = true;
				  
	    		
	    		/*
				int index = url.indexOf("?");
		        Log.i(CustomWebViewLayout.TAG, "onPageStarted index:" + url);
		        if (index != -1)
		        	url = url.substring(0, index);
		        isHome = url.toLowerCase(Locale.getDefault()).contains(
								mainPage.toLowerCase(Locale.getDefault()));
		        */
				int index = url.indexOf("?");
				Log.i(CustomWebViewLayout.TAG, "onPageStarted index:" + url);
				if (index != -1)
					url = url.substring(0, index);

				int firstindex = mFirstUrl.indexOf("?");
				String editFirstUrl = mFirstUrl;
				if (firstindex != -1) {
					editFirstUrl = editFirstUrl.substring(0, firstindex);
				}

				// isHome = url.toLowerCase(Locale.getDefault()).contains(
				//		editFirstUrl.toLowerCase(Locale.getDefault()));

				isHome = url.equals(mFirstUrl) || url.toLowerCase(Locale.getDefault()).contains(
						editFirstUrl.toLowerCase(Locale.getDefault())) || !url.contains("http://");

				// if(isHome)
				//((WebContainerActivity)mActivity).showContainer();


				/*
				if(!((WebContainerActivity)mActivity).mIsReload){
					((WebContainerActivity)mActivity).showBottom(isHome);
					((WebContainerActivity)mActivity).setScrollable(isHome);
				 }
				*/


				timer = new Timer();
				TimerTask tt = new TimerTask() {
					@Override
					public void run() {
						/*
						 * 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
		                 */
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);

					}
				};
				timer.schedule(tt, timeout, timeout);


			}

			/**
			 * 加载错误异常回调函数
			 */
			@Override
			public void onReceivedError(WebView view, int errorCode,
										String description, String failingUrl) {
				isError = true;
				//removeSynCookies();

			}

			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				//handler.cancel(); 默认的处理方式，WebView变成空白页
//                        //接受证书
				handler.proceed();
				//handleMessage(Message msg); 其他处理
			}

		};
		mWebView.setWebViewClient(mWebViewClient);
	}

	@SuppressLint({"SetJavaScriptEnabled", "ResourceAsColor"})
	private void webViewAddJS() {
		WebSettings webSettings = mWebView.getSettings();
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webSettings.setJavaScriptEnabled(true);
		if (NetworkUtils.isNetworkAvailable(mContext)) {
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		webSettings.setAllowFileAccess(false);
		webSettings.setSupportZoom(true);
		// 设置出现缩放工具
		webSettings.setBuiltInZoomControls(true);
		//扩大比例的缩放
		webSettings.setUseWideViewPort(true);
		//自适应屏幕
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(true);
		//支持android4.0
		webSettings.setBuiltInZoomControls(true);
		webSettings.setNeedInitialFocus(false);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setDomStorageEnabled(true);
	}
}