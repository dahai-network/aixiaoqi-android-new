package de.blinkt.openvpn.util;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
/**
 * Created by kim
 * on 2017/6/9.
 */

public class CreateFiles {

    String filenameTemp = Environment.getExternalStorageDirectory().getPath() + "/aixiaoqi_log.txt";
    public CreateFiles(String name){
        filenameTemp = Environment.getExternalStorageDirectory().getPath() + "/"+name+".txt";
    }
    public CreateFiles(){
    }

    //向已创建的文件中写入数据
    public void print(String str) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        String datetime = "";
        try {
            SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "
                    + "hh:mm:ss");
            datetime = tempDate.format(new java.util.Date()).toString();
            fw = new FileWriter(filenameTemp, true);//
            // 创建FileWriter对象，用来写入字符流
            bw = new BufferedWriter(fw); // 将缓冲对文件的输出
            String myreadline = datetime + "---" + str;
            bw.write(myreadline + "\n"); // 写入文件
            bw.newLine();
            bw.flush(); // 刷新该流的缓冲
            bw.close();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                bw.close();
                fw.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
            }
        }
    }
}
