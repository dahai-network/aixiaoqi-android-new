package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class SelectNumberEntity  implements Serializable{
    private String totalRows;
    private List<SelectInfo> list;


   public  class SelectInfo{
        private String MobileNumber;
        private String Price;

        public String getMobileNumber() {
            return MobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            MobileNumber = mobileNumber;
        }

        public String getPrice() {
            return Price;
        }

        public void setPrice(String price) {
            Price = price;
        }
    }

    public String getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(String totalRows) {
        this.totalRows = totalRows;
    }

    public List<SelectInfo> getList() {
        return list;
    }

    public void setList(List<SelectInfo> list) {
        this.list = list;
    }
}
