package cuit.epoch.pymjl.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/19 0:07
 **/
public class CommonUtil {
    /**
     * 得到本机IP
     *
     * @return {@code String}
     */
    public static String getLocalIp() {
        try {
            InetAddress ip4 = InetAddress.getLocalHost();
            return ip4.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }
}
