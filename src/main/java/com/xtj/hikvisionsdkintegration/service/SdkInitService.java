package com.xtj.hikvisionsdkintegration.service;

import com.xtj.hikvisionsdkintegration.sdklib.HCNetSDK;
import com.xtj.hikvisionsdkintegration.task.InitSdkTask;
import com.xtj.hikvisionsdkintegration.util.OSUtils;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;


@Component
public class SdkInitService {

    private static Logger logger = Logger.getLogger("com.xtj.hikvisionsdkintegration.service.SdkInitService");

    public static HCNetSDK hCNetSDK = null;

    static FExceptionCallBack_Imp fExceptionCallBack;

    static class FExceptionCallBack_Imp implements HCNetSDK.FExceptionCallBack {
        public void invoke(int dwType, int lUserID, int lHandle, Pointer pUser) {
            logger.severe("异常事件类型:" + dwType);
            return;
        }
    }

    public SdkInitService() {
        if (hCNetSDK == null) {
            synchronized (HCNetSDK.class) {
                try {
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(OSUtils.getLoadLibrary(), HCNetSDK.class);
                } catch (Exception ex) {
                    logger.severe("SdkInitService-init-hCNetSDK-error");
                }
            }
        }
    }

    @Autowired
    private ThreadPoolExecutor executor;

    public void initSdk() {
        logger.info("HKSDKInitService-init-coming");
        executor.execute(new InitSdkTask());
    }
}
