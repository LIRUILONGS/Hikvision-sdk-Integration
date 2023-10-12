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

