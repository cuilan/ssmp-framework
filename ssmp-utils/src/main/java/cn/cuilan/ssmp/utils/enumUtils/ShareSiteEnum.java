package cn.cuilan.ssmp.utils.enumUtils;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Jag 2019-06-21 10:55
 */
@Getter
public enum ShareSiteEnum {

    xl(1, "新浪微博"),
    dou(2, "豆瓣"),
    qq(3, "QQ空间"),
    we(4, "腾讯微博");

    Integer value;
    String desc;

    ShareSiteEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
