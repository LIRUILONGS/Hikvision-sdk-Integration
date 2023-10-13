package com.xtj.hikvisionsdkintegration.controller;

import com.alibaba.fastjson2.JSON;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.xtj.hikvisionsdkintegration.dto.GlobalResponseEntity;
import com.xtj.hikvisionsdkintegration.sdklib.HCNetSDK;
import com.xtj.hikvisionsdkintegration.service.SdkInitService;
import com.sun.jna.ptr.IntByReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

import static com.xtj.hikvisionsdkintegration.controller.TestDemo.*;

@Api(tags = "hikvision integration API")
@RestController
@RequestMapping("/config")
@Component
public class ConfigController {

    private static Logger logger = Logger.getLogger("com.xtj.hikvisionsdkintegration.controller.ConfigController");


    @Value("${CAMERA.username}")
    private String username;


    @Value("${CAMERA.password}")
    private String password;


    @Value("${FTP.username}")
    private String ftpUsername;


    @Value("${FTP.password}")
    private String ftpPassword;


    @Value("${FTP.serverIP}")
    private String serverIP;


    @Value("${FTP.serverPort}")
    private short wFTPPort;


    @Value("${FTP.enableAnony}")
    private int byEnableAnony;

    @Value("${FTP.custdir}")
    private String custdir;


    @Value("${snapshot.dwPicInterval}")
    private int dwPicInterval;


    @ApiOperation(value = "单个FTP 开启关闭", notes = "http://127.0.0.1:8099/config/state/192.168.1.143")
    @GetMapping("/state/{m_sDeviceIP}")
    public GlobalResponseEntity<Object> getSdkState(@PathVariable String m_sDeviceIP) {
        //登录
        Integer userId = login(m_sDeviceIP);
        logger.info("IP:"+m_sDeviceIP +" userId=" + userId);
        getCfg(userId);
        getFtpCfg(userId);
        getCapturePlan(userId);
        getSnapshotConfiguration(userId);
        HCNetSDK.NET_DVR_SDKSTATE sdkState = new HCNetSDK.NET_DVR_SDKSTATE();
        //获取当前SDK状态信息
        boolean result = SdkInitService.hCNetSDK.NET_DVR_GetSDKState(sdkState);
        if (result) {
            sdkState.read();
            return GlobalResponseEntity.success(JSON.toJSON(sdkState));
        }
        return null;

    }

    @ApiOperation(value = "多个FTP 开启关闭", notes = "http://127.0.0.1:8099/config/ftp?ips=192.168.1.143,192.168.1.141,192.168.1.142")
    @GetMapping("/ftp")
    public GlobalResponseEntity<String> getFTPConfig(@RequestParam("ips") String[] ips) {
        //登录
        ArrayList<String> objectArrayList = new ArrayList<>();
        for (String m_sDeviceIP : ips) {
            //登录
            Integer userId = login(m_sDeviceIP);
            logger.info("IP:"+m_sDeviceIP +" userId=" + userId);
            if (Objects.nonNull(userId)){
                getCfg(userId);
                getFtpCfg(userId);
                getCapturePlan(userId);
                getSnapshotConfiguration(userId);
                if (SdkInitService.hCNetSDK.NET_DVR_Logout(userId)) {
                    logger.info("注销成功 userId：" + userId);
                }
                //objectArrayList.add( " --配置成功--"+ m_sDeviceIP);

            }else {
                objectArrayList.add(  " @--配置失败--@"+ m_sDeviceIP);
            }

        }
            return GlobalResponseEntity.success(JSON.toJSONString(null));

    }


    @ApiOperation(value = "多个FTP 开启关闭", notes = " [\n" +
            "    \"192.168.1.143\",\n" +
            "    \"192.168.1.141\",\n" +
            "    \"192.168.1.142\"\n" +
            "]")
    @PostMapping("/ftp")
    public GlobalResponseEntity<String> getFTPPostConfig(@RequestBody String[] ips) {
        ArrayList<String> objectArrayList = new ArrayList<>();
        for (String m_sDeviceIP : ips) {
            Integer userId = login(m_sDeviceIP);
            logger.info("IP:"+m_sDeviceIP +" userId=" + userId);
            if (Objects.nonNull(userId)){
                getCfg(userId);
                getFtpCfg(userId);
                getCapturePlan(userId);
                getSnapshotConfiguration(userId);
                if (SdkInitService.hCNetSDK.NET_DVR_Logout(userId)) {
                    logger.info("注销成功 userId：" + userId);
                }
                //objectArrayList.add( " --配置成功--"+ m_sDeviceIP);

            }else {
                objectArrayList.add(  " @--配置失败--@"+ m_sDeviceIP);
            }

        }
            return GlobalResponseEntity.success(JSON.toJSONString(objectArrayList));

    }


