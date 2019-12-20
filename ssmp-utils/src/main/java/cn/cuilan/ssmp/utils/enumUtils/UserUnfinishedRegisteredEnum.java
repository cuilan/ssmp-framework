package cn.cuilan.ssmp.utils.enumUtils;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Jag 2018/11/29 17:29
 */
@Getter
// 非0为真 1为真(TRUE), 0为假(FALSE)
public enum UserUnfinishedRegisteredEnum {
    finished(0, "0", "完成了"),
    unfinished(1, "1", "未完成"),;

    private int code;
    private String value;
    private String desc;

    UserUnfinishedRegisteredEnum(int code, String value, String desc) {
        this.code = code;
        this.value = value;
        this.desc = desc;
    }
}
