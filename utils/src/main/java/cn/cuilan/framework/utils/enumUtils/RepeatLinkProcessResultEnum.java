package cn.cuilan.framework.utils.enumUtils;

import lombok.Getter;


/**
 * 人工 判断是否重复
 */
@Getter
public enum RepeatLinkProcessResultEnum {

    unprocessed(0, "未处理"),
    processed_repeat(1, "已处理且重复"),
    processed_no_repeat(2, "已处理未重复"),
    processed_content_best(3, "已处理本文最佳");


    private int code;
    private String desc;

    RepeatLinkProcessResultEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
