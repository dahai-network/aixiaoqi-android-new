package cn.com.johnson.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/6.
 */
public class IndexBannerData {
	private int status;
	private String msg;
	private ArrayList<IndexBannerUrl> data;

	public ArrayList<IndexBannerUrl> getData() {
		return data;
	}

	public void setData(ArrayList<IndexBannerUrl> data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public class IndexBannerUrl {
		private String Url;
		private String Image;

		public String getUrl() {
			return Url;
		}

		public void setUrl(String url) {
			Url = url;
		}

		public String getImage() {
			return Image;
		}

		public void setImage(String image) {
			Image = image;
		}

		@Override
		public String toString() {
			return "IndexBannerUrl{" +
					"Url='" + Url + '\'' +
					", Image='" + Image + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "IndexBannerData{" +
				"status=" + status +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
