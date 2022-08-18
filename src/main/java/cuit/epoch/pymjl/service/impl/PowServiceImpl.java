package cuit.epoch.pymjl.service.impl;

import cuit.epoch.pymjl.dao.BlockCache;
import cuit.epoch.pymjl.entity.Block;
import cuit.epoch.pymjl.entity.Transaction;
import cuit.epoch.pymjl.service.BlockService;
import cuit.epoch.pymjl.service.PowService;
import cuit.epoch.pymjl.utils.CommonUtil;
import cuit.epoch.pymjl.utils.CryptoUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/19 0:05
 **/
@Service
public class PowServiceImpl implements PowService {
    @Resource
    BlockCache blockCache;
    @Resource
    BlockService blockService;

    @Override
    public Block prove() {
        // 封装业务数据集合，记录区块产生的节点信息，临时硬编码实现
        List<Transaction> tsaList = new ArrayList<>();
        Transaction tsa1 = new Transaction();
        tsa1.setId(1);
        tsa1.setInfo("这是IP为：" + CommonUtil.getLocalIp() + "，端口号为：" + blockCache.getPort() + "的节点挖矿生成的区块");
        tsaList.add(tsa1);
        Transaction tsa2 = new Transaction();
        tsa2.setId(2);
        tsa2.setInfo("区块链高度为：" + (blockCache.getLatestBlock().getIndex() + 1));
        tsaList.add(tsa2);

        // 定义每次哈希函数的结果
        String newBlockHash = "";
        int nonce = 0;
        long start = System.currentTimeMillis();
        System.out.println("开始挖矿...");
        while (true) {
            // 计算新区块hash值
            newBlockHash = CryptoUtil.calculateHash(blockCache.getLatestBlock().getHash(), tsaList, nonce);
            // 校验hash值
            if (blockService.isValidHash(newBlockHash)) {
                System.out.println("挖矿完成，正确的hash值：" + newBlockHash);
                System.out.println("挖矿耗费时间：" + (System.currentTimeMillis() - start) + "ms");
                break;
            }
            System.out.println("第" + (nonce + 1) + "次尝试计算的hash值：" + newBlockHash);
            nonce++;
        }
        // 创建新的区块
        Block block = blockService.createNewBlock(nonce, blockCache.getLatestBlock().getHash(), newBlockHash, tsaList);
        return block;
    }
}
