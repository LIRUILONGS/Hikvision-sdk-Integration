
## 海康设备 通过 SDK 查看修改网络摄像头配置

需要通过程序远程修改 海康网络摄像头配置，可以在指定的时间间隔的情况下抓图，通过 FTP 传到指定服务器，需要修改的配置项：


+ FTP 配置: `NET_DVR_SET_FTPCFG_V40`,`NET_DVR_GET_FTPCFG_V40`
+ 抓图配置: `NET_DVR_GET_JPEG_CAPTURE_CFG`,`NET_DVR_SET_JPEG_CAPTURE_CFG`
+ 抓图计划配置: `NET_DVR_GET_SCHED_CAPTURECFG`,`NET_DVR_SET_SCHED_CAPTURECFG`

项目目录

```bash
X:.
├─main
│  ├─java
│  │  └─com
│  │      └─xtj
│  │          └─hikvisionsdkintegration
│  │              ├─config
│  │              ├─controller
│  │              ├─dto
│  │              ├─sdklib
│  │              ├─service
│  │              ├─task
│  │              └─util
│  └─resources

```

调用方式通过接口调用,支持 `Get,Post` 方式


get 方式
```bash
http://127.0.0.1:8099/config/state/192.168.1.143
http://127.0.0.1:8099/config/ftp?ips=192.168.1.143,192.168.1.141,192.168.1.142
http://127.0.0.1:8099/config/ftp/status?ips=192.168.1.143,192.168.1.141,192.168.1.142
```
post 方式
```bash
curl --location --request POST 'http://127.0.0.1:8099/config/ftp' \
--header 'Content-Type: application/json' \
--data-raw ' [
    "192.168.1.143",
    "192.168.1.141",
    "192.168.1.142"
]'
```

```bash
curl --location --request POST 'http://127.0.0.1:8099/config/ftp/status' \
--header 'Content-Type: application/json' \
--data-raw ' [
    "192.168.1.143",
    "192.168.1.141",
    "192.168.1.142"
]

'
```

提供了 swagger UI ,接口文档,可以直接调用

[](./file/20231012024029.png)


### 配置说明

```yaml
server:
  port: 8099


logging:
  level:
    org:
      springframework.web: info


# 摄像头配置
CAMERA:
  username: "admin"
  password: "hik12345"



# ftp 配置
FTP:
  username: "face"
  password: "face"
  serverIP: "110.110.110.170"
  serverPort: 21
  enableAnony: 0
  custdir: "face"


#抓图配置
snapshot:
  dwPicInterval: 4000 # 抓图时间间隔 4s

```



### 部署

当前 SDK 没办法打包进去，时间关系没有在看，没有测试 Linux 环境， window 下需要把SDK 包和 当前项目 jar 包放到同一级目录
```bash
java -jar  hikvision-sdk-integration-0.0.1-SNAPSHOT.jar
```

### 海康 SDK 使用

开发手册，动态库下载：


[https://open.hikvision.com/download/5cda567cf47ae80dd41a54b3?type=10&id=5cda5902f47ae80dd41a54b7](https://open.hikvision.com/download/5cda567cf47ae80dd41a54b3?type=10&id=5cda5902f47ae80dd41a54b7)

+ 确定需要调用的动态库功能
+ 通过`开发手册`查看对应的宏定义,结构体
+ 定义宏变量，结构体转化
+ 编写需要的功能业务,可以参考官方的 Demo 


以 FTP 配置为 Demo：

宏变量

```java
    public static final int NET_DVR_GET_FTPCFG_V40 = 6162;  //获取FTP信息
    public static final int NET_DVR_SET_FTPCFG_V40 = 6163;  //设置FTP信息
```

结构体转化

```java

    public static class NET_DVR_STRUCTHEAD extends Structure {
        public short wLength;        //结构长度
        public byte byVersion ;    /*高低4位分别代表高低版本，后续根据版本和长度进行扩展，不同的版本的长度进行限制*/
        public byte byRes;
    }

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

编写功能业务










