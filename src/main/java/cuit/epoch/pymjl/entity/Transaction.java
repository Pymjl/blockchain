package cuit.epoch.pymjl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Pymjl
 * @version 1.0
 * @date 2022/8/18 17:26
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Serializable {
    @Serial
    private static final long serialVersionUID = 1660814857376L;
    /**
     * id
     */
    private Integer id;

    /**
     * 信息
     */
    private String info;
}
