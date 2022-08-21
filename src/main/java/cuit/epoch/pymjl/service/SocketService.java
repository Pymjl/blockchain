package cuit.epoch.pymjl.service;

import org.java_websocket.WebSocket;

import java.util.List;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/21 15:36
 **/
public interface SocketService {
    /**
     * 客户端和服务端共用的消息处理方法
     *
     * @param webSocket 网络套接字
     * @param msg       味精
     * @param sockets   套接字
     */
    void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets);

    /**
     * 写
     *
     * @param ws      ws
     * @param message 消息
     */
    void write(WebSocket ws, String message);

    /**
     * 查询最新块味精
     *
     * @return {@code String}
     */
    String queryLatestBlockMsg();

    /**
     * 获取套接字
     *
     * @return {@code List<WebSocket>}
     */
    List<WebSocket> getSockets();

    /**
     * 广播
     *
     * @param message 消息
     */
    void broadcast(String message);
}
