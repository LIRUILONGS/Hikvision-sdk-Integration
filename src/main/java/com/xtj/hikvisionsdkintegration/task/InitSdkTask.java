package com.xtj.hikvisionsdkintegration.task;

import com.xtj.hikvisionsdkintegration.sdklib.HCNetSDK;
import com.xtj.hikvisionsdkintegration.service.SdkInitService;
import com.xtj.hikvisionsdkintegration.util.OSUtils;
import com.xtj.hikvisionsdkintegration.util.OsSelect;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;


public class InitSdkTask implements Runnable {

    private static Logger logger = Logger.getLogger("com.xtj.hikvisionsdkintegration.task.InitSdkTask");
    /**
     * 装配 sdk 所需依赖
     */
    private static HCNetSDK hCNetSDK = SdkInitService.hCNetSDK;

    @Override
    public void run() {
        try {
            //linux系统建议调用以下接口加载组件库
            if (OsSelect.isLinux()) {
                logger.info("InitSdk-is-linux");
                String userDir = System.getProperty("user.dir");
                logger.info("InitSdk-userDir"+ userDir);
                HCNetSDK.BYTE_ARRAY ptrByteArray1 = new HCNetSDK.BYTE_ARRAY(256);
                HCNetSDK.BYTE_ARRAY ptrByteArray2 = new HCNetSDK.BYTE_ARRAY(256);
                //这里是库的绝对路径，请根据实际情况修改，注意改路径必须有访问权限
                String strPath1 =  "/usr/lib" + File.separator +"sdk"+ File.separator +"hkliblinux64"+File.separator + "libcrypto.so.1.1";
                String strPath2 =  "/usr/lib" + File.separator +"sdk"+ File.separator +"hkliblinux64"+File.separator  + "libssl.so.1.1";

                System.arraycopy(strPath1.getBytes(), 0, ptrByteArray1.byValue, 0, strPath1.length());
                ptrByteArray1.write();
                hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArray1.getPointer());

                System.arraycopy(strPath2.getBytes(), 0, ptrByteArray2.byValue, 0, strPath2.length());
                ptrByteArray2.write();
                hCNetSDK.NET_DVR_SetSDKInitCfg(4, ptrByteArray2.getPointer());

                String strPathCom = "/usr/lib" + File.separator +"sdk"+ File.separator +"hkliblinux64"+File.separator ;
                HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
                System.arraycopy(strPathCom.getBytes(), 0, struComPath.sPath, 0, strPathCom.length());
                struComPath.write();
                hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
            }
            //初始化sdk
            boolean isOk = hCNetSDK.NET_DVR_Init();
            hCNetSDK.NET_DVR_SetConnectTime(10, 1);
            hCNetSDK.NET_DVR_SetReconnect(100, true);
            if (!isOk) {
                logger.severe("=================== InitSDK init fail ===================");
            } else {
                logger.info("============== InitSDK init success ====================");
            }
        } catch (Exception e) {
            logger.severe("InitSDK-error,e="+ e.getMessage());
            e.printStackTrace();
        }
    }
}
