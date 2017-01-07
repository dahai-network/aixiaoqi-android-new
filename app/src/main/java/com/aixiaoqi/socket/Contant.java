package com.aixiaoqi.socket;

/**
 * Created by Administrator on 2016/12/17 0017.
 */
public class Contant {

    public static String [] CONNENCT_TAG={"01",//请求GoIp模块
            Integer.toHexString(101),//链接存活时间
            Integer.toHexString(107),//是否请求预读数据
            Integer.toHexString(120)//服务寻址sessionID
            ,Integer.toHexString(121)//数据链接协议
            ,Integer.toHexString(150)//uuwifi设备编号vid
            ,Integer.toHexString(160)//模块位置描述goip
            ,Integer.toHexString(170)//模块IMEI
            , Integer.toHexString(171)//模块类型mod_type
            ,Integer.toHexString(172)//模块版本 mod_ver
            ,Integer.toHexString(180)//sim卡位置描述
            ,Integer.toHexString(190)//SIM卡ICCID
            , Integer.toHexString(191)//SIM卡IMSI
            ,Integer.toHexString(192)//SIM卡卡号，number
            ,Integer.toHexString(193)//SIM卡余额
            ,Integer.toHexString(198)//预读数据压缩前的长度
            ,Integer.toHexString(199)};//预读数据内容
    public static String []CONNENCT_VALUE={"01",//01，请求GoIp模块的值
            "b4"//101,链接存活时间
            ,"01"//107,是否请求预读数据
            ,"3433666133383161303161336364393334633731666463353464636463326637"//120,服务寻址sessionID
            , "01"//121,数据链接协议
            ,"757573696d00"//150,uuwifi设备编号vid
            ,"757573696d2e303100"//160,模块位置描述goip
            ,"00"//170,模块IMEI
            ,"00"//171,模块类型mod_type
            ,"00"//172,模块版本
            ,"00"//180,sim卡位置描述
            ,"383938363031313532383531303131393435303400",//190,SIM卡ICCID
            "34363030313834373832373032303600",//191,SIM卡IMSI
            "00"//192,SIM卡卡号，number
            ,"00"//193,SIM卡余额
            ,"15f1"//198,预读数据压缩前的长度
            , "78dac5975f4853511cc77ff7df9c73ea5dced2925aff40e88f1bbe489a9b3aff4c872e1532a29835ff40c989283004ff1451e8cb464141453e04f562dce6833e46e0530f51d17328d84b2f37087b8a75ee7137dc45cf3dbb5ceac0c6b9f7fb3bfbfc7ebff33b7f06405a00a07c55eb04c1a471f85322cdbf00e0c933ff7c4fcd9ac918017f5a73c6944af36f014ac9d32edc0357cd9a08a21ae6b04512a47905bf226ab1f278543e73bb5c4ef58792502331a0827494582389c0e5a044a2162819dc9230e53381683f5d9b9b0302d94d9e2a718fdb3fe5dbd45c29a870c09dd93bb349403718bcefa27b2fa31b227872bc97895a46bcdfda30708901d84e07bad1927166dc442d550cb0ddf996ce4311bd6670b081ee20875e1b1de4882a2a02f6ca8e8a41261583ea6c5801a84e04efb62b20539bc9042128f81b30aa800115a6a30a518186f26e411512d5ad38230eb9e3de755f1c931aed989b466350fadc68690bda4108d2087e06c22295c01d477ee38a1b27ea849250e5ccff6838b04b16165bdcc298410b637c7694a8cf9874bd443b8b163e5e78f3e3017e8f2619486d7452119ad4485b174311514b94587ff41b9ee2a32f5d1a2b6047b10668c5facf8f87093b8e870923503f1e122a24545f420d25d46b18d6c4000bd161126a1261570e6c737b776ad169efd00203a5914e11d082f1401188eac03d8d31cec0384d67f068dc58099bb69202f89e83ca2c2c3baf8531ef2d8ca9b430a69521653df4947951abb1d23623dea3ecb047b63140a374a807b5196bc14354af02db103bec08b3c3581926617a2ccc47ccc2985e3bb6f5de9db6f5475fdcebf7d7f9248723ea632045e8a412d467249510d5a37c7db63c1693396d67af3b84c52939ffff3c28643246bb20dea6bb5887425917b92acdc534275611f5409a7ef863539ed1149d32f1d389c33b49f7f3153aa5f979681b3f8fb4f7446247741c3fa35af5139b0aeca622bba9c46eea60cd693d43799ea5e7b40ad5674fb23ce71e3530c0bfd3e10a6ad00b4fc9c215a22ea605676a3420c78725352ccd70b20acec1586f6afaeee1510984133c7feb53c63d7475eed7cd7383a57e7ef8e7b9bd634da90f73dfe22b73bffa3e26396881018842b4d70f01f003f91e599f5bf3f323c7af1cd47a939f479e9e1f385b7d71636823e3ee5eef76f5acc651f74acf93be77d3829fcffb267cdac256d6c990c40e7a128b51a79ec4e26c128b892aa7c1e061971db42ebd5ecc6851139af60772994e6b42d1ec196446234b7c674da068224593768cae99219797e9d155a3667de6aab3d15513f558cedafbbdf5812c0b1f48ea5f475aecb804b618cf28fd1258af21c276ec33e12c42c8739f8998c0f12d9f4bd3e12a8ae8655491855710755fdae470633213d8cc44363389cdccc16656c066e664332b6433733198fd01cfbcbaf2"
            //199,预读数据内容
    };
    public static String SESSION_ID="00000000";
    public static String SDK_TAG="c7";
    public static String SDK_VALUE="";

    public static String UPDATE_CONNECTION="108a0500";
    public static String CONNECTION="108a0400";
    public static String PRE_DATA=  "108a9000";
    public static  final String SESSION_ID_TEMP="00000000";
    public static final  String hostIP = "192.168.1.50";
    public static final int port = 20016;
}
