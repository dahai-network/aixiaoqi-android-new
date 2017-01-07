package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/25 0025.
 */
public class EBizOrderListEntity implements Serializable {
    private String totalRows;
    private List<OrderInfo> list;

    public String getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(String totalRows) {
        this.totalRows = totalRows;
    }

    public List<OrderInfo> getList() {
        if(list==null){
            list=new ArrayList<>();
        }
        return list;
    }

    public void setList(List<OrderInfo> list) {
        this.list = list;
    }

    public  class OrderInfo implements Serializable{
        private String OrderByZCID;
        private String OrderByZCNum;
        private String Quantity;
        private String CallPhone;
        private List<SelectNumber> SelectionedNumberList;

        public String getOrderByZCID() {
            return OrderByZCID;
        }

        public void setOrderByZCID(String orderByZCID) {
            OrderByZCID = orderByZCID;
        }

        public String getOrderByZCNum() {
            return OrderByZCNum;
        }

        public void setOrderByZCNum(String orderByZCNum) {
            OrderByZCNum = orderByZCNum;
        }

        public String getQuantity() {
            return Quantity;
        }

        public void setQuantity(String quantity) {
            Quantity = quantity;
        }

        public String getCallPhone() {
            return CallPhone;
        }

        public void setCallPhone(String callPhone) {
            CallPhone = callPhone;
        }

        public List<SelectNumber> getSelectionedNumberList() {
            return SelectionedNumberList;
        }

        public void setSelectionedNumberList(List<SelectNumber> selectionedNumberList) {
            SelectionedNumberList = selectionedNumberList;
        }

     public    class SelectNumber implements Serializable{
            private String MobileNumber;

            public String getMobileNumber() {
                return MobileNumber;
            }

            public void setMobileNumber(String mobileNumber) {
                MobileNumber = mobileNumber;
            }
        }
    }
}
