package cuit.pymjl.epoch;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import cuit.epoch.pymjl.entity.Transaction;
import cuit.epoch.pymjl.utils.CryptoUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/18 17:11
 **/
public class Context {
    @Test
    void testEncrypt() {
        String str = "hello world";
        System.out.println(CryptoUtil.encrypt(str));
    }

    @Test
    void getTimeStamp() {
        System.out.println(System.currentTimeMillis());
    }

    @Test
    void testHashUtils() {
        System.out.println(DigestUtil.sha256Hex("hello world"));
        System.out.println(CryptoUtil.isValid("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9", "hello world"));
    }

    @Test
    void testJson() {
        System.out.println(JSONUtil.toJsonPrettyStr(new Transaction(1, "hello world")));
    }
}
