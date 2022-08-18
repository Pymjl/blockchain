package cuit.epoch.pymjl.service.impl;


import cn.hutool.json.JSONUtil;
import cuit.epoch.pymjl.dao.BlockCache;
import cuit.epoch.pymjl.entity.Block;
import cuit.epoch.pymjl.entity.Transaction;
import cuit.epoch.pymjl.service.BlockService;
import cuit.epoch.pymjl.utils.CryptoUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/18 17:08
 **/
@Service
public class BlockServiceImpl implements BlockService {

    @Resource
    BlockCache blockCache;

    @Override
    public List<Block> getBlockChain() {
        return blockCache.getBlockChain();
    }

    @Override
    public String createGenesisBlock() {
        Block genesisBlock = new Block();
        //设置创世区块高度为1
        genesisBlock.setIndex(1);
        genesisBlock.setTimestamp(System.currentTimeMillis());
        genesisBlock.setNonce(1);

        //封装业务数据
        List<Transaction> tsaList = new ArrayList<Transaction>();
        Transaction tsa = new Transaction();
        tsa.setId(1);
        tsa.setInfo("这是创世区块");
        tsaList.add(tsa);
        Transaction tsa2 = new Transaction();
        tsa2.setId(2);
        tsa2.setInfo("区块链高度为=1");
        tsaList.add(tsa2);
        genesisBlock.setTransactions(tsaList);

        //设置创世区块的hash值
        genesisBlock.setHash(CryptoUtil.calculateHash("",
                genesisBlock.getTransactions(), genesisBlock.getNonce()));

        //添加到已打包保存的业务数据集合中
        blockCache.getPackedTransactions().addAll(tsaList);

        //添加到区块链中
        blockCache.getBlockChain().add(genesisBlock);
        return JSONUtil.toJsonPrettyStr(genesisBlock);
    }

    @Override
    public Block createNewBlock(int nonce, String previousHash, String hash, List<Transaction> blockTxs) {
        Block block = new Block();
        block.setIndex(blockCache.getBlockChain().size() + 1);
        //时间戳
        block.setTimestamp(System.currentTimeMillis());
        block.setTransactions(blockTxs);
        //工作量证明，计算正确hash值的次数
        block.setNonce(nonce);
        //上一区块的哈希
        block.setPreviousHash(previousHash);
        //当前区块的哈希
        block.setHash(hash);
        if (addBlock(block)) {
            return block;
        }
        return null;
    }

    /**
     * 添加区块到区块链中
     *
     * @param newBlock 待添加的区块
     * @return boolean
     */
    private boolean addBlock(Block newBlock) {
        //先对新区块的合法性进行校验
        if (isValidNewBlock(newBlock, blockCache.getLatestBlock())) {
            blockCache.getBlockChain().add(newBlock);
            // 新区块的业务数据需要加入到已打包的交易集合里去
            blockCache.getPackedTransactions().addAll(newBlock.getTransactions());
            return true;
        }
        return false;
    }

    /**
     * 判断新区块是否合法
     *
     * @param newBlock      新块
     * @param previousBlock 区块链中最后一个区块
     * @return boolean
     */
    private boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (!previousBlock.getHash().equals(newBlock.getPreviousHash())) {
            throw new RuntimeException("新区块的前一个区块hash验证不通过");
        } else {
            // 验证新区块hash值的正确性
            String hash = CryptoUtil.calculateHash(newBlock.getPreviousHash(), newBlock.getTransactions(), newBlock.getNonce());
            if (!hash.equals(newBlock.getHash())) {
                throw new RuntimeException("新区块的hash无效: " + hash + " " + newBlock.getHash());
            }
            if (!isValidHash(newBlock.getHash())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 验证hash值是否满足系统条件
     *
     * @param hash 哈希
     * @return boolean
     */
    private boolean isValidHash(String hash) {
        return hash.startsWith("0000");
    }

    /**
     * 验证整个区块链是否有效
     *
     * @param chain 链
     * @return boolean
     */
    private boolean isValidChain(List<Block> chain) {
        Block block = null;
        Block lastBlock = chain.get(0);
        int currentIndex = 1;
        while (currentIndex < chain.size()) {
            block = chain.get(currentIndex);

            if (!isValidNewBlock(block, lastBlock)) {
                return false;
            }

            lastBlock = block;
            currentIndex++;
        }
        return true;
    }
}
