package com.alibaba.jvm.sandbox.repeater.plugin.core.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author peng.hu1
 * @Date 2022/12/9 10:35
 */
public class IpUtil {

    public static String getLocalIp() {
        InetAddress inetAddress = null;
        boolean isFind = false; // 返回标识
        Enumeration<NetworkInterface> networkInterfaceLists = null;
        try {
            // 获取网络接口
            networkInterfaceLists = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (networkInterfaceLists.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface) networkInterfaceLists.nextElement();
            Enumeration<InetAddress> ips = networkInterface.getInetAddresses();
            // 遍历所有ip，获取本地地址中不是回环地址的ipv4地址
            while (ips.hasMoreElements()) {
                inetAddress = (InetAddress) ips.nextElement();
                if (inetAddress instanceof Inet4Address && inetAddress.isSiteLocalAddress()
                        && !inetAddress.isLoopbackAddress()) {
                    isFind = true;
                    break;
                }
            }
            if (isFind) {
                break;
            }
        }
        return inetAddress == null ? "" : inetAddress.getHostAddress();
    }
}
