package de.blinkt.openvpn.model;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ProductEntity {

	/**
	 * Title : 爱小器手环1代
	 * Url : http://www.baidu.com
	 * Image : http://img.unitoys.com/Unitoys/2017/04/1704081643243097922.png
	 * Price : 299
	 */

	private String Title;
	private String Url;
	private String Image;
	private int Price;

	public String getTitle() {
		return Title;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String Url) {
		this.Url = Url;
	}

	public String getImage() {
		return Image;
	}

	public void setImage(String Image) {
		this.Image = Image;
	}

	public int getPrice() {
		return Price;
	}

	public void setPrice(int Price) {
		this.Price = Price;
	}

	@Override
	public String toString() {
		return "DataBean{" +
				"Title='" + Title + '\'' +
				", Url='" + Url + '\'' +
				", Image='" + Image + '\'' +
				", Price=" + Price +
				'}';
	}

}
