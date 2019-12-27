package cn.cuilan.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * 性别
 */
public enum Gender implements IEnum<Integer> {

    // 男
    MAN(0),

    // 女
    WOMAN(1);

    private Integer value;

    Gender(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
