package cn.qulei.oa.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description:      封装一些状态
 ** @Author:      QuLei
 * @CreateDate:   2019-07-09 00:12
 * @Version:      1.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RespState {
    private int code; // 封装状态码
    private String msg; //状态信息
    private String data; //json格式的数据流

    public static RespState build(int i) {
        return new RespState(i,"OK","meiyou");
    }

    public static RespState build(int i, String msg) {
        return new RespState(i,msg,"meiyou");
    }

}
