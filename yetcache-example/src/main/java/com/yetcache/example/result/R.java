package com.yetcache.example.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R<T> {
    private Integer code;

    private String msg;

    private T data;

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("操作成功");
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail(String message) {
        R<T> r = new R<>();
        r.setCode(-1);
        r.setMsg(message);
        return r;
    }

    public static <T> R<T> fail() {
        R<T> r = new R<>();
        r.setCode(-1);
        r.setMsg("操作失败");
        return r;
    }
}
