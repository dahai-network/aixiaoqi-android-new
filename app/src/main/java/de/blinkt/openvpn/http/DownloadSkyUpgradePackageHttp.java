package de.blinkt.openvpn.http;

/**
 * Created by Administrator on 2016/10/20 0020.
 */
public class DownloadSkyUpgradePackageHttp extends BaseHttp{

   private  String downloadPath;

    public String getDownloadStatues() {
        return downloadStatues;
    }

    private String downloadStatues;
    public DownloadSkyUpgradePackageHttp(InterfaceCallback interfaceCallback, int cmdType_, boolean isDownload,String... downloadPath ){
       super(interfaceCallback,cmdType_);
        isCreateHashMap=false;
        this.isDownload=isDownload;
        this.downloadPath=downloadPath[0];

    }
    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        sendMethod_=GET_MODE;
        slaverDomain_=downloadPath;

    }

    @Override
    protected void parseObject(String response) {
        downloadStatues=response;
    }

}
