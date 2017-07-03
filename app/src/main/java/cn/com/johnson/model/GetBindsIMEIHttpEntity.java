package cn.com.johnson.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/29.
 */

public class GetBindsIMEIHttpEntity implements Serializable {

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    private List<String> list;


}
