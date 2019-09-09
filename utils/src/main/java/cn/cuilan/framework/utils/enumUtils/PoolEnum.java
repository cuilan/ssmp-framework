package cn.cuilan.framework.utils.enumUtils;

import lombok.Getter;

@Getter
public enum PoolEnum {

    news(0, "新榜"),
    hot(1, "热榜"),
    audit(2, "审核榜单"),
    // pool = 3 不存在
    discovery(4, "发现榜单"),
    category_news(5, "分类新闻"),
    recommend(6, "推荐"),
    weixin(7, "微信"),
    man(8, "人类发布");

    private int code;
    private String desc;

    PoolEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
