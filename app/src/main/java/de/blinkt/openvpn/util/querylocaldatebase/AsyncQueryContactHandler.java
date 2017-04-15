package de.blinkt.openvpn.util.querylocaldatebase;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.PinYinConverNumber;
import de.blinkt.openvpn.util.pinyin.CharacterParser;
import de.blinkt.openvpn.util.pinyin.PinyinComparator;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class AsyncQueryContactHandler extends AsyncQueryHandler {
    QueryCompleteListener queryCompleteListener;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    /**
     * @author Administrator
     */


    private Map<String, ContactBean> contactIdMap = null;
    public List<ContactBean> mAllLists;
    private List<ContactBean> mMembers = new ArrayList<>();

    public AsyncQueryContactHandler(QueryCompleteListener queryCompleteListener, ContentResolver cr) {
        super(cr);
        pinyinComparator = new PinyinComparator();
        characterParser = CharacterParser.getInstance();
        this.queryCompleteListener = queryCompleteListener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            if (contactIdMap == null)
                contactIdMap = new HashMap<>();
            if (mAllLists == null)
                mAllLists = new ArrayList<>();
            cursor.moveToFirst(); // 游标移动到第一项
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                String name = cursor.getString(1);
                String number = cursor.getString(2);
                String sortKey = cursor.getString(3);
                int contactId = cursor.getInt(4);
                Long photoId = cursor.getLong(5);
                String lookUpKey = cursor.getString(6);
                number = number.replace(" ", "");
                if (contactIdMap.containsKey(name)) {
                    ContactBean contac = contactIdMap.get(name);
                    if (contac != null) {
                        mMembers.remove(contac);
                        contac.setPhoneNum(contac.getPhoneNum() + "," + number);
                        mMembers.add(contac);
                    }
                } else {
                    // 创建联系人对象
                    ContactBean contact = new ContactBean();
                    contact.setContactId(contactId);
                    contact.setDesplayName(name);
                    contact.setPhoneNum(number);
                    contact.setSortKey(sortKey);
                    if (!TextUtils.isEmpty(name)) {
                        contact.setFormattedNumber(PinYinConverNumber.getInstance().getNameNum(name));
                    }

//                    else{
//                        contact.setFormattedNumber(PinYinConverNumber.getInstance().getNameNum(number));
//                    }

                    String pinyin = characterParser.getSelling(name);
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    if (sortString.matches("[A-Z]")) {
                        contact.setSortLetters(sortString.toUpperCase());
                    } else {
                        contact.setSortLetters("#");
                    }

                    contact.setPhotoId(photoId);
                    contact.setBitmapHeader(getContactPhoto(ICSOpenVPNApplication.getContext(),number));
                    contact.setLookUpKey(lookUpKey);
                    mMembers.add(contact);
                    contactIdMap.put(name, contact);
                }
            }

            Collections.sort(mMembers, pinyinComparator);
            mAllLists.addAll(mMembers);

        }
        queryCompleteListener.queryComplete(mAllLists);
        super.onQueryComplete(token, cookie, cursor);
        if (cursor != null) {
            cursor.close();
        }
    }



    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    public static Bitmap getContactPhoto(Context context, String number) {
        // 获得Uri
        Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/"
                + "data/phones/filter/" + number);
        // 查询Uri，返回数据集
        Cursor cursorCantacts = context.getContentResolver().query(
                uriNumber2Contacts,
                null,
                null,
                null,
                null);
        Bitmap bmp_head = null;
        // 如果该联系人存在
        if (cursorCantacts.getCount() > 0) {
            // 移动到第一条数据
            cursorCantacts.moveToFirst();
            // 获得该联系人的contact_id
            Long contactID = cursorCantacts.getLong(cursorCantacts.getColumnIndex("contact_id"));
            // 获得contact_id的Uri
            Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactID);
            // 打开头像图片的InputStream
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
            // 从InputStream获得bitmap
//            try {
//
//                byte[] data=readStream(input);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeByteArray(data, 0, data.length, options);
//            options.inSampleSize =2;
//            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
//            }catch (Exception e){
//
//            }
            try {

                byte[] data=readStream(input);
                if(data!=null) {
                    bmp_head = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    double scale =10;
//                    int bmpWidth = bmp_head.getWidth();
//                    int bmpHeight = bmp_head.getHeight();
////        /* 放大以后的宽高，一定要强制转换为float型的 */
//                    float    scaleWidth = (float) (1 * scale);
//                    float   scaleHeight = (float) (1 * scale);
//
//        /* 产生resize后的Bitmap对象 */
//                    Matrix matrix = new Matrix();
//                    matrix.postScale(scaleWidth, scaleHeight);
//                    Bitmap resizeBmp = Bitmap.createBitmap(bmp_head, 0, 0, bmpWidth, bmpHeight,
//                            matrix, true);
//
//                    int resizeBmpWidth = resizeBmp.getWidth();
//                    int resizeBmpHeight = resizeBmp.getHeight();
//                    Bitmap output = Bitmap.createBitmap(resizeBmpWidth, resizeBmpHeight, Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(output);
//                    int color = 0xff424242;
//                    Paint paint = new Paint();
//                    Rect rect = new Rect(resizeBmpWidth/2-(int)(resizeBmpWidth/2*Math.sqrt(2)/2), resizeBmpHeight/2-(int)(resizeBmpHeight/2*Math.sqrt(2)/2), resizeBmpWidth/2+(int)(resizeBmpWidth/2*Math.sqrt(2)/2)
//                            , resizeBmpHeight/2+(int)(resizeBmpHeight/2*Math.sqrt(2)/2));
//                    Log.e("getContactPhoto","resizeBmp="+resizeBmp.getWidth()+","+resizeBmp.getHeight()+","+(int)(resizeBmpWidth/2*Math.sqrt(2)/2)+","+(int)(resizeBmpHeight/2*Math.sqrt(2)/2));
//                    Log.e("getContactPhoto","bmp_head="+bmp_head.getWidth()+bmp_head.getHeight());
//                    RectF rectF = new RectF(rect);
//
//                    paint.setAntiAlias(true);
//                    canvas.drawARGB(0, 0, 0, 0);
//                    paint.setColor(color);
//                    canvas.drawRoundRect(rectF, 5, 5, paint);
//                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//                    canvas.drawBitmap(resizeBmp, rect, rect, paint);
//                    int outputWidth = output.getWidth();
//                    int outputHeight = output.getHeight();
//                    Bitmap resizeOutputBmp = Bitmap.createBitmap(output, 0, 0, outputWidth, outputHeight,
//                            matrix, true);
//                    return  output;
                }

            }catch (Exception e){

            }

        }
        return bmp_head;
    }
}