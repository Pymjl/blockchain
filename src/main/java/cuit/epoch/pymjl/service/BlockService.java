package cuit.epoch.pymjl.service;

import cuit.epoch.pymjl.entity.Block;
import cuit.epoch.pymjl.entity.Transaction;

import java.util.List;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/18 17:18
 **/
public interface BlockService {
    /**
     * 创建创世区块
     *
     * @return {@code String}
     */
    String createGenesisBlock();

    /**
     * 创建新的区块
     *
     * @param nonce        工作量证明
     * @param previousHash 上一个区块的hash散列
     * @param hash         哈希
     * @param blockTxs     当前区块事务集合
     * @return {@code Block}
     */
    Block createNewBlock(int nonce, String previousHash, String hash, List<Transaction> blockTxs);

    /**
     * 获取当前区块链的集合
     *
     * @return {@code List<Block>}
     */
    List<Block> getBlockChain();
}
