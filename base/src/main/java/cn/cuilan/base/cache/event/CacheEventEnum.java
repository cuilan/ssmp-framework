package cn.cuilan.base.cache.event;

public enum CacheEventEnum {

    // 本地请求
    LOCAL_REQ("本地请求"),

    // 本地命中
    LOCAL_HIT("本地命中"),

    // 远程请求
    REMOTE_REQ("远程请求"),

    // 远程命中
    REMOTE_HIT("远程命中"),

    // 防穿透
    NULL_HIT("防穿透"),

    // 重建次数
    DB_LOAD("重建次数"),

    // 重建累计ms
    DB_TIME("重建累计ms");

    private String desc;

    CacheEventEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
