package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/29 0029.
 */
public class EBizOrderDetailEntity implements Serializable {

    private String OrderByZCNum;
    private String Quantity;
    private String CallPhone;
    private List<NumberInfo> SelectionedNumberList;

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

    public List<NumberInfo> getSelectionedNumberList() {
        if(SelectionedNumberList==null){
            SelectionedNumberList=new ArrayList<>();
        }
        return SelectionedNumberList;
    }

    public void setSelectionedNumberList(List<NumberInfo> selectionedNumberList) {
        SelectionedNumberList = selectionedNumberList;
    }

    public class NumberInfo{
        private String Name;
        private String IdentityNumber;
        private String ProvinceName;
        private String CityName;
        private String MobileNumber;
        private String Price;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getIdentityNumber() {
            return IdentityNumber;
        }

        public void setIdentityNumber(String identityNumber) {
            IdentityNumber = identityNumber;
        }

        public String getProvinceName() {
            return ProvinceName;
        }

        public void setProvinceName(String provinceName) {
            ProvinceName = provinceName;
        }

        public String getCityName() {
            return CityName;
        }

        public void setCityName(String cityName) {
            CityName = cityName;
        }

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
}
