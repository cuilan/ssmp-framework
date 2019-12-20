package cn.cuilan.ssmp.utils.enumUtils;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum SubjectEnum {
    //
    news(1, "42区", "news"),//一个月大约数量 70000
    //
    scoff(2, "段子", "scoff"),//120
    //
    //rumor(3, "谣言"),
    //
    pic(4, "图片", "pic"),//1600
    //
    //pub(5, "公共场合不宜"),
    //
    tec(100, "挨踢1024", "tec"),//15000
    //
    ask(151, "你问我答", "ask"),//120
    //
    video(177, "视频", "video"),//
    section(99, "话题", "section");//

    public Integer value;
    private String desc;
    private String name;

    SubjectEnum(Integer value, String desc, String name) {
        this.value = value;
        this.desc = desc;
        this.name = name;
    }

    public static String getDescById(Integer subjectId) {
        SubjectEnum[] values = SubjectEnum.values();
        for (SubjectEnum se : values) {
            if (se.value.equals(subjectId)) {
                return se.desc;
            }
        }
        return "";
    }

    public static String getNameById(Integer subjectId) {
        SubjectEnum[] values = SubjectEnum.values();
        for (SubjectEnum se : values) {
            if (se.value.equals(subjectId)) {
                return se.name;
            }
        }
        return "";
    }

    public SubjectEnum get(Integer value) {
        return this.get(value, null);
    }

    public SubjectEnum get(Integer value, SubjectEnum defaultEnum) {
        for (SubjectEnum ae : SubjectEnum.values()) {
            if (ae.value.equals(value)) {
                return ae;
            }
        }
        return defaultEnum;
    }

    public static SubjectEnum getSubject(String subjectName) {
        if (StringUtils.isBlank(subjectName)) {
            return null;
        }
        try {
            return SubjectEnum.valueOf(subjectName);
        } catch (Exception e) {
            return null;
        }
    }
}
