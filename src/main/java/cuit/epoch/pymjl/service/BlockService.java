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

    /**
     * 校验哈希合法性，粗略的定位前四位为0的情况合法
     *
     * @param hash 哈希
     * @return boolean
     */
    boolean isValidHash(String hash);

    /**
     * 添加区块到区块链中
     *
     * @param newBlock 待添加的区块
     * @return boolean
     */
    boolean addBlock(Block newBlock);

    /**
     * 验证整个区块链是否有效
     *
     * @param chain 链
     * @return boolean
     */
    boolean isValidChain(List<Block> chain);

    /**
     * 替换本地区块链
     *
     * @param newBlocks 最新的区块链
     */
    void replaceChain(List<Block> newBlocks);
}
