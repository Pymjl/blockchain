package cuit.epoch.pymjl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/21 15:17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     */
    private int type;
    /**
     * 消息内容
     */
    private String data;

    public Message(int type) {
        this.type = type;
    }
}
