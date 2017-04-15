package de.blinkt.openvpn.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import de.blinkt.openvpn.views.contact.Indexable;

public class ContactBean implements Serializable, Indexable {

	private int contactId; //id
	private String desplayName;//姓名
	private String phoneNum; // 电话号码

	private String sortKey; // 排序用的
	private Long photoId; // 图片id
	private String lookUpKey;
	private int selected = 0;
	private String[] formattedNumber=new String[2];
	private String sortLetters;
	private int header;
	private String pinyin; // 姓名拼音
	 private  Bitmap bitmapHeader;
	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	@Override
	public String getIndex() {
		return sortLetters;
	}

	public List<String> getPhoneNumList() {
		return phoneNumList;
	}

	public void setPhoneNumList(List<String> phoneNumList) {
		this.phoneNumList = phoneNumList;
	}

	private List<String> phoneNumList;

	public int getHeader() {
		return header;
	}

	public void setHeader(int header) {
		this.header = header;
	}

//	private void writeObject(ObjectOutputStream oos) throws IOException {
//		// This will serialize all fields that you did not mark with 'transient'
//		// (Java's default behaviour)
//		oos.defaultWriteObject();
//		// Now, manually serialize all transient fields that you want to be serialized
//		if(bitmapHeader!=null){
//			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//			boolean success = bitmapHeader.compress(Bitmap.CompressFormat.PNG, 20, byteStream);
//			if(success){
//				oos.writeObject(byteStream.toByteArray());
//			}
//		}
//	}
//
//	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
//		// Now, all again, deserializing - in the SAME ORDER!
//		// All non-transient fields
//		ois.defaultReadObject();
//		// All other fields that you serialized
//		byte[] image = (byte[]) ois.readObject();
//		if(image != null && image.length > 0){
//			bitmapHeader = BitmapFactory.decodeByteArray(image, 0, image.length);
//		}
//	}

	public Bitmap getBitmapHeader() {
		return bitmapHeader;
	}

	public void setBitmapHeader(Bitmap bitmapHeader) {
		this.bitmapHeader = bitmapHeader;
	}

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
	}

	public String getDesplayName() {
		return desplayName;
	}

	public void setDesplayName(String desplayName) {
		this.desplayName = desplayName;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public Long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}

	public String getLookUpKey() {
		return lookUpKey;
	}

	public void setLookUpKey(String lookUpKey) {
		this.lookUpKey = lookUpKey;
	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	public String[] getFormattedNumber() {
		return formattedNumber;
	}

	public void setFormattedNumber(String[] formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	@Override
	public String toString() {
		return "ContactBean{" +
				"contactId=" + contactId +
				", desplayName='" + desplayName + '\'' +
				", phoneNum='" + phoneNum + '\'' +
				", sortKey='" + sortKey + '\'' +
				", photoId=" + photoId +
				", lookUpKey='" + lookUpKey + '\'' +
				", selected=" + selected +
				", formattedNumber='" + formattedNumber + '\'' +
				", pinyin='" + pinyin + '\'' +
				'}';
	}



}