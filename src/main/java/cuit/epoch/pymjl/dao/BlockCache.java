package cuit.epoch.pymjl.dao;

import cuit.epoch.pymjl.entity.Block;
import cuit.epoch.pymjl.entity.Transaction;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.http.WebSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/18 17:16
 **/
@Data
@Component
@ConfigurationProperties(prefix = "block")
public class BlockCache {
    /**
     * 当前节点的区块链结构，为了简化操作，仅放在本地缓存中，实际区块链是需要做持久化的
     */
    private List<Block> blockChain = new CopyOnWriteArrayList<>();

    /**
     * 已打包保存的业务数据集合
     */
    private List<Transaction> packedTransactions = new CopyOnWriteArrayList<>();

    /**
     * 当前节点的socket对象
     */
    private List<WebSocket> socketsList = new CopyOnWriteArrayList<>();

    /**
     * 获取最新的区块，即当前链上最后一个区块
     *
     * @return {@code Block}
     */
    public Block getLatestBlock() {
        return blockChain.size() > 0 ? blockChain.get(blockChain.size() - 1) : null;
    }

    /**
     * 挖矿的难度系数
     */
    @Value("${block.difficulty}")
    private int difficulty;

    /**
     * 当前节点p2p server端口号
     */
    @Value("${block.port}")
    private int port;

    /**
     * 要连接的节点地址
     */
    @Value("${block.address}")
    private String address;


}
