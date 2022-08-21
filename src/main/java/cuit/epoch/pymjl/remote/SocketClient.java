package cuit.epoch.pymjl.remote;

import cuit.epoch.pymjl.service.SocketService;
import cuit.epoch.pymjl.service.impl.SocketServiceImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/21 15:04
 **/
@Service
public class SocketClient {
    @Resource
    SocketService socketService;

    public void connectToPeer(String addr) {
        try {
            final WebSocketClient socketClient = new WebSocketClient(new URI(addr)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    //客户端发送请求，查询最新区块
                    socketService.write(this, socketService.queryLatestBlockMsg());
                    socketService.getSockets().add(this);
                }

                /**
                 * 接收到消息时触发
                 * @param msg
                 */
                @Override
                public void onMessage(String msg) {
                    socketService.handleMessage(this, msg, socketService.getSockets());
                }

                @Override
                public void onClose(int i, String msg, boolean b) {
                    socketService.getSockets().remove(this);
                    System.out.println("connection closed");
                }

                @Override
                public void onError(Exception e) {
                    socketService.getSockets().remove(this);
                    System.out.println("connection failed");
                }
            };
            socketClient.connect();
        } catch (URISyntaxException e) {
            System.out.println("p2p connect is error:" + e.getMessage());
        }
    }
}