    @ApiOperation(value = "多个FTP 开启", notes = " [\n" +
            "    \"192.168.1.143\",\n" +
            "    \"192.168.1.141\",\n" +
            "    \"192.168.1.142\"\n" +
            "]")
    @PostMapping("/ftpopen")
    public GlobalResponseEntity<String> getFTPPostConfigOPen(@RequestBody String[] ips) {
        ArrayList<String> objectArrayList = new ArrayList<>();
        for (String m_sDeviceIP : ips) {
            Integer userId = login(m_sDeviceIP);
            logger.info("IP:"+m_sDeviceIP +" userId=" + userId);
            if (Objects.nonNull(userId)){
                getCfg(userId);
                getFtpCfgAll(userId, (byte) 1);
                getCapturePlan(userId);
                getSnapshotConfiguration(userId);
                if (SdkInitService.hCNetSDK.NET_DVR_Logout(userId)) {
                    logger.info("注销成功 userId：" + userId);
                }
                //objectArrayList.add( " --配置成功--"+ m_sDeviceIP);

            }else {
                objectArrayList.add(  " @--配置失败--@"+ m_sDeviceIP);
            }

        }
        return GlobalResponseEntity.success(JSON.toJSONString(objectArrayList));

    }

    @ApiOperation(value = "多个FTP 关闭", notes = " [\n" +
            "    \"192.168.1.143\",\n" +
            "    \"192.168.1.141\",\n" +
            "    \"192.168.1.142\"\n" +
            "]")
    @PostMapping("/ftpclose")
    public GlobalResponseEntity<String> getFTPPostConfigClose(@RequestBody String[] ips) {
        ArrayList<String> objectArrayList = new ArrayList<>();
        for (String m_sDeviceIP : ips) {
            Integer userId = login(m_sDeviceIP);
            logger.info("IP:"+m_sDeviceIP +" userId=" + userId);
            if (Objects.nonNull(userId)){
                getCfg(userId);
                getFtpCfgAll(userId, (byte) 0);
                getCapturePlan(userId);
                getSnapshotConfiguration(userId);
                if (SdkInitService.hCNetSDK.NET_DVR_Logout(userId)) {
                    logger.info("注销成功 userId：" + userId);
                }
                //objectArrayList.add( " --配置成功--"+ m_sDeviceIP);

            }else {
                objectArrayList.add(  " @--配置失败--@"+ m_sDeviceIP);
            }

        }
        return GlobalResponseEntity.success(JSON.toJSONString(objectArrayList));

    }


    @ApiOperation(value = "多个FTP 状态查看", notes = "http://127.0.0.1:8099/ftp/status?ips=192.168.1.143,192.168.1.141,192.168.1.142")
    @GetMapping("/ftp/status")
    public GlobalResponseEntity<Object> getFTPStatus(@RequestParam("ips") String[] ips) {
        HashMap<String, Integer> hashMap = new HashMap<>(ips.length);
        for (String m_sDeviceIP : ips) {
            //登录
            Integer userId = login(m_sDeviceIP);
            logger.info("IP:"+m_sDeviceIP +" userId=" + userId);
            if (Objects.nonNull(userId)){
                getCfg(userId);
                hashMap.put(m_sDeviceIP, getFtpStatus(userId));
                if (SdkInitService.hCNetSDK.NET_DVR_Logout(userId)) {
                    logger.info("注销成功 userId：" + userId);
                }
            }else {
                hashMap.put(m_sDeviceIP, -1);
            }
        }
        return GlobalResponseEntity.success(JSON.toJSON(hashMap));

    }


