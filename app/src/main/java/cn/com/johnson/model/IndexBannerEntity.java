package cn.com.johnson.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6.
 */
public class IndexBannerEntity implements Serializable {


	private String Url;
	private String Image;
	private String Title;

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

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
				", Title='" + Title + '\'' +
				'}';
	}


}
