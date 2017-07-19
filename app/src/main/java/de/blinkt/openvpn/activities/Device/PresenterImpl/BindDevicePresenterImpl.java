package de.blinkt.openvpn.activities.Device.PresenterImpl;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.aixiaoqi.socket.EventBusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Device.ModelImpl.BindDeviceModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.IsBindDeviceModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.SkyUpgradeModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.UpdateDeviceInfoModelImpl;
import de.blinkt.openvpn.activities.Device.Presenter.BindDevicePresenter;
import de.blinkt.openvpn.activities.Device.View.BindDeviceView;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetBindsIMEIHttp;
import de.blinkt.openvpn.http.IsBindHttp;
import de.blinkt.openvpn.model.BluetoothEntity;
import de.blinkt.openvpn.model.BluetoothMessageCallBackEntity;
import de.blinkt.openvpn.model.enentbus.BindStatue;
import de.blinkt.openvpn.model.enentbus.BlueReturnData;
import de.blinkt.openvpn.util.CheckAuthorityUtil;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.CreateFiles;
import de.blinkt.openvpn.util.SharedUtils;
import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.BASIC_MESSAGE;
import static de.blinkt.openvpn.constant.Constant.ICCID_GET;
import static de.blinkt.openvpn.util.CommonTools.getBLETime;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class BindDevicePresenterImpl extends NetPresenterBaseImpl implements BindDevicePresenter {
    BindDeviceView bindDeviceView;
    BindDeviceModelImpl bindDeviceModel;
    IsBindDeviceModelImpl isBindDeviceModel;
    UpdateDeviceInfoModelImpl updateDeviceInfoModel;
    private Handler mHandler;
    private Handler findDeviceHandler;
    private static final long SCAN_PERIOD = 20000;
    private List<BluetoothEntity> deviceList;
    private ArrayList<String> addressList;
    CreateFiles createFiles;
    SkyUpgradeModelImpl skyUpgradeModel;
    public BindDevicePresenterImpl(BindDeviceView bindDeviceView) {
        this.bindDeviceView = bindDeviceView;
        bindDeviceModel = new BindDeviceModelImpl(this);
        isBindDeviceModel = new IsBindDeviceModelImpl(this);
        updateDeviceInfoModel = new UpdateDeviceInfoModelImpl(this);
        skyUpgradeModel=new SkyUpgradeModelImpl(this);
        EventBus.getDefault().register(this);
//创建记录日志对象
        if (createFiles == null) {
            createFiles = new CreateFiles();
        }
    }

    @Override
    public void requestSkyUpgrade() {
        skyUpgradeModel.skyUpgrade();
    }

    @Override
    public void requestBindDevice(String deviceType) {
        if(bindDeviceModel!=null)
            bindDeviceModel.bindDevice(deviceAddress,deviceType);
    }

    @Override
    public void requestBindDeviceList(ArrayList<String> addresss) {
        if(addresss!=null){
            isBindDeviceModel.getDeviceState(addresss);
        }

    }

    @Override
    public void requestUpdateDeviceInfo() {
        if(updateDeviceInfoModel!=null)
        updateDeviceInfoModel.updateDeviceInfo();
    }

    /**
     * 查看手环或者钥匙扣是否已绑定，如果已经绑定过就提醒用户已绑定。
     * 未绑定，手环直接发送绑定请求，等绑定成功以后才去连接
     * 钥匙扣直接连接，等用户按确定绑定以后才去向服务气发送绑定请求
     *
     * @param cmdType
     * @param object
     */

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        //判断是否绑定过，如果绑定过就不在绑定，换设备绑定，如果没有绑定过则开始绑定。
        if (cmdType == HttpConfigUrl.COMTYPE_ISBIND_DEVICE) {
            IsBindHttp http = (IsBindHttp) object;
            //记录当前日志
            createFiles.print("监听是否绑定服务器返回的值" + http.getIsBindEntity().getBindStatus() + "");

            if (http.getStatus() == 1 && http.getIsBindEntity() != null) {
                if (http.getIsBindEntity().getBindStatus() == 0) {
                    if (ICSOpenVPNApplication.uartService != null) {
                        if (bindDeviceView.getDeviceName().contains(Constant.UNITOYS)) {
                            requestBindDevice("0");
                        } else if (bindDeviceView.getDeviceName().contains(Constant.UNIBOX)) {
                            Log.e("deviceAddress", "deviceAddress=" + deviceAddress);
                            bindDeviceView.connect(deviceAddress);//
                        }
                    } else {
                        //如果蓝牙服务没有打开去打开蓝牙设备
                        bindDeviceView.showToast(R.string.connect_failure);
                        bindDeviceView.finishView();
                    }
                } else if (http.getIsBindEntity().getBindStatus() == 1) {
                    //记录当前日志
                    createFiles.print("设备已绑定" +"address="+deviceAddress+"--状态="+ http.getIsBindEntity().getBindStatus() + "");
                    bindDeviceView.showToast(R.string.device_already_bind);
                    bindDeviceView.finishView();
                }
            } else {
                bindDeviceView.showToast(R.string.service_is_error);
                bindDeviceView.finishView();
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_BIND_DEVICE) {
            Log.i("BindDevicePresenterImpl", "绑定设备返回：" + object.getMsg() + ",返回码：" + object.getStatus());
            if (object.getStatus() == 1) {
                SharedUtils.getInstance().writeString(Constant.IMEI, deviceAddress);
                SharedUtils.getInstance().writeString(Constant.BRACELETNAME, bindDeviceView.getDeviceName());
                if (bindDeviceView.getDeviceName() != null && bindDeviceView.getDeviceName().contains(Constant.UNIBOX)) {
                    bindDeviceView.searchBluetoothText(R.string.finded_unibox);

                } else if (bindDeviceView.getDeviceName().contains(Constant.UNITOYS)) {
                    bindDeviceView.searchBluetoothText(R.string.finded_unitoy);
                }
                Log.i("test", "保存设备名成功");
                if (bindDeviceView.getDeviceName().contains(Constant.UNITOYS)) {
                    bindDeviceView.connect(deviceAddress);
                } else {
                    bindDeviceView.setFindedImageView(View.GONE);
                    bindDeviceView.tipSearchText(R.string.can_use);

                    bindDeviceView.SetUniImageViewBackground(R.drawable.bind_finish);
                    //更新时间操作
                    sendMessageToBlueTooth(getBLETime());
                    //获取基本信息
                    sendMessageToBlueTooth(BASIC_MESSAGE);
                    CommonTools.delayTime(200);
                    sendMessageToBlueTooth(ICCID_GET);
                }

                requestUpdateDeviceInfo();

            } else {
               if(object.getStatus()==1301)
                   return;

                bindDeviceView.showToast(object.getMsg());
            }
//			finish();
        } else if (cmdType == HttpConfigUrl.COMTYPE_UPDATE_CONN_INFO) {
            if (object.getStatus() == 1) {
                bindDeviceView.finishView();
            }
            //批量获取绑定状态
        }else if(cmdType==HttpConfigUrl.COMTYPE_GET_BINDS_IMEI){
                if(object.getStatus()==1){
                    if(object instanceof GetBindsIMEIHttp ) {
                        Log.d("BindDevicePresenterImpl", "rightLoad: "+ object.getData());
                        Log.d("BindDevicePresenterImpl", "deviceList"+ deviceList.size());
                        GetBindsIMEIHttp http = (GetBindsIMEIHttp) object;
                       // bindDeviceView.showBindState(http);
                        bindDeviceView.showDeviceView(deviceList,http);

                    }
                }
        }
    }

    private boolean isStartFindDeviceDelay;
    public void clearListData(){
        if(deviceList!=null) {
            deviceList.clear();
        }
    }
    //把搜索到的蓝牙设备保存到list中然后进行排序
    public void findDevices(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device.getName() == null) {
            return;
        }

        if (deviceList == null) {
            deviceList = new ArrayList<>();
            addressList=new ArrayList<>();
        }

        if (findDeviceHandler == null) {
            findDeviceHandler = new Handler();
        }
        Log.i("BindDevicePresenterImpl", "find the device:" + device.getName() + ",rssi :" + rssi);
        if (device.getName().contains(bindDeviceView.getDeviceName())) {
            //过滤只需要的设备
            BluetoothEntity model = new BluetoothEntity();
            model.setAddress(device.getAddress());
            model.setDiviceName(device.getName());
            model.setRssi(rssi);
            deviceList.add(model);

            rmoveReplicatedData(deviceList);
            if (!isStartFindDeviceDelay) {
                findDeviceHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isStartFindDeviceDelay){
                            return;
                        }
                        //排序后连接操作
                        bindDeviceView.scanLeDevice(false);
                        if (deviceList.size() == 0 && !isStartFindDeviceDelay) {
                            bindDeviceView.showToast(R.string.no_device_around);
                            bindDeviceView.finishView();
                            return;
                        }
                        Collections.sort(deviceList, new Comparator<BluetoothEntity>() {
                            @Override
                            public int compare(BluetoothEntity lhs, BluetoothEntity rhs) {
                                return rhs.getRssi() - lhs.getRssi();
                            }
                        });
                        for (int i = 0; i < deviceList.size(); i++) {
                            String id = deviceList.get(i).toString();
                            Log.i("BindDevicePresenterImpl", "排序后：" + id);
                        }
                        Log.d("BindDevicePresenterImpl", "run: 排序完毕"+isStartFindDeviceDelay);
                        try {
                            if(!isStartFindDeviceDelay&&deviceList.size()>0) {
                                getDevicesBindState(deviceList);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isStartFindDeviceDelay = true;
                    }
                }, 5000);
                isStartFindDeviceDelay = false;
            }
        }
    }
    ///SMS/Gets
    private void getDevicesBindState(List<BluetoothEntity> deviceLists){

        for (int i=0;i<deviceList.size();i++){
            addressList.add(i,deviceLists.get(i).getAddress());
        }
        Log.d(TAG, "getDevicesBindState:address= "+addressList.size()+"--deviceList="+deviceList.size());
       requestBindDeviceList(addressList);

    }

    String deviceAddress;
   @Subscribe(threadMode = ThreadMode.MAIN)//
    public  void getAddress(BluetoothEntity bluetoothEntity) {
       deviceAddress=bluetoothEntity.getAddress();
    }

    public void scanNotFindDevice() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(showdialogRun, SCAN_PERIOD);
    }

    Runnable showdialogRun = new Runnable() {
        @Override
        public void run() {
            if (ICSOpenVPNApplication.uartService != null && ICSOpenVPNApplication.uartService.mConnectionState != UartService.STATE_CONNECTED && !isStartFindDeviceDelay) {
                bindDeviceView.scanLeDevice(false);
                if (TextUtils.isEmpty(deviceAddress))
                    bindDeviceView.showNotSearchDeviceDialog();
            }
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)//ui线程
    public void onVersionEntity(BluetoothMessageCallBackEntity entity) {
        String type = entity.getBlueType();
        if (BluetoothConstant.BLUE_BIND_SUCCESS.equals(type)) {
            EventBusUtil.bindStatue(BindStatue.BIND_DEVICE);
            Log.i("BindDevicePresenterImpl", "蓝牙注册返回:" + entity.getBlueType() + ",参数：MEI：" + deviceAddress + ",版本号：" + SharedUtils.getInstance().readString(Constant.BRACELETVERSION));
            if (bindDeviceView.getDeviceName().contains(Constant.UNIBOX)) {
                requestBindDevice("1");
            }
        } else if (BluetoothConstant.BLUE_BIND.equals(type)) {
            bindDeviceView.afterConnDevice();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueReturnData(BlueReturnData blueReturnData){
        e("blueReturnData:" + blueReturnData );
        switch (blueReturnData.getDataType()){
            case Constant.SYSTEM_BASICE_INFO:
                requestSkyUpgrade();

                break;
        }
    }

    /**
     * 取出相同的address
     * @param list  保存数据的集合
     */
  public void rmoveReplicatedData(List<BluetoothEntity> list){

      for (int i = 0; i < list.size(); i++)  //外循环是循环的次数
      {
          for (int j = list.size() - 1 ; j > i; j--)  //内循环是 外循环一次比较的次数
          {
              if (list.get(i).getAddress().equals(list.get(j).getAddress()))
              {
                  list.remove(j);
              }
          }
      }
  }
    @Override
    public void onDestory() {
        if (bindDeviceModel != null) {
            bindDeviceModel = null;
        }
        if (isBindDeviceModel != null) {
            isBindDeviceModel = null;
        }
        if (updateDeviceInfoModel != null) {
            updateDeviceInfoModel = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(showdialogRun);
            mHandler = null;
        }
        if (findDeviceHandler != null) {
            findDeviceHandler = null;
        }
        EventBus.getDefault().unregister(this);
    }
}
