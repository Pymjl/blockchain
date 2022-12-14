package cuit.epoch.pymjl.controller;

import cn.hutool.json.JSONUtil;
import cuit.epoch.pymjl.service.BlockService;
import cuit.epoch.pymjl.service.PowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/18 18:39
 **/
@RestController
public class BlockChainController {
    @Autowired
    private BlockService blockService;
    @Autowired
    private PowService powService;

    @GetMapping("/list")
    public String scanBlocks() {
        return JSONUtil.toJsonPrettyStr(blockService.getBlockChain());
    }

    @PostMapping("/create")
    public String createBlock() {
        return blockService.createGenesisBlock();
    }

    @GetMapping("/prove")
    public String prove() {
        return JSONUtil.toJsonPrettyStr(powService.prove());
    }
}
