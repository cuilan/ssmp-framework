package cn.cuilan.ssmp.entity;

import cn.cuilan.ssmp.annotation.RedisCached;
import cn.cuilan.ssmp.common.BaseIdTimeEntity;
import cn.cuilan.ssmp.enums.Gender;
import cn.cuilan.ssmp.redis.EntityRedisPrefix;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@RedisCached(EntityRedisPrefix.USER)
@TableName("t_user")
public class User extends BaseIdTimeEntity<Long> {

    private String username;

    private String password;

    private String realName;

    // 0-男，1-女
    private Gender gender;

    private int age;

}
