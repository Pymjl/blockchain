package cuit.epoch.pymjl.utils;


import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import cuit.epoch.pymjl.entity.Transaction;

import java.util.Collection;
import java.util.List;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/18 17:08
 **/
public class CryptoUtil {
    /**
     * 盐
     */
    private static final String SALT = "SHA-256";

    /**
     * 加密,使用SHA256摘要算法
     *
     * @param hash 哈希
     * @return {@code String}
     */
    public static String encrypt(String hash) {
        return DigestUtil.sha256Hex(SALT + hash);
    }

    public static boolean isValid(String encrypted, String hash) {
        return encrypted.equals(encrypt(hash));
    }

    /**
     * 计算区块的哈希
     *
     * @param previousHash 上一个区块的Hash散列
     * @param data         当前区块的数据
     * @param nonce        工作量证明
     * @return {@code String}
     */
    public static String calculateHash(String previousHash, Collection<?> data, Integer nonce) {
        return encrypt(previousHash + JSONUtil.toJsonPrettyStr(data) + nonce);
    }
}
