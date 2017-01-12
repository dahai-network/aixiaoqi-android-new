package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/2 0002.
 */
public class OrderInfoEntity implements Serializable{
    private OrderInfo order;
    public class OrderInfo implements Serializable{
        private String OrderByZCSelectionNumberID;
        private String OrderByZCSelectionNumberNum;
        private String OrderDate;
        private String PaymentMethod;
    }
}
