package cn.cuilan.ssmp.redis;

/**
 * Redis数据类型
 *
 * @author zhang.yan
 */
public enum RedisDataType {

    // key-value
    KV,

    // hash
    HASH,

    // list
    LIST,

    // set
    SET,

    // sorted-set
    ZSET;
}
