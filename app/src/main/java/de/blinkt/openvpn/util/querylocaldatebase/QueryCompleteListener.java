package de.blinkt.openvpn.util.querylocaldatebase;



import java.util.List;

/**
 * Created by Administrator on 2016/9/24 0024.
 */
public interface QueryCompleteListener<T> {
   void  queryComplete(List<T> mAllLists);
}
