package cuit.epoch.pymjl.service;

import cuit.epoch.pymjl.entity.Block;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/19 0:04
 **/
public interface PowService {
    /**
     * 通过挖矿进行工作量证明，实现节点间的共识
     *
     * @return {@code Block} 返回新建的区块
     */
    Block prove();
}