    @ApiOperation(value = "多个FTP 状态查看", notes =  " [\n" +
            "    \"192.168.1.143\",\n" +
            "    \"192.168.1.141\",\n" +
            "    \"192.168.1.142\"\n" +
            "]")
    @PostMapping("/ftp/status")
    public GlobalResponseEntity<Object> getFTPPostStatus(@RequestBody String[] ips) {

        HashMap<String, Integer> hashMap = new HashMap<>(ips.length);
        for (String m_sDeviceIP : ips) {
            //登录
            Integer userId = login(m_sDeviceIP);
            logger.info("IP:"+m_sDeviceIP +" userId=" + userId);
            if (Objects.nonNull(userId)){
                getCfg(userId);
                hashMap.put(m_sDeviceIP, getFtpStatus(userId));
                if (SdkInitService.hCNetSDK.NET_DVR_Logout(userId)) {
                    logger.info("注销成功 userId：" + userId);
                }
            }else {
                hashMap.put(m_sDeviceIP, -1);
            }

        }
        return GlobalResponseEntity.success(JSON.toJSON(hashMap));

    }


    public boolean getFtpCfgAll(int lUserID,byte byEnableFTP) {
        HCNetSDK.NET_DVR_FTP_TYPE m_struFtpCond = new HCNetSDK.NET_DVR_FTP_TYPE();
        m_struFtpCond.byType = 0;
        m_struFtpCond.write();

        HCNetSDK.NET_DVR_FTPCFG_V40 m_struFTPCfg = new HCNetSDK.NET_DVR_FTPCFG_V40();
        m_struFTPCfg.write();

        IntByReference dwStatus = new IntByReference(0);
        Pointer lpStatus = dwStatus.getPointer();

        if (false == SdkInitService.hCNetSDK.NET_DVR_GetDeviceConfig(lUserID, HCNetSDK.NET_DVR_GET_FTPCFG_V40, 1,
                m_struFtpCond.getPointer(), m_struFtpCond.size(), lpStatus, m_struFTPCfg.getPointer(),
                m_struFTPCfg.size())) {
            logger.severe("NET_DVR_GET_FTPCFG_V40 failed, error code:" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
            return Boolean.FALSE;
        } else {
            m_struFTPCfg.read();

            if (m_struFTPCfg.byAddresType == 0) {
                m_struFTPCfg.unionServer.setType(HCNetSDK.STRUCT_SELF_IP.class);
                m_struFTPCfg.unionServer.read();
                logger.info("NET_DVR_GET_FTPCFG_V40 succ, FTP服务器地址:" +
                        new String(m_struFTPCfg.unionServer.struAddrIP.struIp.sIpV4).trim() +
                        ", FTP服务器端口:" + m_struFTPCfg.wFTPPort + "摄像头FTP 状态：" + m_struFTPCfg.byEnableFTP);
            }

            m_struFTPCfg.byEnableFTP = byEnableFTP ; //是否启动ftp上传功能：0- 否，1- 是
            m_struFTPCfg.byPicArchivingInterval = 2;
            m_struFTPCfg.byAddresType = 0;
            m_struFTPCfg.byEnableAnony = 0;
            m_struFTPCfg.szUserName = ftpUsername.getBytes();
            m_struFTPCfg.szPassWORD = ftpPassword.getBytes();
            m_struFTPCfg.szTopCustomDir = custdir.getBytes();
            m_struFTPCfg.unionServer.setType(HCNetSDK.STRUCT_SELF_IP.class);
            m_struFTPCfg.unionServer.struAddrIP.struIp.sIpV4 = new byte[16];
            System.arraycopy(serverIP.getBytes(), 0, m_struFTPCfg.unionServer.struAddrIP.struIp.sIpV4, 0, serverIP.length());
            m_struFTPCfg.wFTPPort = wFTPPort;
            m_struFTPCfg.write();

            if (false == SdkInitService.hCNetSDK.NET_DVR_SetDeviceConfig(lUserID, HCNetSDK.NET_DVR_SET_FTPCFG_V40, 1,
                    m_struFtpCond.getPointer(), m_struFtpCond.size(), lpStatus, m_struFTPCfg.getPointer(),
                    m_struFTPCfg.size())) {
                logger.severe("FTP 配置失败, error code:" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
                return Boolean.FALSE;
            } else {
                logger.info("FTP 配置成功!");
                logger.info("是否启动ftp上传功能：0- 否，1- 是   " + m_struFTPCfg.byEnableFTP);
            }

        }
        return Boolean.TRUE;
    }



    public boolean getFtpCfg(int lUserID) {
        HCNetSDK.NET_DVR_FTP_TYPE m_struFtpCond = new HCNetSDK.NET_DVR_FTP_TYPE();
        m_struFtpCond.byType = 0;
        m_struFtpCond.write();

        HCNetSDK.NET_DVR_FTPCFG_V40 m_struFTPCfg = new HCNetSDK.NET_DVR_FTPCFG_V40();
        m_struFTPCfg.write();

        IntByReference dwStatus = new IntByReference(0);
        Pointer lpStatus = dwStatus.getPointer();

        if (false == SdkInitService.hCNetSDK.NET_DVR_GetDeviceConfig(lUserID, HCNetSDK.NET_DVR_GET_FTPCFG_V40, 1,
                m_struFtpCond.getPointer(), m_struFtpCond.size(), lpStatus, m_struFTPCfg.getPointer(),
                m_struFTPCfg.size())) {
            logger.severe("NET_DVR_GET_FTPCFG_V40 failed, error code:" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
            return Boolean.FALSE;
        } else {
            m_struFTPCfg.read();

            if (m_struFTPCfg.byAddresType == 0) {
                m_struFTPCfg.unionServer.setType(HCNetSDK.STRUCT_SELF_IP.class);
                m_struFTPCfg.unionServer.read();
                logger.info("NET_DVR_GET_FTPCFG_V40 succ, FTP服务器地址:" +
                        new String(m_struFTPCfg.unionServer.struAddrIP.struIp.sIpV4).trim() +
                        ", FTP服务器端口:" + m_struFTPCfg.wFTPPort + "摄像头FTP 状态：" + m_struFTPCfg.byEnableFTP);
            }

            if (m_struFTPCfg.byEnableFTP == 1) {
                m_struFTPCfg.byEnableFTP = 0; //是否启动ftp上传功能：0- 否，1- 是
            } else {
                m_struFTPCfg.byEnableFTP = 1; //是否启动ftp上传功能：0- 否，1- 是
            }
            m_struFTPCfg.byPicArchivingInterval = 2;
            m_struFTPCfg.byAddresType = 0;
            m_struFTPCfg.byEnableAnony = 0;
            m_struFTPCfg.szUserName = ftpUsername.getBytes();
            m_struFTPCfg.szPassWORD = ftpPassword.getBytes();
            m_struFTPCfg.szTopCustomDir = custdir.getBytes();
            m_struFTPCfg.unionServer.setType(HCNetSDK.STRUCT_SELF_IP.class);
            m_struFTPCfg.unionServer.struAddrIP.struIp.sIpV4 = new byte[16];
            System.arraycopy(serverIP.getBytes(), 0, m_struFTPCfg.unionServer.struAddrIP.struIp.sIpV4, 0, serverIP.length());
            m_struFTPCfg.wFTPPort = wFTPPort;
            m_struFTPCfg.write();

            if (false == SdkInitService.hCNetSDK.NET_DVR_SetDeviceConfig(lUserID, HCNetSDK.NET_DVR_SET_FTPCFG_V40, 1,
                    m_struFtpCond.getPointer(), m_struFtpCond.size(), lpStatus, m_struFTPCfg.getPointer(),
                    m_struFTPCfg.size())) {
                logger.severe("FTP 配置失败, error code:" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
                return Boolean.FALSE;
            } else {
                logger.info("FTP 配置成功!");
                logger.info("是否启动ftp上传功能：0- 否，1- 是   " + m_struFTPCfg.byEnableFTP);
            }

        }
        return Boolean.TRUE;
    }


    public int getFtpStatus(int lUserID) {
        HCNetSDK.NET_DVR_FTP_TYPE m_struFtpCond = new HCNetSDK.NET_DVR_FTP_TYPE();
        m_struFtpCond.byType = 0;
        m_struFtpCond.write();

        HCNetSDK.NET_DVR_FTPCFG_V40 m_struFTPCfg = new HCNetSDK.NET_DVR_FTPCFG_V40();
        m_struFTPCfg.write();

        IntByReference dwStatus = new IntByReference(0);
        Pointer lpStatus = dwStatus.getPointer();

        if (false == SdkInitService.hCNetSDK.NET_DVR_GetDeviceConfig(lUserID, HCNetSDK.NET_DVR_GET_FTPCFG_V40, 1,
                m_struFtpCond.getPointer(), m_struFtpCond.size(), lpStatus, m_struFTPCfg.getPointer(),
                m_struFTPCfg.size())) {
            logger.severe("NET_DVR_GET_FTPCFG_V40 failed, error code:" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
            return -1;
        } else {
            m_struFTPCfg.read();
            if (m_struFTPCfg.byAddresType == 0) {
                m_struFTPCfg.unionServer.setType(HCNetSDK.STRUCT_SELF_IP.class);
                m_struFTPCfg.unionServer.read();
                logger.info("NET_DVR_GET_FTPCFG_V40 succ, FTP服务器地址:" +
                        new String(m_struFTPCfg.unionServer.struAddrIP.struIp.sIpV4).trim() +
                        ", FTP服务器端口:" + m_struFTPCfg.wFTPPort + "摄像头FTP 状态：" + m_struFTPCfg.byEnableFTP);
            }
            return m_struFTPCfg.byEnableFTP;
        }
    }

    /**
     * 抓图计划 配置
     *
     * @param lUserID
     */
    public Boolean getCapturePlan(int lUserID) {


        HCNetSDK.NET_DVR_SCHED_CAPTURECFG net_dvr_sched_capturecfg = new HCNetSDK.NET_DVR_SCHED_CAPTURECFG();
        net_dvr_sched_capturecfg.dwSize = net_dvr_sched_capturecfg.size();
        Pointer lpIpParaConfig = net_dvr_sched_capturecfg.getPointer();
        net_dvr_sched_capturecfg.write();
        Pointer p_net_dvr_sched_capturecfg = net_dvr_sched_capturecfg.getPointer();
        NativeLong lChannel = new NativeLong(1);
        IntByReference pInt = new IntByReference(0);

        boolean flag = SdkInitService.hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_SCHED_CAPTURECFG, lChannel.intValue(),
                p_net_dvr_sched_capturecfg, net_dvr_sched_capturecfg.size(), pInt);
        net_dvr_sched_capturecfg.read();
        if (!flag) {
            logger.severe("获取抓图计划配置失败，错误码为" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
            return Boolean.FALSE;
        } else {
            logger.info("获取抓图计划配置成功");
        }

        // 配置每天全天
        for (int i = 0; i < 7; i++) {
            net_dvr_sched_capturecfg.struCaptureDay[i].byCaptureType = 0;
            net_dvr_sched_capturecfg.struCaptureDay[i].byAllDayCapture = 1;
            net_dvr_sched_capturecfg.tempStructure[i].struCaptureSched[0].byCaptureType = 0;
            net_dvr_sched_capturecfg.tempStructure[i].struCaptureSched[0].struCaptureTime.byStartHour = 0;
            net_dvr_sched_capturecfg.tempStructure[i].struCaptureSched[0].struCaptureTime.byStartMin = 0;
            net_dvr_sched_capturecfg.tempStructure[i].struCaptureSched[0].struCaptureTime.byStopHour = 24;
            net_dvr_sched_capturecfg.tempStructure[i].struCaptureSched[0].struCaptureTime.byStopMin = 0;
        }
        net_dvr_sched_capturecfg.byEnable = 1;
        net_dvr_sched_capturecfg.write();
        flag = SdkInitService.hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_SCHED_CAPTURECFG, 1,
                lpIpParaConfig, net_dvr_sched_capturecfg.size());
        if (flag == false) {
            logger.severe("设置抓图计划失败，错误码为" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
            return Boolean.FALSE;
        } else {
            logger.info("设置抓图计划成功");

            for (int i = 0; i < 7; i++) {
                HCNetSDK.NET_DVR_SCHEDTIME struRecordTime = net_dvr_sched_capturecfg.tempStructure[i].struCaptureSched[0].struCaptureTime;

                logger.info("抓图计划：周 " + (i + 1) + " ,时间段" + struRecordTime.byStartHour + ":" + struRecordTime.byStartMin + "-" + struRecordTime.byStopHour + ":" + struRecordTime.byStopMin);

            }

        }
        return Boolean.TRUE;
    }


    /**
     * 抓图配置
     *
     * @param lUserID
     */
    public Boolean getSnapshotConfiguration(int lUserID) {


        HCNetSDK.NET_DVR_JPEG_CAPTURE_CFG net_dvr_jpeg_capture_cfg = new HCNetSDK.NET_DVR_JPEG_CAPTURE_CFG();
        net_dvr_jpeg_capture_cfg.dwSize = net_dvr_jpeg_capture_cfg.size();
        Pointer lpIpParaConfig = net_dvr_jpeg_capture_cfg.getPointer();
        net_dvr_jpeg_capture_cfg.write();
        Pointer p_net_dvr_sched_capturecfg = net_dvr_jpeg_capture_cfg.getPointer();
        NativeLong lChannel = new NativeLong(1);
        IntByReference pInt = new IntByReference(0);

        boolean flag = SdkInitService.hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_JPEG_CAPTURE_CFG, lChannel.intValue(),
                p_net_dvr_sched_capturecfg, net_dvr_jpeg_capture_cfg.size(), pInt);
        net_dvr_jpeg_capture_cfg.read();
        if (!flag) {
            logger.severe("获取抓图配置失败，错误码为" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
            return Boolean.FALSE;
        } else {
            logger.info("获取抓图配置成功");
        }
        HCNetSDK.NET_DVR_TIMING_CAPTURE net_dvr_timing_capture = new HCNetSDK.NET_DVR_TIMING_CAPTURE();
        net_dvr_timing_capture.dwPicInterval = dwPicInterval;

        net_dvr_jpeg_capture_cfg.struTimingCapture = net_dvr_timing_capture;


        net_dvr_jpeg_capture_cfg.write();
        flag = SdkInitService.hCNetSDK.NET_DVR_SetDVRConfig(lUserID, HCNetSDK.NET_DVR_SET_JPEG_CAPTURE_CFG, 1,
                lpIpParaConfig, net_dvr_jpeg_capture_cfg.size());
        if (flag == false) {
            logger.severe("设置抓图配置失败，错误码为" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
            return Boolean.FALSE;
        } else {
            logger.info("设置抓图配置成功");
        }
        return Boolean.TRUE;
    }


    private Integer login(String m_sDeviceIP) {
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
        HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息

        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(username.getBytes(), 0, m_strLoginInfo.sUserName, 0, username.length());

        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(password.getBytes(), 0, m_strLoginInfo.sPassword, 0, password.length());

        m_strLoginInfo.wPort = Short.valueOf("8000");
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        m_strLoginInfo.write();


        int loginHandler = SdkInitService.hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);

        if (loginHandler == -1) {
            int errorCode = SdkInitService.hCNetSDK.NET_DVR_GetLastError();
            IntByReference errorInt = new IntByReference(errorCode);
            logger.severe(String.format("[HK] login fail errorCode:%s, errMsg:%s", errorCode, SdkInitService.hCNetSDK.NET_DVR_GetErrorMsg(errorInt)));
            return null;
        } else {
            return loginHandler;
        }
    }


    //获取设备的基本参数
    public void getCfg(int iUserID) {
        HCNetSDK.NET_DVR_DEVICECFG_V40 m_strDeviceCfg = new HCNetSDK.NET_DVR_DEVICECFG_V40();
        m_strDeviceCfg.dwSize = m_strDeviceCfg.size();
        m_strDeviceCfg.write();
        Pointer pStrDeviceCfg = m_strDeviceCfg.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean b_GetCfg = SdkInitService.hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_DEVICECFG_V40,
                0Xffffffff, pStrDeviceCfg, m_strDeviceCfg.dwSize, pInt);
        if (b_GetCfg == false) {
            logger.severe("获取参数失败  错误码：" + SdkInitService.hCNetSDK.NET_DVR_GetLastError());
        } else {
            logger.info("获取参数成功");
            m_strDeviceCfg.read();
            logger.info("===========================  设备信息  ============================");
            logger.info("设备名称:" + new String(m_strDeviceCfg.sDVRName).trim() + " 设备序列号：" + new String(m_strDeviceCfg.sSerialNumber));
            logger.info("设备型号名称:" + new String(m_strDeviceCfg.byDevTypeName).trim() + " 设备型号：" + m_strDeviceCfg.wDevType);
            logger.info("模拟通道个数：" + m_strDeviceCfg.byChanNum);
            logger.info("软件版本号：" + parseVersion(m_strDeviceCfg.dwSoftwareVersion));
            logger.info("软件生成日期：" + parseBuildTime(m_strDeviceCfg.dwSoftwareBuildDate));
            logger.info("DSP软件生成日期:" + parseDSPBuildDate(m_strDeviceCfg.dwDSPSoftwareBuildDate));
            logger.info("===========================  设备信息  ============================");


        }
        ;
    }

}
