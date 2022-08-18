package cuit.epoch.pymjl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/18 17:03
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block implements Serializable {
    @Serial
    private static final long serialVersionUID = 1660814857334L;

    /**
     * 区块链索引号（区块链高度）
     */
    private int index;

    /**
     * 当前区块的hash值,区块唯一标识
     */
    private String hash;

    /**
     * 前一个区块的hash值
     */
    private String previousHash;

    /**
     * 生成区块的时间戳
     */
    private long timestamp;

    /**
     * 工作量证明，计算正确hash值的次数
     */
    private int nonce;

    /**
     * 当前区块存储的业务数据集合（例如转账交易信息、票据信息、合同信息等）
     */
    private List<Transaction> transactions;

}
