package com.xtj.hikvisionsdkintegration.util;

import com.sun.jna.Platform;

import java.io.File;
import java.util.logging.Logger;


public class OSUtils {
    private static Logger logger = Logger.getLogger("com.xtj.hikvisionsdkintegration.util.OSUtils");

    // 获取操作平台信息
    public static String getOsPrefix() {
        String arch = System.getProperty("os.arch").toLowerCase();
        final String name = System.getProperty("os.name");
        String osPrefix;
        if (Platform.isWindows()) {
            if ("i386".equals(arch)) {
                arch = "x86";
            }
            osPrefix = "win32-" + arch;
        } else if (Platform.isLinux()) {
            if ("x86".equals(arch)) {
                arch = "i386";
            } else if ("x86_64".equals(arch)) {
                arch = "amd64";
            }
            osPrefix = "linux-" + arch;
        } else {
            osPrefix = name.toLowerCase();
            if ("x86".equals(arch)) {
                arch = "i386";
            }
            if ("x86_64".equals(arch)) {
                arch = "amd64";
            }
            int space = osPrefix.indexOf(" ");
            if (space != -1) {
                osPrefix = osPrefix.substring(0, space);
            }
            osPrefix += "-" + arch;
        }
        return osPrefix;
    }

    public static String getOsName() {
        String osName = "";
        String osPrefix = getOsPrefix();
        if (osPrefix.toLowerCase().startsWith("win32-x86")
                || osPrefix.toLowerCase().startsWith("win32-amd64")) {
            osName = "win";
        } else if (osPrefix.toLowerCase().startsWith("linux-i386")
                || osPrefix.toLowerCase().startsWith("linux-amd64")) {
            osName = "linux";
        }

        return osName;
    }

    /**
     * 获取库文件
     * 区分win、linux
     *
     * @return
     */
    public static String getLoadLibrary() {
        if (isChecking()) {
            return null;
        }
        String userDir = System.getProperty("user.dir");
        logger.info("getLoadLibrary-userDir= "+ userDir);
        String loadLibrary = "";
        String library = "";
        String osPrefix = getOsPrefix();
        if (osPrefix.toLowerCase().startsWith("win32-x86")) {
            loadLibrary = System.getProperty("user.dir") + File.separator + "sdk" + File.separator + "hklibwin32" + File.separator;
            library = "HCNetSDK.dll";
        } else if (osPrefix.toLowerCase().startsWith("win32-amd64")) {
            loadLibrary = System.getProperty("user.dir") + File.separator + "sdk" + File.separator + "hklibwin64" + File.separator;
            library = "HCNetSDK.dll";
        } else if (osPrefix.toLowerCase().startsWith("linux-i386")) {
            //同 linux-amd64
            loadLibrary = "";
            library = "libhcnetsdk.so";
        } else if (osPrefix.toLowerCase().startsWith("linux-amd64")) {
            //方式一：使用系统默认的加载库路径，在系统的/usr/lib文件中加入你Java工程所需要使用的so文件，然后将HCNetSDKCom文件夹下的组件库也复制到/usr/lib目录，HCNetSDKCom文件夹中的组件库不要随意更换路径。CentOS 64位需拷贝到/usr/lib64下。
            //针对方式一，前缀就是绝对路径
            //loadLibrary = "/usr/lib64/lib/hkliblinux64/";

            //方式二：配置LD_LIBRARY_PATH环境变量加载库文件；配置/etc/ld.so.conf，加上你自己的Java工程所需要的so文件的路径
            //针对方式二，无需添加前缀，程序会从linux系统的so共享库中查找libhcnetsdk.so
            loadLibrary = "";
            library = "libhcnetsdk.so";
        }

        logger.info("================= Load library Path "+loadLibrary + library+": ==================");
        return loadLibrary + library;
    }

    private static boolean checking = false;

    public static void setChecking() {
        checking = true;
    }

    public static void clearChecking() {
        checking = false;
    }

    public static boolean isChecking() {
        return checking;
    }
}

