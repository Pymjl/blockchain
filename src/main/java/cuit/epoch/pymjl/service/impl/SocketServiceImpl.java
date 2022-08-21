package cuit.epoch.pymjl.service.impl;

import com.alibaba.fastjson.JSON;
import cuit.epoch.pymjl.constant.BlockConstant;
import cuit.epoch.pymjl.dao.BlockCache;
import cuit.epoch.pymjl.entity.Block;
import cuit.epoch.pymjl.entity.Message;
import cuit.epoch.pymjl.remote.SocketClient;
import cuit.epoch.pymjl.remote.SocketServer;
import cuit.epoch.pymjl.service.BlockService;
import cuit.epoch.pymjl.service.SocketService;
import org.java_websocket.WebSocket;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/21 15:03
 **/
@Service
public class SocketServiceImpl implements ApplicationRunner, SocketService {
    @Resource
    BlockService blockService;
    @Resource
    BlockCache blockCache;
    @Resource
    SocketServer socketServer;
    @Resource
    SocketClient socketClient;

    /**
     * 客户端和服务端共用的消息处理方法
     *
     * @param webSocket 网络套接字
     * @param msg       味精
     * @param sockets   套接字
     */
    @Override
    public void handleMessage(WebSocket webSocket, String msg, List<WebSocket> sockets) {
        try {
            Message message = JSON.parseObject(msg, Message.class);
            System.out.println("接收到IP地址为：" + webSocket.getRemoteSocketAddress().getAddress().toString()
                    + "，端口号为：" + webSocket.getRemoteSocketAddress().getPort() + "的p2p消息："
                    + JSON.toJSONString(message));
            switch (message.getType()) {
                //客户端请求查询最新的区块:1
                case BlockConstant.QUERY_LATEST_BLOCK:
                    //服务端调用方法返回最新区块:2
                    write(webSocket, responseLatestBlockMsg());
                    break;
                //接收到服务端返回的最新区块:2
                case BlockConstant.RESPONSE_LATEST_BLOCK:
                    handleBlockResponse(message.getData(), sockets);
                    break;
                //客户端请求查询整个区块链:3
                case BlockConstant.QUERY_BLOCKCHAIN:
                    //服务端调用方法返回最新区块:4
                    write(webSocket, responseBlockChainMsg());
                    break;
                //直接接收到其他节点发送的整条区块链信息:4
                case BlockConstant.RESPONSE_BLOCKCHAIN:
                    handleBlockChainResponse(message.getData(), sockets);
                    break;
                default:
                    throw new RuntimeException("消息类型不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("处理IP地址为：" + webSocket.getRemoteSocketAddress().getAddress().toString()
                    + "，端口号为：" + webSocket.getRemoteSocketAddress().getPort() + "的p2p消息错误:"
                    + e.getMessage());
        }
    }

    /**
     * 处理其它节点发送过来的区块信息
     *
     * @param blockData 数据块
     * @param sockets   套接字
     */
    private synchronized void handleBlockResponse(String blockData, List<WebSocket> sockets) {
        //反序列化得到其它节点的最新区块信息
        Block latestBlockReceived = JSON.parseObject(blockData, Block.class);
        //当前节点的最新区块
        Block latestBlock = blockCache.getLatestBlock();

        if (latestBlockReceived != null) {
            if (latestBlock != null) {
                //如果接收到的区块高度比本地区块高度大的多
                if (latestBlockReceived.getIndex() > latestBlock.getIndex() + 1) {
                    broadcast(queryBlockChainMsg());
                    System.out.println("重新查询所有节点上的整条区块链");
                } else if (latestBlockReceived.getIndex() > latestBlock.getIndex() &&
                        latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
                    if (blockService.addBlock(latestBlockReceived)) {
                        broadcast(responseLatestBlockMsg());
                    }
                    System.out.println("将新接收到的区块加入到本地的区块链");
                }
            } else {
                //如果本地没有区块，则向其他节点请求整条区块链
                broadcast(queryBlockChainMsg());
                System.out.println("重新查询所有节点上的整条区块链");
            }
        }
    }

    /**
     * 处理其它节点发送过来的区块链信息
     *
     * @param blockData 数据块
     * @param sockets   套接字
     */
    private synchronized void handleBlockChainResponse(String blockData, List<WebSocket> sockets) {
        //反序列化得到其它节点的整条区块链信息
        List<Block> receiveBlockchain = JSON.parseArray(blockData, Block.class);
        if (!CollectionUtils.isEmpty(receiveBlockchain) && blockService.isValidChain(receiveBlockchain)) {
            //根据区块索引先对区块进行排序
            receiveBlockchain.sort(Comparator.comparingInt(Block::getIndex));

            //其它节点的最新区块
            Block latestBlockReceived = receiveBlockchain.get(receiveBlockchain.size() - 1);
            //当前节点的最新区块
            Block latestBlock = blockCache.getLatestBlock();

            if (latestBlock == null) {
                //替换本地的区块链
                blockService.replaceChain(receiveBlockchain);
            } else {
                //其它节点区块链如果比当前节点的长，则处理当前节点的区块链
                if (latestBlockReceived.getIndex() > latestBlock.getIndex()) {
                    if (latestBlock.getHash().equals(latestBlockReceived.getPreviousHash())) {
                        if (blockService.addBlock(latestBlockReceived)) {
                            broadcast(responseLatestBlockMsg());
                        }
                        System.out.println("将新接收到的区块加入到本地的区块链");
                    } else {
                        // 用长链替换本地的短链
                        blockService.replaceChain(receiveBlockchain);
                    }
                }
            }
        }
    }

    /**
     * 全网广播消息
     *
     * @param message 消息
     */
    @Override
    public void broadcast(String message) {
        List<WebSocket> socketsList = this.getSockets();
        if (CollectionUtils.isEmpty(socketsList)) {
            System.out.println("=======没有连接的节点======");
            return;
        }
        System.out.println("======全网广播消息开始========");
        for (WebSocket socket : socketsList) {
            this.write(socket, message);
        }
        System.out.println("======全网广播消息结束=======");
    }

    /**
     * 向其它节点发送消息
     *
     * @param ws      套接字
     * @param message 消息
     */
    @Override
    public void write(WebSocket ws, String message) {
        System.out.println("发送给IP地址为：" + ws.getRemoteSocketAddress().getAddress().toString()
                + "，端口号为：" + ws.getRemoteSocketAddress().getPort() + " 的p2p消息:" + message);
        ws.send(message);
    }

    /**
     * 查询整条区块链
     *
     * @return {@code String}
     */
    private String queryBlockChainMsg() {
        return JSON.toJSONString(new Message(BlockConstant.QUERY_BLOCKCHAIN));
    }

    /**
     * 返回整条区块链数据
     *
     * @return {@code String}
     */
    private String responseBlockChainMsg() {
        Message msg = new Message();
        msg.setType(BlockConstant.RESPONSE_BLOCKCHAIN);
        msg.setData(JSON.toJSONString(blockCache.getBlockChain()));
        return JSON.toJSONString(msg);
    }

    /**
     * 查询最新的区块
     *
     * @return {@code String}
     */
    @Override
    public String queryLatestBlockMsg() {
        return JSON.toJSONString(new Message(BlockConstant.QUERY_LATEST_BLOCK));
    }

    /**
     * 返回最新的区块
     *
     * @return {@code String}
     */
    private String responseLatestBlockMsg() {
        Message msg = new Message();
        msg.setType(BlockConstant.RESPONSE_LATEST_BLOCK);
        Block b = blockCache.getLatestBlock();
        msg.setData(JSON.toJSONString(b));
        return JSON.toJSONString(msg);
    }

    @Override
    public List<WebSocket> getSockets() {
        return blockCache.getSocketsList();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        socketServer.initServer(blockCache.getPort());
        Thread.sleep(3000);
        socketClient.connectToPeer(blockCache.getAddress());
        System.out.println("难度系数=[" + blockCache.getDifficulty() + "]");
        System.out.println("端口号=[" + blockCache.getPort() + "]");
        System.out.println("节点地址=[" + blockCache.getAddress() + "]");
    }
}
