package cuit.epoch.pymjl.remote;

import cuit.epoch.pymjl.service.SocketService;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/21 15:00
 **/
@Component
public class SocketServer {
    @Resource
    SocketService socketService;

    public void initServer(int port) {
        WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
            /**
             * 连接建立后触发
             */
            @Override
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                socketService.getSockets().add(webSocket);
            }

            /**
             * 连接关闭后触发
             */
            @Override
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                socketService.getSockets().remove(webSocket);
                System.out.println("connection closed to address:" + webSocket.getRemoteSocketAddress());
            }

            /**
             * 接收到客户端消息时触发
             */
            @Override
            public void onMessage(WebSocket webSocket, String msg) {
                //作为服务端，业务逻辑处理
                socketService.handleMessage(webSocket, msg, socketService.getSockets());
            }

            /**
             * 发生错误时触发
             */
            @Override
            public void onError(WebSocket webSocket, Exception e) {
                socketService.getSockets().remove(webSocket);
                System.out.println("connection failed to address:" + webSocket.getRemoteSocketAddress());
            }

            @Override
            public void onStart() {

            }

        };
        socketServer.start();
        System.out.println("listening websocket p2p port on: " + port);
    }

}
