



网络摄像机(IPC系列设备)功能接口介绍
















添加的宏定义

```java
    /*   */
    public static final int NET_DVR_GET_FTPCFG_V40 = 6162;  //获取FTP信息
    public static final int NET_DVR_SET_FTPCFG_V40 = 6163;  //设置FTP信息
    public static final int NET_DVR_SCHED_CAPTURECFG = 1283;  //设置抓图计划
    public static final int NET_DVR_SET_JPEG_CAPTURE_CFG = 1281 ; //设置设备抓图配置
    
    public static final int NET_DVR_GET_SCHED_CAPTURECFG = 1282;  //获取抓图计划
    public static final int  NET_DVR_GET_JPEG_CAPTURE_CFG = 1280;   //获取设备抓图配置

```


添加的结构体

```java
 //图片命名扩展 2013-09-27
    public static class NET_DVR_PICTURE_NAME_EX extends Structure {
        public byte[] byItemOrder = new byte[PICNAME_MAXITEM];    /*    桉数组定义文件命名的规则 */
        public byte byDelimiter;                    /*分隔符，一般为'_'*/
        public byte[] byPicNamePrefix = new byte[PICNAME_PREFIX/*32*/];  //图片名自定义前缀
    }

    public static class STRUCT_SELF_DOMAIN extends Structure {
        public byte[] szDomain = new byte[64];        //服务器地址，可以使IPv4 IPv6或是域名
        public byte[] byRes1 = new byte[80];
    }

    public static class STRUCT_SELF_IP extends Structure {
        public NET_DVR_IPADDR struIp = new NET_DVR_IPADDR(); /*IP地址:IPv4 IPv6地址, 144字节*/
    }


    public static class UNION_SELF_FTPSEVER extends Union {
        public STRUCT_SELF_DOMAIN struDomain = new STRUCT_SELF_DOMAIN();
        public STRUCT_SELF_IP struAddrIP = new STRUCT_SELF_IP();
    }

    public static class NET_DVR_FTPCFG_V40 extends Structure {
        public NET_DVR_STRUCTHEAD struStruceHead = new NET_DVR_STRUCTHEAD();
        public byte byEnableFTP;            /*是否启动ftp上传功能，0-否，1-是*/
        public byte byProtocolType;             /*协议类型 0-FTP，1-SFTP*/
        public short wFTPPort;                /*端口*/
        public UNION_SELF_FTPSEVER unionServer = new UNION_SELF_FTPSEVER();
        public byte[] szUserName = new byte[32];            /*用户名*/
        public byte[] szPassWORD = new byte[16];            /*密码*/
        public byte[] szTopCustomDir = new byte[64];        /*自定义一级目录*/
        public byte[] szSubCustomDir = new byte[64];        /*自定义二级目录*/
        public byte byDirLevel;                /*0 = 不使用目录结构，直接保存在根目录,    1 = 使用1级目录,2=使用2级目录*/
        public byte byTopDirMode;            /* 一级目录，0x1 = 使用设备名,0x2 = 使用设备号,0x3 = 使用设备ip地址，
                                                    0x4=使用监测点,0x5=使用时间(年月),0x6-使用自定义 ,0x7=违规类型,0x8=方向,0x9=地点*/
        public byte bySubDirMode;           /*二级目录，0x1=使用通道名,0x2=使用通道号 0x3=使用时间(年月日),
                                                    0x4=使用车道号,0x5-使用自定义, 0x6=违规类型,0x7=方向,0x8=地点,0x9 = 车位编号*/
        public byte byType;                    /* 0-主服务器，1-备服务器*/
        public byte byEnableAnony;          /*启用匿名 0-否 1是*/
        public byte byAddresType;           /*0 使用IPV4、IPV6  1- 使用域名*/
        public byte byFTPPicType;           //0-保留，1-停车场抓拍图片命名规则
        public byte byPicArchivingInterval; //图片归档间隔[1~30],0表示关闭
        public NET_DVR_PICTURE_NAME_EX struPicNameRule = new NET_DVR_PICTURE_NAME_EX();   /* 图片命名规则:
        byFTPPicType ==1的时候,自数组内的命名规则是停车场抓拍图片命名规则*/
        public byte byPicNameRuleType;      //图片命令规则类型；0~默认类型，1~图片前缀名定义(启用struPicNameRule中的byPicNamePrefix字段)
        public byte[] byRes = new byte[203];             /*保留*/
    }

    public static class NET_DVR_FTP_TYPE extends Structure {
        public byte byType;  // 0-主服务器，1-备服务器
        public byte[] byRes = new byte[3];
    }

```

